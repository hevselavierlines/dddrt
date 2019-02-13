package com.baselet.element.ddd;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.baselet.diagram.CurrentDiagram;

import at.mic.dddrt.db.model.TableColumn;

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

	public static ValueObjectProperty createFromDatabaseColumn(TableColumn column) {
		try {
			String visibility = "private";
			String type = column.getColumnType();
			String name = column.getColumnName();
			boolean idProperty = column.isIDColumn();
			return new ValueObjectProperty(visibility, type, name, idProperty);
		} catch (Exception ex) {
			return new ValueObjectProperty();
		}
	}

	protected ValueObjectProperty() {
		super();
		init();
	}

	protected ValueObjectProperty(String propertyVisibility,
			String propertyType,
			String propertyName,
			boolean idProperty) {
		super(propertyVisibility, propertyType, propertyName, idProperty);
		init();
	}

	private void init() {
		FIXED_WIDTHS[0] = 0;
	}

	@Override
	protected void addPropertyTypes() {
		Object selection = elementType.getSelectedItem();
		elementType.removeAllItems();
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
			elementType.addItem(fc.getName(), fc);
		}
		for (String DEFAULT_TYPE : DEFAULT_TYPES) {
			elementType.addItem(DEFAULT_TYPE);
		}
		for (FieldComposite fc : diffrentBoundedContextElements) {
			elementType.addItem(fc.getFullName(), fc);
		}

		if (selection != null) {
			elementType.setSelectedItem(selection);
		}
		else {
			elementType.setSelectedIndex(0);
		}
	}

}
