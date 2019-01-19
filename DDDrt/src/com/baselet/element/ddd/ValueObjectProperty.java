package com.baselet.element.ddd;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.baselet.diagram.CurrentDiagram;

public class ValueObjectProperty extends FieldProperty {
	private static final long serialVersionUID = -3533886022028899849L;

	public static ValueObjectProperty createFromJSON(JSONObject property) {
		try {
			String visibility = property.getString(JSON_VISIBILITY);
			String type = property.getString(JSON_TYPE);
			String name = property.getString(JSON_NAME);
			boolean idProperty = property.getBoolean(JSON_IDPROPERTY);
			return new ValueObjectProperty(visibility, type, name, idProperty);
		} catch (Exception ex) {
			return new ValueObjectProperty();
		}
	}

	protected ValueObjectProperty() {
		super();
	}

	protected ValueObjectProperty(String propertyVisibility,
			String propertyType,
			String propertyName,
			boolean idProperty) {
		super(propertyVisibility, propertyType, propertyName, idProperty);
	}

	@Override
	protected void addPropertyTypes() {
		Object selection = propertyType.getSelectedItem();
		propertyType.removeAllItems();
		List<FieldComposite> sameBoundedContextElements = new LinkedList<FieldComposite>();
		List<FieldComposite> diffrentBoundedContextElements = new LinkedList<FieldComposite>();
		for (ValueObjectComposite ec : CurrentDiagram
				.getInstance()
				.getDiagramHandler()
				.getDrawPanel()
				.getHelper(ValueObjectComposite.class)) {
			if (getParentFieldComposite().isInSameBoundedContext(ec)) {
				sameBoundedContextElements.add(ec);
			}
			else {
				diffrentBoundedContextElements.add(ec);
			}
		}

		Collections.sort(sameBoundedContextElements);
		Collections.sort(diffrentBoundedContextElements);
		for (FieldComposite fc : sameBoundedContextElements) {
			propertyType.addItem(fc.getName(), fc);
		}
		for (String DEFAULT_TYPE : DEFAULT_TYPES) {
			propertyType.addItem(DEFAULT_TYPE);
		}
		for (FieldComposite fc : diffrentBoundedContextElements) {
			propertyType.addItem(fc.getFullName(), fc);
		}

		if (selection != null) {
			propertyType.setSelectedItem(selection);
		}
		else {
			propertyType.setSelectedIndex(0);
		}
	}

}
