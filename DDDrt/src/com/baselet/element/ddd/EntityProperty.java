package com.baselet.element.ddd;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.json.JSONObject;

import com.baselet.diagram.CurrentDiagram;
import com.baselet.element.TableCellPrimaryKeyBinding;

import at.mic.dddrt.db.model.TableColumn;

public class EntityProperty extends FieldProperty {
	private static final long serialVersionUID = 6298444413188448662L;
	public static Image PRIMARY_KEY_ICON;

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

	public void createPrimaryKey() {
		try {
			if (PRIMARY_KEY_ICON == null) {
				PRIMARY_KEY_ICON = ImageIO.read(new File("img/primarykey.png"));
			}
			primaryKeyIcon = PRIMARY_KEY_ICON.getScaledInstance(HEIGHT, HEIGHT, Image.SCALE_FAST);
			if (idProperty && primaryKeyIcon != null) {
				keyButton.setIcon(new ImageIcon(primaryKeyIcon));
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
			boolean idProperty) {
		super(propertyVisibility, propertyType, propertyName, idProperty);
		createPrimaryKey();
	}

	@Override
	protected void addPropertyTypes() {
		Object selection = propertyType.getSelectedItem();
		propertyType.removeAllItems();
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

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		if (idProperty && PRIMARY_KEY_ICON != null) {
			Image img = PRIMARY_KEY_ICON.getScaledInstance(HEIGHT, HEIGHT, Image.SCALE_FAST);
			keyButton.setIcon(new ImageIcon(img));
		}
	}

	@Override
	public void print(Graphics g) {
		if (idProperty && PRIMARY_KEY_ICON != null) {
			Image img = PRIMARY_KEY_ICON.getScaledInstance(HEIGHT, HEIGHT, Image.SCALE_FAST);
			keyButton.setIcon(new ImageIcon(img));
		}
	}

}
