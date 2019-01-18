package at.mic.dddrt.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import at.mic.dddrt.db.model.ColumnRelation;
import at.mic.dddrt.db.model.Table;
import at.mic.dddrt.db.model.TableColumn;

public class DatabaseImportManager {
	private final Connection connection;
	private final String username;

	public DatabaseImportManager(String connectionString, String username, String password)
			throws ClassNotFoundException, SQLException {
		this.username = username.toUpperCase();
		Class.forName("oracle.jdbc.driver.OracleDriver");
		connection = DriverManager.getConnection(connectionString, username, password);
	}

	public List<Table> loadTables() throws SQLException {
		List<Table> retTables = new LinkedList<Table>();
		PreparedStatement statement;
		statement = connection.prepareStatement("SELECT table_name " +
												"FROM all_tables " +
												"WHERE owner=? " +
												"AND tablespace_name IS NOT NULL " +
												"AND table_name not like \'%$%\' " +
												"ORDER BY TABLE_NAME asc");
		statement.setString(1, username);
		ResultSet rs = statement.executeQuery();
		while (rs.next()) {
			retTables.add(new Table(rs.getString(1)));
		}
		rs.close();
		statement.close();

		return retTables;
	}

	public void loadColumns(Table table) throws SQLException {
		PreparedStatement columnStatement;
		columnStatement = connection.prepareStatement(
				"   SELECT \r\n" +
														"            a.column_name, \r\n" +
														"            a.data_type, \r\n" +
														"            a.data_length, \r\n" +
														"            a.nullable, \r\n" +
														"            CASE WHEN c.constraint_type = 'P' THEN 'Y' ELSE 'N' END as prim\r\n" +
														"   FROM ALL_TAB_COLUMNS a \r\n" +
														"   LEFT OUTER JOIN all_cons_columns b on a.owner = b.owner AND a.table_name = b.table_name AND a.column_name = b.column_name\r\n" +
														"   LEFT OUTER JOIN all_constraints c ON b.owner = c.owner AND b.constraint_name = c.constraint_name\r\n" +
														"   WHERE a.OWNER = ? AND a.TABLE_NAME = ?\r\n" +
														"   ORDER BY a.column_id ASC");
		columnStatement.setString(1, username);
		columnStatement.setString(2, table.getTableName());
		ResultSet columnSet = columnStatement.executeQuery();
		while (columnSet.next()) {
			boolean primaryKey = columnSet.getBoolean(5);
			if (primaryKey) {
				table.setPrimaryKey(true);
			}
			TableColumn column = new TableColumn(columnSet.getString(1), columnSet.getString(2), columnSet.getLong(3),
					columnSet.getBoolean(4), primaryKey);
			table.addColumn(column);
		}
		columnSet.close();
		columnStatement.close();
	}

	public List<ColumnRelation> loadRelations(Table table) throws SQLException {
		PreparedStatement relStatement;
		relStatement = connection.prepareStatement("SELECT " +
													"            a.table_name original_table,  " +
													"            a.column_name original_column,  " +
													"            b.table_name reference_table,  " +
													"            b.column_name reference_column " +
													"FROM  " +
													"            all_cons_columns a " +
													"JOIN all_constraints c ON a.owner = c.owner AND a.constraint_name = c.constraint_name " +
													"JOIN all_cons_columns b on c.owner = b.owner and c.r_constraint_name = b.constraint_name " +
													"WHERE  " +
													"    c.constraint_type = 'R' " +
													"   AND a.owner = ?  " +
													"   AND a.table_name = ?");
		relStatement.setString(1, username);
		relStatement.setString(2, table.getTableName());
		ResultSet relSet = relStatement.executeQuery();
		List<ColumnRelation> relations = new LinkedList<ColumnRelation>();
		while (relSet.next()) {
			// System.out.println("Relations: " + relSet.getString(1) + " with column " + relSet.getString(2)
			// + " references to " + relSet.getString(3) + " with column " + relSet.getString(4));
			relations.add(ColumnRelation.findRelationByName(table, relSet.getString(2), relSet.getString(3), relSet.getString(4)));
		}
		relSet.close();
		relStatement.close();
		return relations;
	}

	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
