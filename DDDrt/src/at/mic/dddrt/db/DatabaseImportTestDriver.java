package at.mic.dddrt.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import at.mic.dddrt.db.model.Table;
import at.mic.dddrt.db.model.TableColumn;

public class DatabaseImportTestDriver {

	public static void main(String[] args) {
		DatabaseImportManager dim = null;
		try {
		    dim = new DatabaseImportManager("jdbc:oracle:thin:@localhost:1521:xe","afaci","afaci");
		    List<Table> tables = dim.loadTables();
		    for(Table table : tables) {
		    	dim.loadColumns(table);
		    }
		    for(Table table : tables) {
		    	dim.loadRelations(table, tables);
		    }
		    for(Table table : tables) {
		    	System.out.println(table);
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(dim != null) {
				dim.close();
			}
		}
		
		
	}

}
