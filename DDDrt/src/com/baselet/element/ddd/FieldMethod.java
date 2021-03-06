package com.baselet.element.ddd;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;

import org.json.JSONObject;

import com.baselet.design.metal.DDDRoundBorder;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.element.ComponentSwing;
import com.baselet.element.NewGridElement;
import com.baselet.gui.command.ComboBoxChange;
import com.baselet.gui.command.Controller;
import com.baselet.gui.command.TextFieldChange;

public class FieldMethod extends FieldElement implements ActionListener, DocumentListener, FocusListener, PopupMenuListener {
	private static final long serialVersionUID = -6900199799847961884L;

	public static class Builder {
		/* readMethod.setMethodVisibility("#"); readMethod.setMethodName("read"); readMethod.setMethodType(fieldComp.getName()); */
		private final String visibility;
		private final String name;
		private final String type;
		private final List<String> parameters;

		public Builder(String visibility, String name, String type) {
			this.visibility = visibility;
			this.name = name;
			this.type = type;
			parameters = new LinkedList<String>();
		}

		public Builder addParameter(String type, String name) {
			parameters.add(name);
			parameters.add(type);
			return this;
		}

		public FieldMethod build() {
			StringBuilder parameterList = new StringBuilder();
			parameterList.append('(');
			for (int i = 0; i < parameters.size(); i += 2) {
				parameterList.append(parameters.get(i));
				parameterList.append(": ");
				parameterList.append(parameters.get(i + 1));
				parameterList.append(", ");
			}
			if (parameters.size() > 0) {
				parameterList.delete(parameterList.length() - 2, parameterList.length());
			}
			parameterList.append(')');
			return new FieldMethod(visibility, type, name, parameterList.toString());
		}
	}

	public final static int DEFAULT_HEIGHT = 40;
	public final static int DEFAULT_HALF_HEIGHT = DEFAULT_HEIGHT / 2;
	public final static int DEFAULT_FONT_SIZE = 12;
	private int FULL_HEIGHT = 40;
	private int HALF_HEIGHT = FULL_HEIGHT / 2;
	private final int[] PERCENT_WIDTHS = { -1, 60, 40, -1 };
	private final int[] FIXED_WIDTHS = { 40, 30 };
	private final JTextField textParameters;
	private ActionListener removeListener;
	private FieldComposite parentFieldComposite;
	private String originalString;
	private Object originalSelection;
	private double currentZoomLevel;
	private static Image deleteButton;

	public FieldMethod() {
		super();

		elementVisibility.addPopupMenuListener(this);
		elementVisibility.setFont(elementFont);
		add(elementVisibility);

		elementType.setFont(elementFont);
		List<String> defaultTypes = FieldProperty.loadDefaultTypes();
		defaultTypes.add(0, "void");
		for (String defaultType : defaultTypes) {
			elementType.addItem(defaultType);
		}
		elementType.setEditable(true);
		final JTextComponent tc = (JTextComponent) elementType.getEditor().getEditorComponent();
		tc.addFocusListener(this);
		add(elementType);

		elementName.setFont(elementFont);
		elementName.getDocument().addDocumentListener(this);
		elementName.addFocusListener(this);
		add(elementName);

		textParameters = new JTextField("()");
		textParameters.setFont(elementFont);
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
		textParameters.addFocusListener(this);
		textParameters.setBorder(new DDDRoundBorder());
		textParameters.setBackground(new Color(0, 0, 0, 0));
		textParameters.setOpaque(false);
		add(textParameters);

		try {
			if (deleteButton == null) {
				deleteButton = ImageIO.read(new File("img/x-button.png"));
			}
			Image img = deleteButton.getScaledInstance(HALF_HEIGHT, HALF_HEIGHT, Image.SCALE_FAST);
			elementRemove.setIcon(new ImageIcon(img));
			elementRemove.setBorderPainted(false);
			elementRemove.setFocusPainted(false);
			elementRemove.setContentAreaFilled(false);
		} catch (Exception ex) {
			elementRemove.setText("X");
			elementRemove.setFont(elementFont);
			System.out.println(ex);
		}
		elementRemove.addActionListener(this);
		add(elementRemove);
	}

	public FieldMethod(String methodVisibility, String methodType, String methodName, String parameters) {
		this();
		setMethodVisibility(methodVisibility);
		setMethodType(methodType);
		setMethodName(methodName);
		setMethodParameters(parameters);
	}

