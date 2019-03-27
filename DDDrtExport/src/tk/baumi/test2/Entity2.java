package tk.baumi.test2;

import tk.baumi.ddd.*;
@DDDEntity(tableName = "TEST2_ENTITY2")
public class Entity2 extends Entity {

    @DDDProperty(primaryKey = true, columnName = "ID_ENTITY2", columnType = "VARCHAR2(1024)")
    private String idForEntity2;

    @DDDProperty(columnName = "VALUE_2", columnType = "CLOB")
    private String value2;

    public Entity2() {
    }

    public Entity2(String _idForEntity2, String _value2) {
        idForEntity2 = _idForEntity2;
        value2 = _value2;
    }

    public int testMethod(Object _inputParam) {
        return 0;
    }

    public Object[] properties() {
        Object[] ret = new Object[2];
        ret[0] = idForEntity2;
        ret[1] = value2;
        return ret;
    }

    public void insert(Object[] properties) {
        if (properties.length == 2) {
            idForEntity2 = (String) properties[0];
            value2 = (String) properties[1];
        }
    }
}