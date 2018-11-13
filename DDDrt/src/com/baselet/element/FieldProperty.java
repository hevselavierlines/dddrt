package com.baselet.element;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComboBox;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;

import org.json.JSONObject;

public class FieldProperty extends JLayeredPane {

	private static String IDENTIFIER = "prop";
	private static final long serialVersionUID = -6900199799847961883L;
	private final JTextField propertyName;
	private final JComboBox<String> propertyType;
	private final JComboBox<String> propertyVisibility;
	private final static String UNIQUE_ID = "UUID";
	public final static int HEIGHT = 30;
	private final int[] WIDTHS = { 40, 70, -1 };

	public static FieldProperty createFromString(String line) {
		String[] split = line.split(";");
		if (split.length == 4) {
			return new FieldProperty(split[1], split[2], split[3]);
		}
		else {
			return null;
		}
	}

	public static FieldProperty createFromJSON(JSONObject property) {
		try {
			String visibility = property.getString("visibility");
			String type = property.getString("type");
			String name = property.getString("name");
			return new FieldProperty(visibility, type, name);
		} catch (Exception ex) {
			return new FieldProperty();
		}
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(IDENTIFIER).append(';')
				.append(getPropertyVisibility()).append(';')
				.append(getPropertyType()).append(';')
				.append(getPropertyName());
		return stringBuilder.toString();
	}

	public JSONObject exportToJSON() {
		JSONObject ret = new JSONObject();
		ret.put("visibility", getPropertyVisibility());
		ret.put("type", getPropertyType());
		ret.put("name", getPropertyName());
		return ret;
	}

	public FieldProperty() {
		propertyVisibility = new JComboBox<String>();
		propertyVisibility.addItem("-");
		propertyVisibility.addItem(" ");
		propertyVisibility.addItem("#");
		propertyVisibility.addItem("+");
		propertyVisibility.setBackground(Color.ORANGE);
		add(propertyVisibility);

		propertyType = new JComboBox<String>();
		propertyType.addItem("String");
		propertyType.addItem("int");
		propertyType.addItem("long");
		propertyType.addItem("byte");
		propertyType.addItem("char");
		propertyType.addItem("short");
		propertyType.addItem("Object");
		propertyType.setEditable(false);
		propertyType.getEditor().getEditorComponent().setBackground(Color.orange);
		add(propertyType);

		propertyName = new JTextField("newProperty");
		add(propertyName);
	}

	public FieldProperty(String propertyVisibility, String propertyType, String propertyName) {
		this();
		setPropertyVisibility(propertyVisibility);
		setPropertyType(propertyType);
		setPropertyName(propertyName);
	}

	public String getPropertyName() {
		return propertyName.getText();
	}

	public void setPropertyName(String propertyName) {
		this.propertyName.setText(propertyName);
	}

	public String getPropertyType() {
		return propertyType.getSelectedItem().toString();
	}

	public void setPropertyType(String propertyType) {
		if (UNIQUE_ID.equals(propertyType)) {
			this.propertyType.addItem("UUID");
		}
		this.propertyType.setSelectedItem(propertyType);
	}

	public String getPropertyVisibility() {
		return propertyVisibility.getSelectedItem().toString();
	}

	public void setPropertyVisibility(String propertyVisibility) {
		this.propertyVisibility.setSelectedItem(propertyVisibility);
	}

	@Override
	public void paint(Graphics g) {
		propertyVisibility.setBounds(0, 0, WIDTHS[0], HEIGHT);
		propertyType.setBounds(WIDTHS[0], 0, WIDTHS[1], HEIGHT);
		propertyName.setBounds(WIDTHS[0] + WIDTHS[1], 0, WIDTHS[2] == -1 ? getBounds().width - (WIDTHS[1] + WIDTHS[2]) : WIDTHS[2], HEIGHT);
		super.paint(g);
	}
}
