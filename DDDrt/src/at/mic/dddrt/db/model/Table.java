package at.mic.dddrt.db.model;

import java.util.LinkedList;
import java.util.List;

public class Table {
	private final String tableName;
	private final List<TableColumn> tableColumns;
	private boolean primaryKey;

	public Table(String tableName) {
		this.tableName = tableName;
		tableColumns = new LinkedList<TableColumn>();
	}

	public String getTableName() {
		return tableName;
	}

	public void addColumn(TableColumn tableColumn) {
		tableColumns.add(tableColumn);
	}

	public List<TableColumn> getColumns() {
		return tableColumns;
	}

	public String generateNotes() {
		StringBuilder sb = new StringBuilder();
		sb.append("The table ")
				.append(tableName)
				.append(" has been imported form the database.");
		return sb.toString();
	}

	public static String convertToCamelCase(final String original) {
		StringBuilder stringBuilder = new StringBuilder();
		boolean nextBig = false;
		for (int i = 0; i < original.length(); i++) {
			char character = original.charAt(i);
			if (character == '_' || character == '-') {
				nextBig = true;
			}
			else {
				if (nextBig || i == 0) {
					character = Character.toUpperCase(character);
					nextBig = false;
				}
				else {
					character = Character.toLowerCase(character);
				}
				stringBuilder.append(character);
			}
		}
		return stringBuilder.toString();
	}

	public String getTableNameAsCamelCase() {
		return Table.convertToCamelCase(tableName);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(tableName);
		sb.append('\n');
		for (TableColumn tableColumn : tableColumns) {
			sb.append(tableColumn.toString());
			sb.append('\n');
		}
		return sb.toString();
	}

	public boolean hasPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

}
