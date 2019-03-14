package tk.baumi.main;

import java.util.List;

public class ExportDatabaseTask {
	public static void createDeleteStatements(StringBuffer sql, List<IFieldComposite> fields) {
		for (IFieldComposite fieldComposite : fields) {
			if(fieldComposite.getType() != CompositeType.ValueObject) {
				sql.append("DROP TABLE ").append(fieldComposite.getDatabaseName()).append(";\n");
			}
		}
	}

	public static void exportFieldCompositeToDBTable(StringBuffer sql, IFieldComposite field) {
		sql.append("CREATE TABLE ").append(field.getDatabaseName()).append('(').append("\n");
		for (ExportProperty property : field.getProperties()) {
			sql.append('\t').append(property.getDatabaseName()).append(' ').append(property.getDatabaseType());
			if (property.isPrimaryProperty()) {
				sql.append(" PRIMARY KEY");
			}
			sql.append(',').append('\n');
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.deleteCharAt(sql.length() - 1);
		sql.append(");\n\n");
	}

	public static void exportRelationToDB(StringBuffer sql, IDDDRelation relation) {
		/*
		 * ALTER TABLE table_name ADD CONSTRAINT constraint_name FOREIGN KEY (column1,
		 * column2, ... column_n) REFERENCES parent_table (column1, column2, ...
		 * column_n);
		 */
		String startTable = relation.getStartTableName();
		String startProperty = relation.getStartPropertyName();
		String endTable = relation.getEndTableName();
		String endProperty = relation.getEndPropertyName();
		sql.append("ALTER TABLE ").append(startTable).append('\n');
		sql.append("ADD CONSTRAINT ").append("fk_").append(startProperty).append('_').append(endTable).append('\n');
		sql.append('\t').append("FOREIGN KEY (").append(startProperty).append(")\n");
		sql.append('\t').append("REFERENCES ").append(endTable).append(" (").append(endProperty).append(");\n");

	}

}
