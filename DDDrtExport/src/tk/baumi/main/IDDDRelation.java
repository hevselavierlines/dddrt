package tk.baumi.main;

public interface IDDDRelation {
	
	String getStartTableName();

	String getStartTableIDProperty();
	
	String getStartPropertyName();
	
	String getStartPropertyType();

	String getEndTableName();

	String getEndPropertyName();
	
	String getEndPropertyType();

	boolean relationToValueObject();
	
	String associateTableName();
	
	boolean multipleRelation();
}
