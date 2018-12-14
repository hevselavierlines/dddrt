package com.baselet.element.ddd;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.json.JSONObject;

import com.baselet.design.metal.MetalComboBox;
import com.baselet.design.metal.VisibilityComboBox;
import com.baselet.element.ComponentSwing;
import com.baselet.element.NewGridElement;

public class FieldMethod extends JLayeredPane implements ActionListener, DocumentListener {
	private static final long serialVersionUID = -6900199799847961884L;
	private final JTextField methodName;
	private final JComboBox<String> methodType;
	private final VisibilityComboBox methodVisibility;
	public final static int HEIGHT = 50;
	public final static int HALF_HEIGHT = HEIGHT / 2;
	private final int[] WIDTHS = { 40, -1, 80, 40 };
	private final JTextField textParameters;
	private final JButton removeButton;
	private ActionListener removeListener;
	private FieldComposite parentFieldComposite;

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
		methodName.getDocument().addDocumentListener(this);
		add(methodName);

		textParameters = new JTextField("()");
		textParameters.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				validateParamters();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				validateParamters();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				validateParameters();
			}
		});
		add(textParameters);

		removeButton = new JButton("x");
		removeButton.addActionListener(this);
		add(removeButton);
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
		int nameWidth = getBounds().width - WIDTHS[0] - WIDTHS[2] - WIDTHS[3];
		methodName.setBounds(WIDTHS[0], 0, nameWidth, HALF_HEIGHT);
		methodType.setBounds(getBounds().width - WIDTHS[3] - WIDTHS[2], 0, WIDTHS[2], HALF_HEIGHT);
		removeButton.setBounds(getBounds().width - WIDTHS[3], 0, WIDTHS[3], HALF_HEIGHT);

		textParameters.setBounds(0, HALF_HEIGHT, (int) getBounds().getWidth(), HALF_HEIGHT);
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

	public void setRemovedListener(ActionListener actionListener) {
		removeListener = actionListener;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (removeListener != null) {
			removeListener.actionPerformed(new ActionEvent(this, 0, FieldProperty.REMOVED_COMMAND));
		}
	}

	public String validateParameters() {
		try {
			// SwingUtilities.invokeLater(new Runnable() {
			//
			// @Override
			// public void run() {
			// if (!getMethodParameters().startsWith("(")) {
			// textParameters.setText("(" + textParameters.getText().replaceAll("\\(", ""));
			// }
			// if (!getMethodParameters().endsWith(")")) {
			// textParameters.setText(textParameters.getText().replaceAll("\\)", "") + ")");
			// }
			// }
			// });

			Set<String> names = new HashSet<String>();
			for (Parameter parameter : parseParameters()) {
				boolean unique = names.add(parameter.name);
				if (!unique) {
					throw new Exception(parameter.name + " has duplicates. Please rename them.");
				}
			}
			return null;
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}

	protected List<Parameter> parseParameters() throws Exception {
		List<Parameter> params = new LinkedList<Parameter>();
		String paramString = getMethodParameters().replaceAll("\\(", "").replaceAll("\\)", "");
		String[] splitParams = paramString.split(",");
		int paramNumber = 1;
		if (!paramString.trim().isEmpty()) {
			for (String splitPara : splitParams) {
				Parameter parameter = new Parameter();
				if (splitPara.contains(":")) {
					String[] parameterElements = splitPara.split(":");
					if (parameterElements.length == 2) {
						parameter.name = parameterElements[0].trim();
						parameter.type = parameterElements[1].trim();
					}
					else {
						parameter = null;
						throw new Exception("Paramter " + paramNumber + ": Type and name not found (at " + parameterElements[0] + ")");
					}
				}
				else {
					String[] parameterElements = splitPara.split(" ");
					if (parameterElements.length >= 2) {
						for (String parameterElement : parameterElements) {
							if (parameterElement.length() > 0) {
								if (parameter.type == null) {
									parameter.type = parameterElement.trim();
								}
								else {
									parameter.name = parameterElement.trim();
								}
							}
						}
						if (parameter.name == null || parameter.type == null) {
							throw new Exception("Parameter " + paramNumber + ": Type or name not found");
						}
					}
					else {
						parameter = null;
						throw new Exception("Parameter " + paramNumber + ": Type and name not found (at " + parameterElements[0] + ")");
					}
				}
				params.add(parameter);
				paramNumber++;
			}
		}
		return params;
	}

	protected static class Parameter {
		private String type;
		private String name;
	}

	protected void validateParamters() {
		String validationResult = validateParameters();
		if (validationResult == null) {
			textParameters.setForeground(Color.BLACK);
			textParameters.setToolTipText(null);
		}
		else {
			textParameters.setForeground(Color.RED);
			textParameters.setToolTipText(validationResult);
		}
	}

	public void setNameValidity(FieldMethod previous) {
		if (previous != null) {
			methodName.setBackground(Color.WHITE);
			methodName.setForeground(Color.RED);
			methodName.setToolTipText("Duplicated name " + previous.getMethodName());
		}
		else {
			methodName.setBackground(Color.WHITE);
			methodName.setForeground(Color.BLACK);
			methodName.setToolTipText(null);
		}
	}

	public FieldComposite getParentFieldComposite() {
		if (parentFieldComposite != null) {
			return parentFieldComposite;
		}
		NewGridElement element = getParentFieldCompositeRecursively(this);
		if (element instanceof FieldComposite) {
			return (FieldComposite) element;
		}
		else {
			return null;
		}
	}

	private NewGridElement getParentFieldCompositeRecursively(java.awt.Component currentComponent) {
		if (currentComponent != null) {
			if (currentComponent instanceof ComponentSwing) {
				ComponentSwing swing = (ComponentSwing) currentComponent;
				return swing.getGridElement();
			}
			else {
				return getParentFieldCompositeRecursively(currentComponent.getParent());
			}
		}
		else {
			return null;
		}
	}

	private void updateValidation() {
		FieldComposite fc = getParentFieldComposite();
		if (fc != null) {
			fc.validateNames();
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		updateValidation();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		updateValidation();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		updateValidation();
	}
}
