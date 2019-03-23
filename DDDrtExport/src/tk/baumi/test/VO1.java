package tk.baumi.test;

import java.util.Date;

public class VO1 extends tk.baumi.ddd.ValueObject {

    private String value1;

    private Date value2;

    private int value3;

    public VO1() {
    }

    public VO1(String _value1, Date _value2, int _value3) {
        value1 = _value1;
        value2 = _value2;
        value3 = _value3;
    }

    public Object[] properties() {
        Object[] ret = new Object[3];
        ret[0] = value1;
        ret[1] = value2;
        ret[2] = value3;
        return ret;
    }

    public void insert(Object[] properties) {
        if (properties.length == 3) {
            value1 = (String) properties[0];
            value2 = (Date) properties[1];
            value3 = (int) properties[2];
        }
    }
}