	public int getFieldHeight() {
		return FULL_HEIGHT + 2;
	}

	@Override
	protected void updateCoordinates(Graphics g, int width) {
		int[] realWidths = new int[PERCENT_WIDTHS.length];
		int[] fixedWidths = new int[FIXED_WIDTHS.length];
		for (int i = 0; i < fixedWidths.length; i++) {
			fixedWidths[i] = (int) (currentZoomLevel * FIXED_WIDTHS[i]);
		}
		int percentFullWidth = width - fixedWidths[0] - fixedWidths[1];
		for (int i = 0; i < realWidths.length; i++) {
			if (PERCENT_WIDTHS[i] > 0) {
				realWidths[i] = (int) (percentFullWidth * ((double) PERCENT_WIDTHS[i] / 100));
			}
		}
		realWidths[0] = fixedWidths[0];
		realWidths[3] = fixedWidths[1];
		elementVisibility.setBounds(0, 0, realWidths[0], HALF_HEIGHT);
		elementName.setBounds(realWidths[0], 0, realWidths[1], HALF_HEIGHT);
		if (g != null) {
			g.drawString(":", realWidths[0] + realWidths[1], 15);
		}
		elementType.setBounds(realWidths[0] + realWidths[1] + 5, 0, realWidths[2], HALF_HEIGHT);
		elementRemove.setBounds(width - realWidths[3] + 5, 0, realWidths[3] - 5, HALF_HEIGHT);
		textParameters.setBounds(0, HALF_HEIGHT, (int) getBounds().getWidth(), HALF_HEIGHT);
	}

	@Override
	public void paint(Graphics g) {
		updateCoordinates(g, getBounds().width);
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
		return elementName.getText();
	}

	public void setMethodName(String methodName) {
		elementName.setText(methodName);
	}

	public String getMethodType() {
		return elementType.getSelectedItem().toString();
	}

	public void setMethodType(String methodType) {
		elementType.setSelectedItem(methodType);
	}

	public String getMethodVisibility() {
		return elementVisibility.getSelectedItem().toString();
	}

	public void setMethodVisibility(String methodVisibility) {
		elementVisibility.setSelection(methodVisibility);
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

	public List<Parameter> getParameters() throws Exception {
		return parseParameters();
	}

	public String validateParameters() {
		try {
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

		protected String getType() {
			return type;
		}

		protected String getName() {
			return name;
		}
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

	public boolean setNameValidity(FieldMethod previous) {
		StringBuffer errorMessage = new StringBuffer();
		if (previous != null) {
			errorMessage.append("Duplicated Name: ").append(getMethodName());
		}
		boolean validateName = VariableNameHelper.validateVariableName(getMethodName());
		if (!validateName) {
			errorMessage.append("Invalid Name: ").append(getMethodName());
		}

		if (errorMessage.length() == 0) {
			elementName.setForeground(Color.BLACK);
			elementName.setToolTipText(null);
			return true;
		}
		else {
			elementName.setForeground(Color.RED);
			elementName.setToolTipText("Invalid name: " + getMethodName());
			return false;
		}
	}

	@Override
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
			fc.validateElementNames();
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

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		if (e.getSource() == elementVisibility) {
			Controller controller = CurrentDiagram.getInstance().getDiagramHandler().getController();
			controller.executeCommand(new ComboBoxChange((JComboBox<?>) e.getSource(), originalSelection));
		}
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		if (e.getSource() == elementVisibility) {
			originalSelection = ((JComboBox<?>) e.getSource()).getSelectedItem();
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
		if (source instanceof JTextField) {
			originalString = ((JTextField) source).getText();
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
		if (source instanceof JTextField) {
			String newText = ((JTextField) source).getText();
			if (newText != null && !newText.equals(originalString)) {
				getParentFieldComposite()
						.getComponent()
						.getController()
						.executeCommand(
								new TextFieldChange(
										(JTextField) source,
										originalString));
			}
		}
	}

	@Override
	public void setZoomLevel(double zoomLevel) {
		currentZoomLevel = zoomLevel;

		FULL_HEIGHT = (int) (zoomLevel * DEFAULT_HEIGHT);
		HALF_HEIGHT = FULL_HEIGHT / 2;
		super.setZoomLevel(zoomLevel);

		textParameters.setFont(elementFont);
	}
}
