package tk.baumi.test;

@DDDEntity(tableName = "ENTITY2")
class Entity2 {

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
}