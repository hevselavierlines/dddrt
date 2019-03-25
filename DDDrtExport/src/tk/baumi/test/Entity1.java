package tk.baumi.test;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@DDDEntity(tableName = "ENTITY1")
public class Entity1 extends tk.baumi.ddd.Entity {

    @DDDProperty(primaryKey = true, columnName = "ID_PROPERTY2", columnType = "VARCHAR2(1024)")
    private UUID idProperty2;

    @DDDProperty(columnName = "PROPERTY2", columnType = "CLOB")
    private int property2;

    @DDDProperty(columnName = "PROPERTY3", columnType = "CLOB")
    private double property3;

    @DDDProperty(columnName = "PROPERTY4", columnType = "CLOB")
    private Date property4;

    @DDDProperty(columnName = "PROPERTY5", columnType = "CLOB")
    private List<Entity2> property5;

    public Entity1() {
    }

    public Entity1(UUID _idProperty2, int _property2, double _property3, Date _property4, List<Entity2> _property5) {
        idProperty2 = _idProperty2;
        property2 = _property2;
        property3 = _property3;
        property4 = _property4;
        property5 = _property5;
    }

    public int testMethod(Object _inputParam) {
        return 0;
    }

    public Object[] properties() {
        Object[] ret = new Object[5];
        ret[0] = idProperty2;
        ret[1] = property2;
        ret[2] = property3;
        ret[3] = property4;
        ret[4] = property5;
        return ret;
    }

    public void insert(Object[] properties) {
        if (properties.length == 5) {
            idProperty2 = (UUID) properties[0];
            property2 = (int) properties[1];
            property3 = (double) properties[2];
            property4 = (Date) properties[3];
            property5 = (List<Entity2>) properties[4];
        }
    }
}