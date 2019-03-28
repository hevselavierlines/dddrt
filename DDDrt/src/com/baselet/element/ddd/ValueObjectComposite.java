package com.baselet.element.ddd;

import java.awt.Color;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baselet.control.basics.geom.Rectangle;
import com.baselet.control.enums.ElementId;
import com.baselet.element.interfaces.Component;
import com.baselet.element.interfaces.DrawHandlerInterface;

import at.mic.dddrt.db.model.TableColumn;
import tk.baumi.main.CompositeType;

public class ValueObjectComposite extends FieldComposite {

	@Override
	public ElementId getId() {
		return ElementId.DDDValueObject;
	}

	public ValueObjectComposite() {
		super();
	}

	@Override
	public void init(Rectangle bounds, String panelAttributes, String additionalAttributes, Component component, DrawHandlerInterface handler) {
		super.init(bounds, panelAttributes, additionalAttributes, component, handler);
	}

	@Override
	protected String getTitle() {
		return "<<Value Object>>";
	}

	@Override
	protected void createDefaultJSON() {
		jsonAttributes = new JSONObject();
		jProperties = new JSONArray();
		JSONObject property = new JSONObject();
		property.put("visibility", "-");
		property.put("type", "String");
		property.put("name", "value1");
		property.put("idproperty", false);
		jProperties.put(property);

		jMethods = new JSONArray();
		JSONObject method = new JSONObject();
		method.put("visibility", "+");
		method.put("type", "void");
		method.put("name", "testMethod");
		method.put("parameters", "(Object inputParam)");
		jMethods.put(method);

		JSONObject entities = new JSONObject();
		entities.put("name", "NewValueObject");
		entities.put("properties", jProperties);
		entities.put("methods", jMethods);
		jsonAttributes.put("entities", entities);
	}

	@Override
	protected FieldProperty addProperty(JSONObject jsonObject) {
		return ValueObjectProperty.createFromJSON(jsonObject);
	}

	@Override
	protected FieldProperty createProperty() {
		return new ValueObjectProperty();
	}

	@Override
	protected FieldMethod createMethod() {
		return new FieldMethod();
	}

	@Override
	protected FieldProperty addPropertyFromDatabaseColumn(TableColumn column) {
		return ValueObjectProperty.createFromDatabaseColumn(column);
	}

	@Override
	protected Color getBackgroundColor() {
		return new Color(0xced7dc);
	}

	@Override
	public CompositeType getType() {
		return CompositeType.ValueObject;
	}

	@Override
	public boolean showProperties() {
		return true;
	}

	@Override
	public boolean requireDatabaseInformation() {
		return false;
	}
}
