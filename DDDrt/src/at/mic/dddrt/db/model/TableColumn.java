package at.mic.dddrt.db.model;

public class TableColumn {
	private final String columnName;
	private final String columnType;
	private final long length;
	private final boolean nullable;
	private ColumnRelation relation;

	public TableColumn(String columnName, String columnType, long length, boolean nullable) {
		super();
		this.columnName = columnName;
		this.columnType = columnType;
		this.length = length;
		this.nullable = nullable;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getColumnType() {
		return columnType;
	}

	public long getLength() {
		return length;
	}

	public boolean isNullable() {
		return nullable;
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
		if (relation != null) {
			sb.append(" relates to ").append(relation.getReferencingTable()).append(" in column ")
					.append(relation.getReferencingColumn());
		}
		return sb.toString();
	}
}
