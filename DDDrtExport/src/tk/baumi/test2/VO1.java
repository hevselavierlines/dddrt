package tk.baumi.test2;

import tk.baumi.ddd.*;
public class VO1 extends ValueObject {

    private String Value1;

    private String Value2;

    public VO1() {
    }

    public VO1(String _Value1, String _Value2) {
        Value1 = _Value1;
        Value2 = _Value2;
    }

    public void testMethod(Object _inputParam) {
    }

    public Object[] properties() {
        Object[] ret = new Object[2];
        ret[0] = Value1;
        ret[1] = Value2;
        return ret;
    }

    public void insert(Object[] properties) {
        if (properties.length == 2) {
            Value1 = (String) properties[0];
            Value2 = (String) properties[1];
        }
    }
}