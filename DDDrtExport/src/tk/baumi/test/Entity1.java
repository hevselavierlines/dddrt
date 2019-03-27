package tk.baumi.test;

import tk.baumi.ddd.*;
@DDDEntity(tableName = "TEST1_ENTITY1")
public class Entity1 extends Entity {

    @DDDProperty(primaryKey = true, columnName = "ENT1_ID", columnType = "VARCHAR2(1024)")
    private String entity1ID;

    @DDDProperty(columnName = "ENTITY2S", columnType = "CLOB")
    private java.util.List<tk.baumi.test.Entity2> entity2s;

    @DDDProperty(columnName = "ENTITY2_DATE", columnType = "CLOB")
    private java.util.Date entity2Date;

    public Entity1() {
    }

    public Entity1(String _entity1ID, java.util.List<tk.baumi.test.Entity2> _entity2s, java.util.Date _entity2Date) {
        entity1ID = _entity1ID;
        entity2s = _entity2s;
        entity2Date = _entity2Date;
    }

    public Object[] properties() {
        Object[] ret = new Object[3];
        ret[0] = entity1ID;
        ret[1] = entity2s;
        ret[2] = entity2Date;
        return ret;
    }

    public void insert(Object[] properties) {
        if (properties.length == 3) {
            entity1ID = (String) properties[0];
            entity2s = (java.util.List<tk.baumi.test.Entity2>) properties[1];
            entity2Date = (java.util.Date) properties[2];
        }
    }
}