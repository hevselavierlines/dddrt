package tk.baumi.ddd;

public abstract class Entity {

    public abstract Object[] properties();

    public abstract void insert(Object[] properties);
    
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