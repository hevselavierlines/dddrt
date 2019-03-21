package tk.baumi.test;

import java.util.UUID;

@DDDEntity(tableName = "AGGREGATE1")
public class Aggregate1 extends tk.baumi.ddd.Entity {

    @DDDProperty(primaryKey = true, columnName = "ID_PROPERTY", columnType = "VARCHAR2(1024)")
    private UUID idProperty;

    @DDDProperty(columnName = "FK_ENTITY1", columnType = "VARCHAR2(1024)")
    private Entity1 fkEntity1;

    public Aggregate1() {
    }

    public Aggregate1(UUID _idProperty, Entity1 _fkEntity1) {
        idProperty = _idProperty;
        fkEntity1 = _fkEntity1;
    }

    public int testMethod(Object _inputParam) {
        return 0;
    }

    public Object[] properties() {
        Object[] ret = new Object[2];
        ret[0] = idProperty;
        ret[1] = fkEntity1;
        return ret;
    }

    public void insert(Object[] properties) {
        if (properties.length == 2) {
            idProperty = (UUID) properties[0];
            fkEntity1 = (Entity1) properties[1];
        }
    }
    
    public String toString() {
    	StringBuffer ret = new StringBuffer();
    	ret.append("{").append(idProperty.toString()).append(",");
    	if(fkEntity1 != null) {
    		ret.append(' ').append(fkEntity1.toString());
    	} else {
    		ret.deleteCharAt(ret.length() - 1);
    	}
    	ret.append("}");
    	return ret.toString();
    }
}