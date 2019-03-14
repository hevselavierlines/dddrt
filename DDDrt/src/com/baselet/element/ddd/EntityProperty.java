package com.baselet.element.ddd;

import java.awt.Graphics;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.baselet.design.metal.PrimaryKeyButton;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.element.TableCellPrimaryKeyBinding;

import at.mic.dddrt.db.model.TableColumn;

public class EntityProperty extends FieldProperty {
	private static final long serialVersionUID = 6298444413188448662L;

	public static EntityProperty createFromJSON(JSONObject property) {
		try {
			String visibility = property.getString(JSON_VISIBILITY);
			String type = property.getString(JSON_TYPE);
			String name = property.getString(JSON_NAME);
			String dbName = property.getString(JSON_DATABASE_NAME);
			boolean idProperty = property.getBoolean(JSON_IDPROPERTY);
			return new EntityProperty(visibility, type, name, idProperty, dbName);
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
			return new EntityProperty(visibility, type, name, idProperty, name);
		} catch (Exception ex) {
			return new EntityProperty();
		}
	}

	public void createPrimaryKey() {
		try {
			if (idProperty) {
				keyButton.setIcon(new PrimaryKeyButton(HEIGHT, HEIGHT));
				properties.addProperty("Primary Key", "true");
			}
			else {
				properties.addProperty("Primary Key", "false");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		new TableCellPrimaryKeyBinding(properties.getTableModel(), this, "Primary Key");
	}

	protected EntityProperty() {
		super();
		createPrimaryKey();
	}

	protected EntityProperty(String propertyVisibility,
			String propertyType,
			String propertyName,
			boolean idProperty, String dbName) {
		super(propertyVisibility, propertyType, propertyName, idProperty, dbName);
		createPrimaryKey();
	}

	@Override
	protected void addPropertyTypes() {
		Object selection = elementType.getSelectedItem();
		elementType.removeAllItems();
		List<FieldComposite> sameBoundedContextElements = new LinkedList<FieldComposite>();
		List<FieldComposite> diffrentBoundedContextElements = new LinkedList<FieldComposite>();

		for (FieldComposite ec : CurrentDiagram
				.getInstance()
				.getDiagramHandler()
				.getDrawPanel()
				.getHelperAndSub(FieldComposite.class)) {
			if (getParentFieldComposite().isInSameBoundedContext(ec)) {
				sameBoundedContextElements.add(ec);
			}
			else {
				diffrentBoundedContextElements.add(ec);
			}

		}

		Collections.sort(sameBoundedContextElements);
		Collections.sort(diffrentBoundedContextElements);
		// SINGLE TYPES
		for (FieldComposite fc : sameBoundedContextElements) {
			elementType.addItem(fc.getName(), fc);
		}
		for (String DEFAULT_TYPE : DEFAULT_TYPES) {
			elementType.addItem(DEFAULT_TYPE);
		}
		for (FieldComposite fc : diffrentBoundedContextElements) {
			elementType.addItem(fc.getFullName(), fc);
		}
		// COLLECTION TYPES
		for (FieldComposite fc : sameBoundedContextElements) {
			elementType.addItem(fc.getName(), fc, true);
		}
		for (String DEFAULT_TYPE : DEFAULT_TYPES) {
			elementType.addItem(DEFAULT_TYPE, true);
		}
		for (FieldComposite fc : diffrentBoundedContextElements) {
			elementType.addItem(fc.getFullName(), fc, true);
		}
		if (selection != null) {
			elementType.setSelectedItem(selection);
		}
		else {
			elementType.setSelectedIndex(0);
		}
	}

	@Override
	public void setZoomLevel(double zoomLevel) {
		super.setZoomLevel(zoomLevel);

		if (idProperty) {
			keyButton.setIcon(new PrimaryKeyButton(HEIGHT, HEIGHT));
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}

}
