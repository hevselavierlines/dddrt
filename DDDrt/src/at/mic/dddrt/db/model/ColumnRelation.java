package at.mic.dddrt.db.model;

import java.util.List;

public class ColumnRelation {
	private final String referencingTable;
	private final String referencingColumn;

	private final String originalTableName;
	private final String originalTableColumn;
	private final TableColumn originalColumn;

	public static Table findTableByName(String tableName, List<Table> tables) {
		Table retTable = null;
		for (int i = 0; i < tables.size() && retTable == null; i++) {
			Table table = tables.get(i);
			if (tableName.equals(table.getTableName())) {
				retTable = table;
			}
		}
		return retTable;
	}

	public static TableColumn findTableColumnByName(String columnName, Table table) {
		TableColumn retColumn = null;
		List<TableColumn> tableColumns = table.getColumns();
		for (int i = 0; i < tableColumns.size() && retColumn == null; i++) {
			TableColumn column = tableColumns.get(i);
			if (columnName.equals(column.getColumnName())) {
				retColumn = column;
			}
		}
		return retColumn;
	}

	public static ColumnRelation findRelationByName(Table originalTable, String originalColumnName, String refTableName, String refTableColumn) {
		ColumnRelation relation = new ColumnRelation(originalTable, originalColumnName, refTableName, refTableColumn);
		if (relation.originalColumn != null && relation.referencingColumn != null) {
			relation.originalColumn.setColumnRelation(relation);
		}
		return relation;
	}

	private ColumnRelation(Table originalTable, String originalColumnName, String refTableName, String refTableColumn) {
		originalTableName = originalTable.getTableName();
		originalTableColumn = originalColumnName;

		originalColumn = findTableColumnByName(originalColumnName, originalTable);

		referencingTable = refTableName;
		referencingColumn = refTableColumn;
	}

	public String getReferencingTable() {
		return referencingTable;
	}

	public String getReferencingColumn() {
		return referencingColumn;
	}

	public String getOriginalTable() {
		return originalTableName;
	}

	public String getOriginalColumn() {
		return originalTableColumn;
	}

}
