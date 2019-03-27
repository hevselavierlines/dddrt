package tk.baumi.test;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;

import oracle.sql.CLOB;
import tk.baumi.ddd.Entity;
import tk.baumi.ddd.ValueObject;

public class RepositoryTest {
	private Connection connection;

	public RepositoryTest(String connectionString, String username, String password) {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection(connectionString, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public <T extends Entity> boolean checkIDExists(T idEntity) {
		List<Column> columns =retrieveColumns(idEntity.getClass());
		Object id = null;
		Object[] objects = idEntity.properties();
		for(int i = 0; i < columns.size() && id == null; i++) {
			Column column = columns.get(i);
			if(column.primary) {
				id = objects[i];
			}
		}
		if(id != null) {
			Entity entity = selectByID(idEntity.getClass(), id);
			return entity != null;
		} else {
			return true;
		}
	}
	
	public <T extends Entity> void update(T databaseUpdate) {
		if(checkIDExists(databaseUpdate)) {
			internalUpdate(databaseUpdate);
		} else {
			internalInsert(databaseUpdate);
		}
	}

	public <T extends Entity> void internalInsert(T databaseInsert) {
		Class<?> databaseClass = databaseInsert.getClass();
		StringBuffer query = new StringBuffer("INSERT INTO ");
		String tableName = retrieveTableName(databaseClass);
		query.append(tableName);
		query.append(" (");
		List<Column> columns = retrieveColumns(databaseClass);
		for (Column column : columns) {
			query.append(column.name).append(',');
		}
		query.deleteCharAt(query.length() - 1);
		query.append(')');

		query.append(" VALUES (");
		Object[] values = databaseInsert.properties();
		convertToDatabaseClob(values, columns);
		for (int i = 0; i < values.length; i++) {
			query.append("?,");
		}
		query.deleteCharAt(query.length() - 1);
		query.append(")");

		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query.toString());
			for (int i = 0; i < columns.size(); i++) {
				Column column = columns.get(i);
				if(values[i] != null) {
					if ("CLOB".equals(column.type)) {
						Clob clob = connection.createClob();
						clob.setString(1, values[i].toString());
						statement.setClob(i + 1, clob);
					} else {
						statement.setString(i + 1, values[i].toString());
					}
				} else {
					statement.setString(i + 1, null);
				}
			}
			statement.executeUpdate();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Object loadRelationEntityID(Column column, Entity entity2) {
		List<Column> propertyColumns = retrieveColumns(entity2.getClass());
		Object id = null;
		for (int j = 0; j < entity2.properties().length && id == null; j++) {
			Column column2 = propertyColumns.get(j);
			if (column2.primary) {
				id = entity2.properties()[j];
			}
		}
		update(entity2);
		return id;
	}

	public <T extends Entity> T selectByID(Class<T> databaseClass, Object id) {
		StringBuffer query = new StringBuffer("SELECT ");
		String tableName = retrieveTableName(databaseClass);

		List<Column> columns = retrieveColumns(databaseClass);
		Column idColumn = null;
		for (Column column : columns) {
			query.append(column.name.replaceAll("\'", "").replaceAll("\"", "")).append(",");
			if (column.primary) {
				idColumn = column;
			}
		}
		query.deleteCharAt(query.length() - 1);
		query.append(" FROM ");
		query.append(tableName);
		query.append(" WHERE ").append(idColumn.name).append("=?");

		PreparedStatement prepStatement = null;
		ResultSet result = null;
		T ret = null;
		try {
			prepStatement = connection.prepareStatement(query.toString());
			if (id instanceof java.util.UUID) {
				id = ((java.util.UUID) id).toString();
			}
			prepStatement.setObject(1, id);
			result = prepStatement.executeQuery();
			if (result.next()) {
				Object[] elements = new Object[columns.size()];
				for (int i = 0; i < elements.length; i++) {
					Column column = columns.get(i);
					String stringValue = result.getString(i + 1);
					Object convertedType = convertToJavaType(column, stringValue);
					elements[i] = convertedType;
				}
				try {
					Entity entity = (Entity) databaseClass.newInstance();
					entity.insert(elements);
					ret = (T) entity;
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
					e.printStackTrace();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public <T extends Entity> List<T> selectAll(Class<T> databaseClass) {
		StringBuffer query = new StringBuffer("SELECT ");
		String tableName = retrieveTableName(databaseClass);

		List<Column> columns = retrieveColumns(databaseClass);
		Column idColumn = null;
		for (Column column : columns) {
			query.append(column.name.replaceAll("\'", "").replaceAll("\"", "")).append(",");
			if (column.primary) {
				idColumn = column;
			}
		}
		query.deleteCharAt(query.length() - 1);
		query.append(" FROM ");
		query.append(tableName);
		Statement statement = null;
		List<T> ret = new LinkedList<T>();
		try {
			statement = connection.createStatement();
			ResultSet result = statement.executeQuery(query.toString());
			while (result.next()) {
				Object[] elements = new Object[columns.size()];
				for (int i = 0; i < elements.length; i++) {
					Column column = columns.get(i);
					String stringValue = result.getString(i + 1);
					Object convertedType = convertToJavaType(column, stringValue);
					elements[i] = convertedType;
				}
				try {
					Entity entity = (Entity) databaseClass.newInstance();
					entity.insert(elements);
					ret.add((T) entity);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return ret;
	}

	private Object convertToJavaType(Column column, String stringValue) {
		Object convertedType = null;
		Class<?> type = column.javaType;
		if(stringValue != null) {
			if ((UUID.class).equals(type)) {
				convertedType = UUID.fromString(stringValue);
			} else if (Entity.class.isAssignableFrom(type)) {
				convertedType = selectByID((Class<Entity>) type, stringValue);
			} else if(ValueObject.class.isAssignableFrom(type)) {
				try {
					ValueObject vo = (ValueObject) type.newInstance();
					JSONArray json = new JSONArray(stringValue);
					Object[] ret = new Object[json.length()];
					Field[] fields = type.getDeclaredFields();
					for(int i = 0; i < json.length(); i++) {
						Object obj = json.get(i);
						Field field = fields[i];
						if(obj != null) {
							ret[i] = convertToJavaType(retrieveColumn(field), obj.toString());
						}
					}
					vo.insert(ret);
					convertedType = vo;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(type == char.class || type == Character.class) {
				if(stringValue.length() >= 1) {
					convertedType = stringValue.charAt(0);
				}
			} else if(type == byte.class || type == Byte.class) {
				convertedType = Byte.parseByte(stringValue);
			} else if(type == short.class || type == Short.class) {
				convertedType = Short.parseShort(stringValue);
			} else if(type == int.class || type == Integer.class) {
				convertedType = Integer.parseInt(stringValue);
			} else if(type == long.class || type == Long.class) {
				convertedType = Long.parseLong(stringValue);
			} else if(type == float.class || type == Float.class) {
				convertedType = Float.parseFloat(stringValue);
			} else if(type == double.class || type == Double.class) {
				convertedType = Double.parseDouble(stringValue.replace(',', '.'));
			} else if(type == Date.class) {
				long dateLong = Long.parseLong(stringValue);
				convertedType = new Date(dateLong);
			} else if(type == List.class && column.field != null) {
				Type genericFieldType = column.field.getGenericType();
				Class<?> listType = null;
				if(genericFieldType instanceof ParameterizedType){
				    ParameterizedType aType = (ParameterizedType) genericFieldType;
				    Type[] fieldArgTypes = aType.getActualTypeArguments();
				    for(Type fieldArgType : fieldArgTypes){
				        listType = (Class<?>) fieldArgType;
				    }
				}
				
				if(listType != null) {
					List list = new LinkedList();
					JSONArray jsonArray = new JSONArray(stringValue);
					for(int i = 0; i < jsonArray.length(); i++) {
						Object obj = jsonArray.get(i);
						Column col = new Column(listType);
						Object converted = convertToJavaType(col, obj.toString());
						list.add(converted);
					}
					convertedType = list;
				}
			} else {
				convertedType = stringValue;
			}
		} else {
			convertedType = null;
		}
		return convertedType;
	}

	public <T extends Entity> int internalUpdate(T databaseUpdate) {
		Class<? extends Entity> databaseClass = databaseUpdate.getClass();
		List<Column> columns = this.retrieveColumns(databaseClass);
		Column primaryColumn = null;
		StringBuffer stringBuffer = new StringBuffer();
		/* "UPDATE item SET Name = ?, Size = ?, Price = ?, WHERE ItemCode = ?" */
		String tableName = retrieveTableName(databaseClass).replaceAll("\'", "").replaceAll("\"", "");
		stringBuffer.append("UPDATE ").append(tableName).append(" SET ");

		for (Column column : columns) {
			if (column.primary) {
				primaryColumn = column;
			} else {
				stringBuffer.append(column.name).append("=?,");
			}
		}
		stringBuffer.deleteCharAt(stringBuffer.length() - 1);
		stringBuffer.append(" WHERE ").append(primaryColumn.name).append("=?");

		PreparedStatement st = null;
		try {
			st = connection.prepareStatement(stringBuffer.toString());
			int index = 1;
			int objectIndex = 0;
			Object[] objects = databaseUpdate.properties();
			convertToDatabaseClob(objects, columns);
			String primaryValue = null;
			for (Column column : columns) {
				if (!column.primary) {
					Object currObj = objects[objectIndex];
					Object setting = null;
					if(currObj == null) {
						st.setObject(index, null);
					} else {
//						if(Entity.class.isAssignableFrom(currObj.getClass())) {
//							Entity entity2 = (Entity) currObj;
//							setting = loadRelationEntityID(column, entity2);
//						} else if(ValueObject.class.isAssignableFrom(currObj.getClass())) {
//							ValueObject vo2 = (ValueObject) currObj;
//							JSONArray json = vo2.serialize();
//							setting = json.toString();
//						} else {
//							setting = currObj;
//						}
//						if(setting instanceof java.util.UUID) {
//							setting = setting.toString();
//						}
						st.setObject(index, objects[objectIndex].toString());
					}
					index++;
				} else {
					primaryValue = objects[objectIndex].toString();
				}
				objectIndex++;

			}
			st.setString(index, primaryValue);
			return st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			if(st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public Object convertJavaToDatabase(Object object, Column column) {
		Object newObject = null;
		if(object instanceof java.util.UUID) {
			newObject = object.toString();
		} else if(object instanceof java.util.Date) {
			Date date = (Date)object;
			newObject = date.getTime();
		} else if (column != null && Entity.class.isAssignableFrom(column.javaType)) {
			Entity entity2 = (Entity) object;
			Object id = loadRelationEntityID(column, entity2);
			newObject = id.toString();
		} else if(ValueObject.class.isAssignableFrom(object.getClass())) {
			ValueObject vo2 = (ValueObject) object;
			org.json.JSONArray ret = new org.json.JSONArray();
	        Object[] properties = vo2.properties();
	        convertToDatabaseClob(properties, retrieveColumns(vo2.getClass()));
	        for(Object property : properties) {
	        	ret.put(property);
	        }
			newObject = ret.toString();
		} else if(List.class.isAssignableFrom(object.getClass())) {
//			Type genericFieldType = column.field.getGenericType();
//
//			if(genericFieldType instanceof ParameterizedType){
//			    ParameterizedType aType = (ParameterizedType) genericFieldType;
//			    Type[] fieldArgTypes = aType.getActualTypeArguments();
//			    for(Type fieldArgType : fieldArgTypes){
//			        Class fieldArgClass = (Class) fieldArgType;
//			        System.out.println("fieldArgClass = " + fieldArgClass);
//			    }
//			}
//			List list = (List) object;
//			Object[] properties = new Object[list.size()];
//			for(int j = 0; j < properties.length; i++) {
//				Object property = list.get(j);
//				properties[j] = property;
//			}
			JSONArray jsonArray = new JSONArray();
			List<?> list = (List<?>) object;
			for(Object elem : list) {
				jsonArray.put(convertListToDatabaseClob(elem));
			}
			newObject = jsonArray.toString();
		} else {
			newObject = object;
		}
		return newObject;
	}
	
	public void convertToDatabaseClob(Object[] objects, List<Column> columns) {
		for(int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			Column column = null;
			if(columns != null) {
				column = columns.get(i);
			}
			if(object != null) {
				object = convertJavaToDatabase(object, column);
				objects[i] = object;
			}
		}
	}
	
	public Object convertListToDatabaseClob(Object object) {
		Object ret = null;
		if(Entity.class.isAssignableFrom(object.getClass())) {
			Entity entity = (Entity) object;
			ret = convertJavaToDatabase(entity, new Column(object.getClass()));
		} else if(ValueObject.class.isAssignableFrom(object.getClass())) {
			ValueObject entity = (ValueObject) object;
			List<Column> columns = retrieveColumns(object.getClass());
			Object[] properties = entity.properties();
			convertToDatabaseClob(properties, columns);
			JSONArray json = new JSONArray();
			for(int i = 0; i < properties.length; i++) {
				json.put(properties[i]);
			}
			ret = json;
		} else {
			ret = object;
		}
		return ret;
	}
	public <T extends Entity> void delete(T databaseDelete) {
		Class<?> databaseClass = databaseDelete.getClass();
		StringBuffer query = new StringBuffer();
		query.append("DELETE FROM ");
		query.append(retrieveTableName(databaseClass).replaceAll("\"", "").replaceAll("\'", ""));
		query.append(" WHERE ");
		int primaryKeyIndex = -1;
		List<Column> columns = retrieveColumns(databaseClass);
		for(int i = 0; i < columns.size() && primaryKeyIndex < 0; i++) {
			Column column = columns.get(i);
			if(column.primary) {
				primaryKeyIndex = i;
			}
		}
		Object[] properties = databaseDelete.properties();
		convertToDatabaseClob(properties, columns);
		String idText = (String) properties[primaryKeyIndex];
		query.append(columns.get(primaryKeyIndex).name);
		query.append("=?");
		
		try {
			PreparedStatement statement = connection.prepareStatement(query.toString());
			statement.setString(1, idText);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private <T> String retrieveTableName(Class<T> databaseClass) {
		String tableName = null;
		if (databaseClass.isAnnotationPresent(DDDEntity.class)) {
			DDDEntity entity = databaseClass.getAnnotation(DDDEntity.class);
			tableName = entity.tableName();
		}
		return tableName;
	}

	private <T> List<Column> retrieveColumns(Class<T> databaseClass) {
		List<Column> columns = new LinkedList<Column>();

		for (Field field : databaseClass.getDeclaredFields()) {
			columns.add(retrieveColumn(field));
		}
		return columns;
	}
	
	private <T> Column retrieveColumn(Field field) {
		if (field.isAnnotationPresent(DDDProperty.class)) {
			DDDProperty property = field.getAnnotation(DDDProperty.class);
			String columnName = property.columnName();
			String columnType = property.columnType();
			return new Column(columnName, columnType, field, property.primaryKey());
//			if (field.getType().isAssignableFrom(Entity.class)) {
//				Reference reference = new Reference();
//				reference.referencingEntity = (Class<Entity>) field.getType();
//			}
		} else {
			return new Column(field);
		}
	}

	public void disconnect() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	class Column {
		String name;
		String type;
		Class<?> javaType;
		boolean primary;
		Field field;
		
		public Column(Field field) {
			this(null, null, field, false);
		}
		
		public Column(Class<?> javaType) {
			this.javaType = javaType;
		}

		public Column(String name, String type, Field field, boolean primary) {
			super();
			this.name = name;
			this.type = type;
			this.javaType = field.getType();
			this.primary = primary;
			this.field = field;
		}
	}
}
