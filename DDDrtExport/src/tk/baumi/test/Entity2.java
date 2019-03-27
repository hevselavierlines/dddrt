package tk.baumi.test;

import tk.baumi.ddd.*;
@DDDEntity(tableName = "TEST1_ENTITY2")
public class Entity2 extends Entity {

    @DDDProperty(primaryKey = true, columnName = "ENT2_IDS", columnType = "VARCHAR2(1024)")
    private String ent2ID;

    @DDDProperty(columnName = "ENT2_VOS", columnType = "CLOB")
    private java.util.List<tk.baumi.test.VO1> ent2VOs;

    public Entity2() {
    }

    public Entity2(String _ent2ID, java.util.List<tk.baumi.test.VO1> _ent2VOs) {
        ent2ID = _ent2ID;
        ent2VOs = _ent2VOs;
    }

    public Object[] properties() {
        Object[] ret = new Object[2];
        ret[0] = ent2ID;
        ret[1] = ent2VOs;
        return ret;
    }

    public void insert(Object[] properties) {
        if (properties.length == 2) {
            ent2ID = (String) properties[0];
            ent2VOs = (java.util.List<tk.baumi.test.VO1>) properties[1];
        }
    }
}