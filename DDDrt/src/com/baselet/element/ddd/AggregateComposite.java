package com.baselet.element.ddd;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baselet.control.enums.ElementId;

public class AggregateComposite extends EntityComposite {
	@Override
	public ElementId getId() {
		return ElementId.DDDAggregate;
	}

	@Override
	protected String getTitle() {
		return "<<Aggregate>>";
	}

	@Override
	protected void createDefaultJSON() {
		jsonAttributes = new JSONObject();
		jProperties = new JSONArray();
		JSONObject property = new JSONObject();
		property.put("visibility", "-");
		property.put("type", "UUID");
		property.put("name", "unqueId");
		property.put("idproperty", true);
		jProperties.put(property);

		jMethods = new JSONArray();
		JSONObject method = new JSONObject();
		method.put("visibility", "+");
		method.put("type", "int");
		method.put("name", "testMethod");
		method.put("parameters", "(Object inputParam)");
		jMethods.put(method);

		JSONObject entities = new JSONObject();
		entities.put("name", "newAggregate");
		entities.put("properties", jProperties);
		entities.put("methods", jMethods);
		jsonAttributes.put("entities", entities);
	}

	@Override
	protected FieldProperty createProperty() {
		return new EntityProperty();
	}

	@Override
	protected FieldMethod createMethod() {
		return new FieldMethod();
	}
}
