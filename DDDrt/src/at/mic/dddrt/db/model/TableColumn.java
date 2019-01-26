package at.mic.dddrt.db.model;

public class TableColumn {
	private final String columnName;
	private final String columnType;
	private final long length;
	private final boolean nullable;
	private final boolean primaryKey;
	private ColumnRelation relation;

	public TableColumn(String columnName, String columnType, long length, boolean nullable, boolean primaryKey) {
		super();
		this.columnName = columnName;
		this.columnType = columnType;
		this.length = length;
		this.nullable = nullable;
		this.primaryKey = primaryKey;
	}

	public String getColumnName() {
		return columnName;
	}

	public static String convertType(String dbType) {
		if ("NUMBER".equals(dbType)) {
			return "long";
		}
		else if ("DATE".equals(dbType)) {
			return "Date";
		}
		else {
			return "String";
		}
	}

	public String getColumnType() {
		return convertType(columnType);
	}

	public long getLength() {
		return length;
	}

	public boolean isNullable() {
		return nullable;
	}

	public boolean isIDColumn() {
		return primaryKey;
	}

	public void setColumnRelation(ColumnRelation relation) {
		this.relation = relation;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(columnName).append(": ");
		if ("VARCHAR2".equals(columnType)) {
			sb.append(columnType).append('(').append(length).append(')');
		}
		else {
			sb.append(columnType);
		}
		if (primaryKey) {
			sb.append(" PRIMARY KEY");
		}
		if (relation != null) {
			sb.append(" relates to ").append(relation.getReferencingTable()).append(" in column ")
					.append(relation.getReferencingColumn());
		}
		return sb.toString();
	}
}
