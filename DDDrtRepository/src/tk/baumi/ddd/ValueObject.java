package tk.baumi.ddd;

public abstract class ValueObject {
	public abstract Object[] properties();
	public abstract void insert(Object[] elements);
	
	public org.json.JSONArray serialize() {
        org.json.JSONArray ret = new org.json.JSONArray();
        Object[] properties = properties();
        for (Object property : properties) {
            if (!ValueObject.class.isAssignableFrom(property.getClass())) {
                ret.put(property);
            }
        }
        return ret;
    }

    public void deserialize(org.json.JSONArray json) {
        Object[] elems = new Object[json.length()];
        for (int i = 0; i < elems.length; i++) {
            Object elem = json.get(i);
            if (!ValueObject.class.isAssignableFrom(elem.getClass())) {
                elems[i] = elem;
            }
        }
        insert(elems);
    }
    
    public String toString() {
    	StringBuffer stringBuffer = new StringBuffer(this.getClass().getName());
    	stringBuffer.append("{\n");
    	for(Object property : properties()) {
    		if(property != null) {
    			stringBuffer.append(property.toString());
    		} else {
    			stringBuffer.append("\tnull");
    		}
    		stringBuffer.append(",\n");
    	}
    	stringBuffer.deleteCharAt(stringBuffer.length() - 1);
    	stringBuffer.append("\n}");
    	return stringBuffer.toString();
    }
}
