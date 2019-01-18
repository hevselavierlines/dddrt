package com.baselet.element.ddd;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.baselet.diagram.CurrentDiagram;
import com.baselet.element.ComponentSwing;
import com.baselet.element.NewGridElement;

import at.mic.dddrt.db.model.TableColumn;

public class EntityProperty extends FieldProperty {
	private static final long serialVersionUID = 6298444413188448662L;

	public static EntityProperty createFromJSON(JSONObject property) {
		try {
			String visibility = property.getString(JSON_VISIBILITY);
			String type = property.getString(JSON_TYPE);
			String name = property.getString(JSON_NAME);
			boolean idProperty = property.getBoolean(JSON_IDPROPERTY);
			return new EntityProperty(visibility, type, name, idProperty);
		} catch (Exception ex) {
			return new EntityProperty();
		}
	}

	public static EntityProperty createFromDatabaseColumn(TableColumn column) {
		try {
			String visibility = "private";
			String type = column.getColumnType();
			String name = column.getColumnName();
			boolean idProperty = column.isIDColumn();
			return new EntityProperty(visibility, type, name, idProperty);
		} catch (Exception ex) {
			return new EntityProperty();
		}
	}

	protected EntityProperty() {
		super();
	}

	protected EntityProperty(String propertyVisibility,
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
		java.awt.Component[] container = CurrentDiagram.getInstance().getDiagramHandler().getDrawPanel().getComponents();
		for (java.awt.Component cont : container) {
			if (cont instanceof ComponentSwing) {
				ComponentSwing cw = (ComponentSwing) cont;
				NewGridElement gridElement = cw.getGridElement();

				if (gridElement instanceof FieldComposite) {
					FieldComposite ec = (FieldComposite) gridElement;
					if (getParentFieldComposite().isInSameBoundedContext((FieldComposite) gridElement)) {
						sameBoundedContextElements.add(ec);
					}
					else {
						diffrentBoundedContextElements.add(ec);
					}
				}
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
