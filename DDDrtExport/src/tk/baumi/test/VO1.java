package tk.baumi.test;

@DDDEntity(tableName = "NEW_VALUE_OBJECT")
class VO1 {

    @DDDProperty(columnName = "VALUE_1", columnType = "CLOB")
    private String Value1;

    @DDDProperty(columnName = "VALUE_2", columnType = "CLOB")
    private String Value2;

    public VO1(String _Value1, String _Value2) {
        Value1 = _Value1;
        Value2 = _Value2;
    }

    public void testMethod(Object _inputParam) {
    }
}