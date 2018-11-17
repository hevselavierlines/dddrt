package com.baselet.element;

import java.awt.Graphics;

import javax.swing.JComboBox;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;

import org.json.JSONObject;

import com.baselet.design.metal.MetalComboBox;
import com.baselet.design.metal.VisibilityComboBox;

public class FieldMethod extends JLayeredPane {
	private static final long serialVersionUID = -6900199799847961884L;
	private final JTextField methodName;
	private final JComboBox<String> methodType;
	private final VisibilityComboBox methodVisibility;
	public final static int HEIGHT = 50;
	public final static int HALF_HEIGHT = HEIGHT / 2;
	private final int[] WIDTHS = { 40, 70, -1 };
	private final JTextField textParameters;

	public FieldMethod() {
		methodVisibility = new VisibilityComboBox();
		add(methodVisibility);

		methodType = new MetalComboBox();
		methodType.addItem("void");
		methodType.addItem("String");
		methodType.addItem("int");
		methodType.addItem("long");
		methodType.addItem("byte");
		methodType.addItem("char");
		methodType.addItem("short");
		methodType.addItem("Object");
		methodType.setEditable(true);
		add(methodType);

		methodName = new JTextField("newmethod");
		add(methodName);

		textParameters = new JTextField("()");
		add(textParameters);
	}

	public FieldMethod(String methodVisibility, String methodType, String methodName, String parameters) {
		this();
		setMethodVisibility(methodVisibility);
		setMethodType(methodType);
		setMethodName(methodName);
		setMethodParameters(parameters);
	}

	@Override
	public void paint(Graphics g) {
		methodVisibility.setBounds(0, 0, WIDTHS[0], HALF_HEIGHT);
		methodType.setBounds(WIDTHS[0], 0, WIDTHS[1], HALF_HEIGHT);
		methodName.setBounds(WIDTHS[0] + WIDTHS[1], 0, WIDTHS[2] == -1 ? getBounds().width - (WIDTHS[1] + WIDTHS[2]) : WIDTHS[2], HALF_HEIGHT);
		textParameters.setBounds(0, 20, (int) getBounds().getWidth(), 30);

		super.paint(g);
	}

	public static FieldMethod createFromJSON(JSONObject method) {
		try {
			String visibility = method.getString("visibility");
			String type = method.getString("type");
			String name = method.getString("name");
			String parameters = method.getString("parameters");
			return new FieldMethod(visibility, type, name, parameters);
		} catch (Exception ex) {
			return new FieldMethod();
		}
	}

	public JSONObject exportToJSON() {
		JSONObject ret = new JSONObject();
		ret.put("visibility", getMethodVisibility());
		ret.put("type", getMethodType());
		ret.put("name", getMethodName());
		ret.put("parameters", getMethodParameters());
		return ret;
	}

	public String getMethodName() {
		return methodName.getText();
	}

	public void setMethodName(String methodName) {
		this.methodName.setText(methodName);
	}

	public String getMethodType() {
		return methodType.getSelectedItem().toString();
	}

	public void setMethodType(String methodType) {
		this.methodType.setSelectedItem(methodType);
	}

	public String getMethodVisibility() {
		return methodVisibility.getSelectedItem().toString();
	}

	public void setMethodVisibility(String methodVisibility) {
		this.methodVisibility.setSelection(methodVisibility);
	}

	public String getMethodParameters() {
		return textParameters.getText();
	}

	public void setMethodParameters(String parameters) {
		textParameters.setText(parameters);
	}
}
