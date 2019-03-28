package com.baselet.element.ddd;

import java.awt.Color;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baselet.control.enums.ElementId;

import at.mic.dddrt.db.model.TableColumn;
import tk.baumi.main.CompositeType;

public class DDDService extends FieldComposite {

	@Override
	public CompositeType getType() {
		return CompositeType.Service;
	}

	@Override
	protected Color getBackgroundColor() {
		return Color.white;
	}

	@Override
	protected void createDefaultJSON() {
		jsonAttributes = new JSONObject();
		jProperties = new JSONArray();

		jMethods = new JSONArray();
		JSONObject method = new JSONObject();
		method.put("visibility", "+");
		method.put("type", "int");
		method.put("name", "testMethod");
		method.put("parameters", "(Object inputParam)");
		jMethods.put(method);

		JSONObject entities = new JSONObject();
		entities.put("name", "NewService");
		entities.put("properties", jProperties);
		entities.put("methods", jMethods);
		jsonAttributes.put("entities", entities);
	}

	@Override
	public ElementId getId() {
		return ElementId.DDDService;
	}

	@Override
	public boolean showProperties() {
		return false;
	}

	@Override
	protected String getTitle() {
		return "<<Service>>";
	}

	@Override
	protected FieldProperty addPropertyFromDatabaseColumn(TableColumn column) {
		return null;
	}

	@Override
	protected FieldProperty addProperty(JSONObject jsonObject) {
		return null;
	}

	@Override
	protected FieldProperty createProperty() {
		return null;
	}

	@Override
	protected FieldMethod createMethod() {
		return new FieldMethod();
	}

	@Override
	public boolean requireDatabaseInformation() {
		return false;
	}

}
