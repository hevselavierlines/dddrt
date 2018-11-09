package com.baselet.element;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComboBox;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;

public class EntityProperty extends JLayeredPane {

	private static String IDENTIFIER = "prop";
	private static final long serialVersionUID = -6900199799847961883L;
	private final JTextField propertyName;
	private final JComboBox<String> propertyType;
	private final JComboBox<String> propertyVisibility;
	private final int HEIGHT = 40;
	private final int[] WIDTHS = { 40, 70, -1 };

	public static EntityProperty createFromString(String line) {
		String[] split = line.split(";");
		if (split.length == 4) {
			return new EntityProperty(split[1], split[2], split[3]);
		}
		else {
			return null;
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

	public EntityProperty() {
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

	public EntityProperty(String propertyVisibility, String propertyType, String propertyName) {
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
