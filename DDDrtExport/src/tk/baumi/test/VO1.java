package tk.baumi.test;

import tk.baumi.ddd.*;
public class VO1 extends ValueObject {

    private long value1;

    private double value2;

    public VO1() {
    }

    public VO1(long _value1, double _value2) {
        value1 = _value1;
        value2 = _value2;
    }

    public Object[] properties() {
        Object[] ret = new Object[2];
        ret[0] = value1;
        ret[1] = value2;
        return ret;
    }

    public void insert(Object[] properties) {
        if (properties.length == 2) {
            value1 = (long) properties[0];
            value2 = (double) properties[1];
        }
    }
}