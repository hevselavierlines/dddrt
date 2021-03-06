package tk.baumi.test;

import tk.baumi.ddd.*;
@DDDEntity(tableName = "TEST1_AGGREGATE1")
public class Aggregate1 extends Entity {

    @DDDProperty(primaryKey = true, columnName = "AGG_ID", columnType = "VARCHAR2(1024)")
    private String aggregateID;

    @DDDProperty(columnName = "AGG_ENT1S", columnType = "CLOB")
    private java.util.List<tk.baumi.test.Entity1> ent1s;

    public Aggregate1() {
    }

    public Aggregate1(String _aggregateID, java.util.List<tk.baumi.test.Entity1> _ent1s) {
        aggregateID = _aggregateID;
        ent1s = _ent1s;
    }

    public Object[] properties() {
        Object[] ret = new Object[2];
        ret[0] = aggregateID;
        ret[1] = ent1s;
        return ret;
    }

    public void insert(Object[] properties) {
        if (properties.length == 2) {
            aggregateID = (String) properties[0];
            ent1s = (java.util.List<tk.baumi.test.Entity1>) properties[1];
        }
    }
}