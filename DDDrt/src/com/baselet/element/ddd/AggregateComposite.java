package com.baselet.element.ddd;

import java.awt.Color;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baselet.control.basics.geom.Rectangle;
import com.baselet.control.enums.ElementId;
import com.baselet.element.interfaces.Component;
import com.baselet.element.interfaces.DrawHandlerInterface;

public class AggregateComposite extends EntityComposite {
	@Override
	public ElementId getId() {
		return ElementId.DDDAggregate;
	}

	@Override
	public void init(Rectangle bounds, String panelAttributes, String additionalAttributes, Component component, DrawHandlerInterface handler, String uuid) {
		super.init(bounds, panelAttributes, additionalAttributes, component, handler, uuid);

		// this.component.setBackground(Color.pink);
	}

	@Override
	protected String getTitle() {
		return "<<Root Aggregate>>";
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

	@Override
	protected Color getBackgroundColor() {
		return new Color(0xd3b47e);
	}

}
