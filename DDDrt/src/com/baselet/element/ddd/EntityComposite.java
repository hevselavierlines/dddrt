package com.baselet.element.ddd;

import java.awt.Color;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baselet.control.basics.geom.Rectangle;
import com.baselet.control.enums.ElementId;
import com.baselet.element.interfaces.Component;
import com.baselet.element.interfaces.DrawHandlerInterface;

import tk.baumi.main.CompositeType;

public class EntityComposite extends FieldComposite {

	@Override
	public ElementId getId() {
		return ElementId.DDDEntity;
	}

	public EntityComposite() {
		super();
	}

	@Override
	public void init(Rectangle bounds, String panelAttributes, String additionalAttributes, Component component, DrawHandlerInterface handler) {
		super.init(bounds, panelAttributes, additionalAttributes, component, handler);
	}

	@Override
	protected String getTitle() {
		return "<<Entity>>";
	}

	@Override
	protected void createDefaultJSON() {
		jsonAttributes = new JSONObject();
		jProperties = new JSONArray();
		JSONObject property = new JSONObject();
		/* protected static final String JSON_IDPROPERTY = "idproperty"; protected static final String JSON_NAME = "name"; protected static final String JSON_TYPE = "type"; protected static final String JSON_VISIBILITY = "visibility"; protected static final String JSON_DATABASE_NAME = "dbname"; */
		property.put(FieldProperty.JSON_VISIBILITY, "-");
		property.put(FieldProperty.JSON_TYPE, "UUID");
		property.put(FieldProperty.JSON_NAME, "unqueId");
		property.put(FieldProperty.JSON_DATABASE_NAME, "UNIQUE_ID");
		property.put(FieldProperty.JSON_IDPROPERTY, true);
		jProperties.put(property);

		jMethods = new JSONArray();
		JSONObject method = new JSONObject();
		method.put("visibility", "+");
		method.put("type", "int");
		method.put("name", "testMethod");
		method.put("parameters", "(Object inputParam)");
		jMethods.put(method);

		JSONObject entities = new JSONObject();
		entities.put("name", "New" + getType().name());
		entities.put("properties", jProperties);
		entities.put("methods", jMethods);
		jsonAttributes.put("entities", entities);
	}

	@Override
	protected FieldProperty addProperty(JSONObject jsonObject) {
		return EntityProperty.createFromJSON(jsonObject);
	}

	@Override
	protected FieldProperty createProperty() {
		return new EntityProperty();
	}

	@Override
	protected FieldProperty addPropertyFromDatabaseColumn(at.mic.dddrt.db.model.TableColumn column) {
		return EntityProperty.createFromDatabaseColumn(column);
	}

	@Override
	protected FieldMethod createMethod() {
		return new FieldMethod();
	}

	@Override
	protected Color getBackgroundColor() {
		return new Color(0xffefcd);
	}

	@Override
	public CompositeType getType() {
		return CompositeType.Entity;
	}

	@Override
	public boolean showProperties() {
		return true;
	}

	@Override
	public boolean requireDatabaseInformation() {
		return true;
	}

}
