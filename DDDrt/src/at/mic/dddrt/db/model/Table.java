package at.mic.dddrt.db.model;

import java.util.LinkedList;
import java.util.List;

public class Table {
	private final String tableName;
	private List<TableColumn> tableColumns;

	public Table(String tableName) {
		this.tableName = tableName;
		this.tableColumns = new LinkedList<TableColumn>();
	}

	public String getTableName() {
		return tableName;
	}
	
	public void addColumn(TableColumn tableColumn) {
		this.tableColumns.add(tableColumn);
	}
	
	public List<TableColumn> getColumns() {
		return tableColumns;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(tableName);
		sb.append('\n'); 
		for(TableColumn tableColumn : tableColumns) {
			sb.append(tableColumn.toString());
			sb.append('\n');
		}
		return sb.toString();
	}
}
