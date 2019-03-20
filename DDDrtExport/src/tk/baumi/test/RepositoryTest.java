package tk.baumi.test;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import oracle.sql.CLOB;
import tk.baumi.ddd.Entity;

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

	public <T extends Entity> void insert(T databaseInsert) {
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
				if ("CLOB".equals(column.type)) {
					Clob clob = connection.createClob();
					clob.setString(1, values[i].toString());
					statement.setClob(i + 1, clob);
				} else {
					if (Entity.class.isAssignableFrom(column.javaType)) {
						Entity entity2 = (Entity) values[i];
						Object id = loadRelationEntityID(column, entity2);
						statement.setString(i + 1, id.toString());
					} else {
						statement.setString(i + 1, values[i].toString());
					}
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
		Entity selection2 = selectByID((Class<Entity>) column.javaType, id);
		if (selection2 != null) {
			// OK because the relation exists. Probably an update
			update(entity2);
		} else {
			insert(entity2);
		}
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
					Class<?> type = columns.get(i).javaType;
					String stringValue = result.getString(i + 1);
					Object convertedType = convertToJavaType(type, stringValue);
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
					Class<?> type = columns.get(i).javaType;
					String stringValue = result.getString(i + 1);
					Object convertedType = convertToJavaType(type, stringValue);
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

	private Object convertToJavaType(Class<?> type, String stringValue) {
		Object convertedType;
		if ((UUID.class).equals(type)) {
			convertedType = UUID.fromString(stringValue);
		} else if (Entity.class.isAssignableFrom(type)) {
			convertedType = selectByID((Class<Entity>) type, stringValue);
		} else {
			convertedType = stringValue;
		}
		return convertedType;
	}

	public <T extends Entity> int update(T databaseUpdate) {
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

		PreparedStatement st;
		try {
			st = connection.prepareStatement(stringBuffer.toString());
			int index = 1;
			int objectIndex = 0;
			Object[] objects = databaseUpdate.properties();
			String primaryValue = null;
			for (Column column : columns) {
				if (!column.primary) {
					Object currObj = objects[objectIndex];
					Object setting = null;
					if(Entity.class.isAssignableFrom(currObj.getClass())) {
						Entity entity2 = (Entity) currObj;
						setting = loadRelationEntityID(column, entity2);
					} else {
						setting = currObj;
					}
					if(setting instanceof java.util.UUID) {
						setting = setting.toString();
					}
					st.setObject(index, setting);
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
			if (field.isAnnotationPresent(DDDProperty.class)) {
				DDDProperty property = field.getAnnotation(DDDProperty.class);
				String columnName = property.columnName();
				String columnType = property.columnType();
				columns.add(new Column(columnName, columnType, field.getType(), property.primaryKey()));
				if (field.getType().isAssignableFrom(Entity.class)) {
					Reference reference = new Reference();
					reference.referencingEntity = (Class<Entity>) field.getType();
				}
			}
		}
		return columns;
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
		Reference reference;

		public Column(String name, String type, Class<?> javaType, boolean primary) {
			super();
			this.name = name;
			this.type = type;
			this.javaType = javaType;
			this.primary = primary;
		}
	}

	class Reference {
		Class<Entity> referencingEntity;
		boolean multi;
	}
}
