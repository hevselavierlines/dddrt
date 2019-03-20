package tk.baumi.test;

import java.util.UUID;

@DDDEntity(tableName = "ENTITY1")
public class Entity1 extends tk.baumi.ddd.Entity {

    @DDDProperty(primaryKey = true, columnName = "ID_PROPERTY2", columnType = "VARCHAR2(1024)")
    private UUID idProperty2;

    @DDDProperty(columnName = "PROPERTY2", columnType = "CLOB")
    private String property2;

    public Entity1() {
    }

    public Entity1(UUID _idProperty2, String _property2) {
        idProperty2 = _idProperty2;
        property2 = _property2;
    }

    public int testMethod(Object _inputParam) {
        return 0;
    }

    public Object[] properties() {
        Object[] ret = new Object[2];
        ret[0] = idProperty2;
        ret[1] = property2;
        return ret;
    }

    public void insert(Object[] properties) {
        if (properties.length == 2) {
            idProperty2 = (UUID) properties[0];
            property2 = (String) properties[1];
        }
    }
    
    public String toString() {
    	return "{" + idProperty2 + ", " + property2 + "}";
    }
}