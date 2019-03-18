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

import oracle.sql.CLOB;

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
		for(Column column : columns) {
			query.append(column.name).append(',');
		}
		query.deleteCharAt(query.length() - 1);
		query.append(')');
		
		query.append(" VALUES (");
		Object[] values = databaseInsert.properties();
		for(int i = 0; i < values.length; i++) {
			query.append("?,");
		}
		query.deleteCharAt(query.length() - 1);
		query.append(")");
		
		PreparedStatement statement = null;
		try {
			statement  = connection.prepareStatement(query.toString());
			for(int i = 0; i < columns.size(); i++) {
				Column column = columns.get(i);
				if("CLOB".equals(column.type)) {
					Clob clob = connection.createClob();
					clob.setString(1, values[i].toString());
					statement.setClob(i + 1, clob);
				} else {
					statement.setString(i + 1, values[i].toString());
				}
			}
			statement.executeUpdate();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public <T extends Entity> List<T> selectAll(Class<T> databaseClass) {
		StringBuffer query = new StringBuffer("SELECT ");
		String tableName = retrieveTableName(databaseClass);
		
		List<Column> columns = retrieveColumns(databaseClass);
		for(Column column : columns) {
			query.append(column.name.replaceAll("\'", "").replaceAll("\"", "")).append(",");
		}
		query.deleteCharAt(query.length() - 1);
		query.append(" FROM ");
		query.append(tableName);
		Statement statement = null;
		List<T> ret = new LinkedList<T>();
		try {
			statement = connection.createStatement();
			ResultSet result = statement.executeQuery(query.toString());
			while(result.next()) {
				Object[] elements = new Object[columns.size()];
				for(int i = 0; i < elements.length; i++) {
					elements[i] = result.getString(i + 1);
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
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return ret;
	}
	
	public <T extends Entity> int update(T databaseUpdate) {
		Class<? extends Entity> databaseClass = databaseUpdate.getClass();
		List<Column> columns = this.retrieveColumns(databaseClass);
		Column primaryColumn = null;
		StringBuffer stringBuffer = new StringBuffer();
		/*"UPDATE item SET Name = ?, Size = ?, Price = ?, WHERE ItemCode = ?"*/
		String tableName = retrieveTableName(databaseClass).replaceAll("\'", "").replaceAll("\"", "");
		stringBuffer.append("UPDATE ").append(tableName).append(" SET ");
		
		for(Column column : columns) {
			if(column.primary) {
				primaryColumn = column;
			} else {
				stringBuffer.append(column.name).append("=?,");
			}
		}
		stringBuffer.deleteCharAt(stringBuffer.length() - 1);
		stringBuffer.append(" WHERE " ).append(primaryColumn.name).append("=?");
		
		PreparedStatement st;
		try {
			st = connection.prepareStatement(stringBuffer.toString());
			int index = 1;
			int objectIndex = 0;
			Object[] objects = databaseUpdate.properties();
			String primaryValue = null;
			for(Column column : columns) {
				if(!column.primary) {
					st.setObject(index, objects[objectIndex]);
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
		if(databaseClass.isAnnotationPresent(DDDEntity.class)) {
			DDDEntity entity = databaseClass.getAnnotation(DDDEntity.class);
			tableName = entity.tableName();
		}
		return tableName;
	}

	private <T> List<Column> retrieveColumns(Class<T> databaseClass) {
		List<Column> columns = new LinkedList<Column>();
		
		for(Field field : databaseClass.getDeclaredFields()) {
			if(field.isAnnotationPresent(DDDProperty.class)) {
				DDDProperty property = field.getAnnotation(DDDProperty.class);
				String columnName = property.columnName();
				String columnType = property.columnType();
				columns.add(new Column(columnName, columnType, property.primaryKey()));
			}
		}
		return columns;
	}
	
	public void disconnect() {
		if(connection != null) {
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
		boolean primary;
		public Column(String name, String type, boolean primary) {
			super();
			this.name = name;
			this.type = type;
			this.primary = primary;
		}
	}
}
