package tk.baumi.test;

import org.json.JSONArray;

import tk.baumi.ddd.ValueObject;

@DDDEntity(tableName = "NEW_VALUE_OBJECT")
public class VO1 extends ValueObject {

    @DDDProperty(columnName = "VALUE_1", columnType = "CLOB")
    private String value1;

    @DDDProperty(columnName = "VALUE_2", columnType = "CLOB")
    private String value2;
    
    public VO1() {
    }

    public VO1(String _Value1, String _Value2) {
        value1 = _Value1;
        value2 = _Value2;
    }

    public void testMethod(Object _inputParam) {
    }

	@Override
	public Object[] properties() {
		return new Object[] {value1, value2};
	}

	@Override
	public void insert(Object[] elements) {
		if(elements.length == 2) {
			value1 = (String) elements[0];
			value2 = (String) elements[0];
		}
	}
}