package at.mic.dddrt.db.model;

import java.sql.SQLException;
import java.util.List;

public class ColumnRelation {
	private Table referencingTable;
	private TableColumn referencingColumn;
	
	private Table originalTable;
	private TableColumn originalColumn;
	
	public static Table findTableByName(String tableName, List<Table> tables) {
		Table retTable = null;
		for(int i = 0; i < tables.size() && retTable == null; i++) {
			Table table = tables.get(i);
			if(tableName.equals(table.getTableName())) {
				retTable = table;
			}
		}
		return retTable;
	}
	
	public static TableColumn findTableColumnByName(String columnName, Table table) {
		TableColumn retColumn = null;
		List<TableColumn> tableColumns = table.getColumns();
		for(int i = 0; i < tableColumns.size() && retColumn == null; i++) {
			TableColumn column = tableColumns.get(i);
			if(columnName.equals(column.getColumnName())) {
				retColumn = column;
			}
		}
		return retColumn;
	}
	
	public ColumnRelation(Table referendingTable, TableColumn referencingColumn) {
		this.referencingTable = referendingTable;
		this.referencingColumn = referencingColumn;
	}
	
	public static void findRelationByName(String originalTableName, String originalColumnName, String refTableName, String refTableColumn, List<Table> tables) {
		ColumnRelation relation = new ColumnRelation(originalTableName, originalColumnName, refTableName, refTableColumn, tables);
		if(relation.originalColumn != null && relation.referencingColumn != null) {
			relation.originalColumn.setColumnRelation(relation);
		}
	}
	
	private ColumnRelation(String originalTableName, String originalColumnName, String refTableName, String refTableColumn, List<Table> tables) {
		this.originalTable = findTableByName(originalTableName, tables);
		if(this.originalTable != null) {
			this.originalColumn = findTableColumnByName(originalColumnName, this.originalTable);
		}
		
		this.referencingTable = findTableByName(refTableName, tables);
		if(this.referencingTable != null) {
			this.referencingColumn = findTableColumnByName(refTableColumn, this.referencingTable);
		}
		
		if(this.originalColumn != null) {
			this.originalColumn.setColumnRelation(this);
		}
	}

	public Table getReferencingTable() {
		return referencingTable;
	}

	public TableColumn getReferencingColumn() {
		return referencingColumn;
	}
	
}
