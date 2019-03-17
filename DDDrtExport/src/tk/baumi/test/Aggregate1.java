package tk.baumi.test;

import java.util.List;
import java.util.UUID;

@DDDEntity(tableName = "AGGREGATE_1")
public class Aggregate1 {

    @DDDProperty(primaryKey = true, columnName = "ID_FOR_AGGREGATE", columnType = "VARCHAR2(1024)")
    private UUID idForAggregate;

    @DDDProperty(columnName = "ENTITY", columnType = "VARCHAR2(1024)")
    private Entity1 entity;

    @DDDProperty(columnName = "VALUE", columnType = "CLOB")
    private VO1 valueObject;

    @DDDProperty(columnName = "ENTITY_2", columnType = "VARCHAR2(1024)")
    private List<Entity2> entity2;

    public Aggregate1() {
    }

    public Aggregate1(UUID _idForAggregate, Entity1 _entity, VO1 _valueObject, List<Entity2> _entity2) {
        idForAggregate = _idForAggregate;
        entity = _entity;
        valueObject = _valueObject;
        entity2 = _entity2;
    }

    public int testMethod(Object _inputParam) {
        return 0;
    }
}