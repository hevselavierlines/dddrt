package tk.baumi.test2;

import tk.baumi.ddd.*;
@DDDEntity(tableName = "TEST2_AGGREGATE1")
public class Aggregate1 extends Entity {

    @DDDProperty(primaryKey = true, columnName = "ID_FOR_AGGREGATE", columnType = "VARCHAR2(1024)")
    private java.util.UUID idForAggregate;

    @DDDProperty(columnName = "ENTITY", columnType = "VARCHAR2(1024)")
    private tk.baumi.test2.Entity1 entity;

    @DDDProperty(columnName = "VALUE", columnType = "CLOB")
    private tk.baumi.test2.VO1 valueObject;

    @DDDProperty(columnName = "ENTITY_2", columnType = "VARCHAR2(1024)")
    private tk.baumi.test2.Entity2 entity2;

    public Aggregate1() {
    }

    public Aggregate1(java.util.UUID _idForAggregate, tk.baumi.test2.Entity1 _entity, tk.baumi.test2.VO1 _valueObject, tk.baumi.test2.Entity2 _entity2) {
        idForAggregate = _idForAggregate;
        entity = _entity;
        valueObject = _valueObject;
        entity2 = _entity2;
    }

    public int testMethod(Object _inputParam) {
        return 0;
    }

    public Object[] properties() {
        Object[] ret = new Object[4];
        ret[0] = idForAggregate;
        ret[1] = entity;
        ret[2] = valueObject;
        ret[3] = entity2;
        return ret;
    }

    public void insert(Object[] properties) {
        if (properties.length == 4) {
            idForAggregate = (java.util.UUID) properties[0];
            entity = (tk.baumi.test2.Entity1) properties[1];
            valueObject = (tk.baumi.test2.VO1) properties[2];
            entity2 = (tk.baumi.test2.Entity2) properties[3];
        }
    }
}