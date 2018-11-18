package com.baselet.element;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;

import org.json.JSONObject;

import com.baselet.design.metal.MetalComboBox;
import com.baselet.design.metal.VisibilityComboBox;

public class FieldProperty extends JLayeredPane implements ActionListener {

	private static final String JSON_IDPROPERTY = "idproperty";
	private static final String JSON_NAME = "name";
	private static final String JSON_TYPE = "type";
	private static final String JSON_VISIBILITY = "visibility";
	private static String IDENTIFIER = "prop";
	private static final long serialVersionUID = -6900199799847961883L;
	private final JTextField propertyName;
	private final JComboBox<String> propertyType;
	private final VisibilityComboBox propertyVisibility;
	private final JButton removeButton;
	private boolean idProperty;
	private final static String UNIQUE_ID = "UUID";
	public final static int HEIGHT = 30;
	private final int[] WIDTHS = { 40, 80, -1, 40 };
	private ActionListener removeListener;

	public static FieldProperty createFromJSON(JSONObject property) {
		try {
			String visibility = property.getString(JSON_VISIBILITY);
			String type = property.getString(JSON_TYPE);
			String name = property.getString(JSON_NAME);
			boolean idProperty = property.getBoolean(JSON_IDPROPERTY);
			return new FieldProperty(visibility, type, name, idProperty);
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
		ret.put(JSON_VISIBILITY, getPropertyVisibility());
		ret.put(JSON_TYPE, getPropertyType());
		ret.put(JSON_NAME, getPropertyName());
		ret.put(JSON_IDPROPERTY, idProperty);
		return ret;
	}

	public FieldProperty() {
		propertyVisibility = new VisibilityComboBox();
		add(propertyVisibility);

		propertyType = new MetalComboBox();
		propertyType.addItem("String");
		propertyType.addItem("int");
		propertyType.addItem("long");
		propertyType.addItem("byte");
		propertyType.addItem("char");
		propertyType.addItem("short");
		propertyType.addItem("Object");
		propertyType.addItem("List");
		propertyType.setEditable(true);
		add(propertyType);

		propertyName = new JTextField("newProperty");
		add(propertyName);

		removeButton = new JButton("x");
		removeButton.addActionListener(this);
		add(removeButton);
	}

	public FieldProperty(String propertyVisibility,
			String propertyType,
			String propertyName,
			boolean idProperty) {
		this();
		if (idProperty) {
			this.remove(removeButton);
		}
		this.idProperty = idProperty;
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
		if (idProperty) {
			this.propertyType.addItem(UNIQUE_ID);
		}
		this.propertyType.setSelectedItem(propertyType);
	}

	public String getPropertyVisibility() {
		return propertyVisibility.getSelectedItem().toString();
	}

	public void setPropertyVisibility(String propertyVisibility) {
		this.propertyVisibility.setSelection(propertyVisibility);
	}

	@Override
	public void paint(Graphics g) {
		propertyVisibility.setBounds(0, 0, WIDTHS[0], HEIGHT);
		propertyType.setBounds(WIDTHS[0], 0, WIDTHS[1], HEIGHT);
		propertyName.setBounds(WIDTHS[0] + WIDTHS[1], 0, WIDTHS[2] == -1 ? getBounds().width - (WIDTHS[0] + WIDTHS[1] + WIDTHS[3]) : WIDTHS[2], HEIGHT);
		if (!idProperty) {
			removeButton.setBounds(getBounds().width - WIDTHS[3], 0, WIDTHS[3], HEIGHT);
		}
		super.paint(g);
	}

	public void setRemovedListener(ActionListener actionListener) {
		removeListener = actionListener;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (removeListener != null) {
			removeListener.actionPerformed(new ActionEvent(this, 0, "removed"));
		}
	}
}
