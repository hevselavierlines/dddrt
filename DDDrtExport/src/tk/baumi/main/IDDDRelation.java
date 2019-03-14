package tk.baumi.main;

public interface IDDDRelation {
	String getStartTableName();
	String getStartPropertyName();
	String getEndTableName();
	String getEndPropertyName();
	boolean relationToValueObject();
}
