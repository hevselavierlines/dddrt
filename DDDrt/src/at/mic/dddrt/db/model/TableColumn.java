package at.mic.dddrt.db.model;

public class TableColumn {
	private final String columnName;
	private final String columnType;
	private final long length;
	private final boolean nullable;
	private final boolean primaryKey;
	private final int precision;
	private final int scale;
	private ColumnRelation relation;

	public TableColumn(String columnName, String columnType, long length, boolean nullable, int precision, int scale, boolean primaryKey) {
		super();
		this.columnName = columnName;
		this.columnType = columnType;
		this.length = length;
		this.nullable = nullable;
		this.primaryKey = primaryKey;
		this.precision = precision;
		this.scale = scale;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getColumnType() {
		if ("NUMBER".equals(columnType)) {
			if (scale > 0) {
				return "double";
			}
			else if (precision > 9) {
				return "long";
			}
			else {
				return "int";
			}
		}
		else if ("DATE".equals(columnType)) {
			return "Date";
		}
		else {
			return "String";
		}
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

	public static String convertToCamelCase(String original) {
		StringBuilder stringBuilder = new StringBuilder();
		boolean nextBig = false;
		for (int i = 0; i < original.length(); i++) {
			char character = original.charAt(i);
			if (character == '_' || character == '-') {
				nextBig = true;
			}
			else {
				if (nextBig) {
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
}
