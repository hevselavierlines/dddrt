package tk.baumi.main;

import java.util.List;

public class ExportDatabaseTask {
	public static void createDeleteStatements(StringBuffer sql, List<IFieldComposite> fields) {
		for (IFieldComposite fieldComposite : fields) {
			if (fieldComposite.getType() != CompositeType.ValueObject) {
				sql
					.append("DROP TABLE ")
					.append(fieldComposite.getDatabaseName())
					.append(" CASCADE CONSTRAINTS")
					.append(";\n");
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
		
		boolean multipleRelation = relation.multipleRelation();
		if(multipleRelation) {
			/*CREATE TABLE ASSOC_AGGREGATE_1_ENTITY_2_ENTITY2 (
		        PROPERTY_ENTITY_2 VARCHAR2(1024),
		        ENTITY_ENTITY VARCHAR2(1024)
		    );
		    
		    ALTER TABLE ASSOC_AGGREGATE_1_ENTITY_2_ENTITY2
		    ADD CONSTRAINT fk_assoc_property_entity_2
		        FOREIGN KEY(PROPERTY_ENTITY_2) 
		        REFERENCES AGGREGATE_1(ID_FOR_AGGREGATE);
		        
		    ALTER TABLE ASSOC_AGGREGATE_1_ENTITY_2_ENTITY2
		    ADD CONSTRAINT fk_assoc_entity_entity
		        FOREIGN KEY(ENTITY_ENTITY)
		        REFERENCES ENTITY2(ID_ENTITY2);*/
			sql
				.append("DROP TABLE ")
				.append(relation.associateTableName())
				.append(" CASCADE CONSTRAINTS;\n\n");
			
			
			String PROPERTY_PREFIX = "PROPERTY_";
			String TABLE_PREFIX = "ENTITY_";
			sql.append("CREATE TABLE ").append(relation.associateTableName()).append(" (\n");
			sql.append('\t').append(PROPERTY_PREFIX).append(relation.getStartTableName()).append(' ').append(relation.getStartPropertyType()).append(",\n");
			sql.append('\t').append(TABLE_PREFIX).append(relation.getEndTableName()).append(' ').append(relation.getEndPropertyType()).append(",\n");
			sql.append('\t').append("FOREIGN KEY (").append(PROPERTY_PREFIX).append(relation.getStartTableName()).append(")\n");
			sql.append("\t\t").append("REFERENCES ").append(relation.getStartTableName()).append(" (").append(relation.getStartTableIDProperty()).append("),\n");
			sql.append('\t').append("FOREIGN KEY (").append(TABLE_PREFIX).append(relation.getEndTableName()).append(")\n");
			sql.append("\t\t").append("REFERENCES ").append(relation.getEndTableName()).append(" (").append(relation.getEndPropertyName()).append(")\n");
			sql.append(");\n\n");
			
		} else {
			//in case of a 1:n relationship.
			sql.append("ALTER TABLE ").append(startTable).append('\n');
			sql.append("ADD CONSTRAINT ").append("fk_").append(startProperty).append('_').append(endTable).append('\n');
			sql.append('\t').append("FOREIGN KEY (").append(startProperty).append(")\n");
			sql.append('\t').append("REFERENCES ").append(endTable).append(" (").append(endProperty).append(");\n");
		}
	}

}
