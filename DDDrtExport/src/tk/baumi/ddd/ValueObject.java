package tk.baumi.ddd;

import org.json.JSONArray;

public abstract class ValueObject {
	public abstract Object[] properties();
	public abstract void insert(Object[] elements);
	
	public void deserialize(JSONArray json) {
		Object[] elems = new Object[json.length()];
		for(int i = 0; i < elems.length; i++) {
			Object elem = json.get(i);
			if(!ValueObject.class.isAssignableFrom(elem.getClass())) {
				elems[i] = elem;
			}
		}
		insert(elems);
	}
	
	public JSONArray serialize() {
		JSONArray ret = new JSONArray();
		Object[] properties = properties();
		for(Object property : properties) {
			if(!ValueObject.class.isAssignableFrom(property.getClass())) {
				ret.put(property);
			}
		}
		return ret;
	}
}
