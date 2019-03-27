package tk.baumi.test2;

import tk.baumi.ddd.*;
@DDDEntity(tableName = "TEST2_ENTITY1")
public class Entity1 extends Entity {

    @DDDProperty(primaryKey = true, columnName = "ID_FOR_ENTITY", columnType = "VARCHAR2(1024)")
    private String idForEntity;

    @DDDProperty(columnName = "ENTITY_PROPERTY2", columnType = "CLOB")
    private String entityProperty2;

    public Entity1() {
    }

    public Entity1(String _idForEntity, String _entityProperty2) {
        idForEntity = _idForEntity;
        entityProperty2 = _entityProperty2;
    }

    public int testMethod(Object _inputParam) {
        return 0;
    }

    public Object[] properties() {
        Object[] ret = new Object[2];
        ret[0] = idForEntity;
        ret[1] = entityProperty2;
        return ret;
    }

    public void insert(Object[] properties) {
        if (properties.length == 2) {
            idForEntity = (String) properties[0];
            entityProperty2 = (String) properties[1];
        }
    }
}