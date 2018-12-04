package com.baselet.element.ddd;

import org.json.JSONObject;

import com.baselet.diagram.CurrentDiagram;
import com.baselet.element.ComponentSwing;
import com.baselet.element.NewGridElement;

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
		java.awt.Component[] container = CurrentDiagram.getInstance().getDiagramHandler().getDrawPanel().getComponents();
		for (java.awt.Component cont : container) {
			if (cont instanceof ComponentSwing) {
				ComponentSwing cw = (ComponentSwing) cont;
				NewGridElement gridElement = cw.getGridElement();
				if (gridElement instanceof ValueObjectComposite) {
					FieldComposite ec = (FieldComposite) gridElement;
					propertyType.addItem(ec.getName(), ec);
				}
			}
		}

		for (String DEFAULT_TYPE : DEFAULT_TYPES) {
			propertyType.addItem(DEFAULT_TYPE);
		}
		if (selection != null) {
			propertyType.setSelectedItem(selection);
		}
		else {
			propertyType.setSelectedIndex(0);
		}
	}

}
