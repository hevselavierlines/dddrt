package tk.baumi.test;

@DDDEntity(tableName = "ENTITY2")
public class Entity2 extends tk.baumi.ddd.Entity {

    @DDDProperty(primaryKey = true, columnName = "ID_PROPERTY", columnType = "VARCHAR2(1024)")
    private String idProperty;

    @DDDProperty(columnName = "PROPERTY2", columnType = "CLOB")
    private String property2;

    public Entity2() {
    }

    public Entity2(String _idProperty, String _property2) {
        idProperty = _idProperty;
        property2 = _property2;
    }

    public int testMethod(Object _inputParam) {
        return 0;
    }

    public Object[] properties() {
        Object[] ret = new Object[2];
        ret[0] = idProperty;
        ret[1] = property2;
        return ret;
    }

    public void insert(Object[] properties) {
        if (properties.length == 2) {
            idProperty = (String) properties[0];
            property2 = (String) properties[1];
        }
    }
}