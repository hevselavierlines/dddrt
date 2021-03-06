package com.baselet.element.ddd;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;

import org.json.JSONObject;

import com.baselet.control.basics.geom.Rectangle;
import com.baselet.design.metal.DataTypeItem;
import com.baselet.design.metal.DeleteButton;
import com.baselet.design.metal.PrimaryKeyButton;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.diagram.DrawPanel;
import com.baselet.element.PropertiesGridElement;
import com.baselet.element.TableCellTextFieldBinding;
import com.baselet.element.relation.DDDRelation;
import com.baselet.gui.command.ComboBoxChange;
import com.baselet.gui.command.Controller;
import com.baselet.gui.command.PropertyDataTypeChange;
import com.baselet.gui.command.TextFieldChange;

public abstract class FieldProperty extends FieldElement implements ActionListener, PopupMenuListener, FocusListener, DocumentListener {

	protected static final String REMOVED_COMMAND = "removed";
	protected static final String JSON_IDPROPERTY = "idproperty";
	protected static final String JSON_NAME = "name";
	protected static final String JSON_TYPE = "type";
	protected static final String JSON_VISIBILITY = "visibility";
	protected static final String JSON_DATABASE_NAME = "dbname";
	protected static String IDENTIFIER = "prop";
	private static final long serialVersionUID = -6900199799847961883L;

	protected final JButton keyButton;
	protected boolean idProperty;
	protected final static String UNIQUE_ID = "UUID";
	protected int HEIGHT = 20;
	private final JButton leftConnectionButton;
	private final JButton rightConnectionButton;
	private final int[] PERCENT_WIDTHS = { -1, -1, -1, 60, 40, -1, -1 };
	protected final int RING_WIDTH = 10;
	protected int[] FIXED_WIDTHS = { RING_WIDTH, 20, 40, 25, RING_WIDTH };
	private ActionListener removeListener;
	protected final List<String> DEFAULT_TYPES;
	private DDDRelation relationLineRef;
	private String originalString;
	private Object originalSelection;
	private final JTextComponent propertyTypeEditor;
	private boolean selection;
	protected final PropertiesGridElement properties;
	private String databaseName;

	public int getFieldHeight() {
		return HEIGHT + 3;
	}

	public boolean isSelected() {
		return selection;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(IDENTIFIER).append(';')
				.append(getPropertyVisibility()).append(';')
				.append(getPropertyType()).append(';')
				.append(getPropertyName()).append(getDatabaseName()).append(';');
		return stringBuilder.toString();
	}

	public JSONObject exportToJSON() {
		JSONObject ret = new JSONObject();
		ret.put(JSON_VISIBILITY, getPropertyVisibility());
		ret.put(JSON_TYPE, getPropertyType());
		ret.put(JSON_NAME, getPropertyName());
		ret.put(JSON_DATABASE_NAME, getDatabaseName());
		ret.put(JSON_IDPROPERTY, idProperty);
		return ret;
	}

	public static List<String> loadDefaultTypes() {
		List<String> types = new LinkedList<String>();
		types.add("String");
		types.add("UUID");
		types.add("int");
		types.add("boolean");
		types.add("long");
		types.add("byte");
		types.add("char");
		types.add("float");
		types.add("double");
		types.add("short");
		types.add("Date");
		types.add("Object");
		return types;
	}

	public FieldProperty() {
		super();

		keyButton = new JButton("");
		keyButton.setBorderPainted(false);
		keyButton.setFocusPainted(false);
		keyButton.setContentAreaFilled(false);
		keyButton.addActionListener(this);
		add(keyButton);

		elementVisibility.setFont(elementFont);
		elementVisibility.addPopupMenuListener(this);
		add(elementVisibility);

		DEFAULT_TYPES = loadDefaultTypes();
		elementType.setEditable(true);
		elementType.addPopupMenuListener(this);
		propertyTypeEditor = (JTextComponent) elementType.getEditor().getEditorComponent();
		propertyTypeEditor.addFocusListener(this);

		elementType.setFont(elementFont);
		elementType.setSelectedItem("String");
		add(elementType);

		elementName.setFont(elementFont);
		elementName.addFocusListener(this);
		elementName.getDocument().addDocumentListener(this);
		add(elementName);

		elementRemove.setIcon(new DeleteButton(HEIGHT, HEIGHT));
		elementRemove.setBorderPainted(false);
		elementRemove.setFocusPainted(false);
		elementRemove.setContentAreaFilled(false);
		elementRemove.addActionListener(this);
		add(elementRemove);

		leftConnectionButton = new JButton("");
		leftConnectionButton.setIcon(new CircleIcon(10, 10));
		leftConnectionButton.setOpaque(false);
		leftConnectionButton.setBorderPainted(false);
		leftConnectionButton.setFocusPainted(false);
		leftConnectionButton.setContentAreaFilled(false);
		/* implementation for dragging a line from the property to another class. */
		// leftConnectionButton.addMouseMotionListener(new MouseMotionListener() {
		//
		// @Override
		// public void mouseMoved(MouseEvent arg0) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void mouseDragged(MouseEvent arg0) {
		// }
		// });
		add(leftConnectionButton);
		rightConnectionButton = new JButton("");
		rightConnectionButton.setIcon(new CircleIcon(10, 10));
		rightConnectionButton.setOpaque(false);
		rightConnectionButton.setBorderPainted(false);
		rightConnectionButton.setFocusPainted(false);
		rightConnectionButton.setContentAreaFilled(false);
		add(rightConnectionButton);

		updateCoordinates(null, 200);
		properties = new PropertiesGridElement();
		properties.addProperty("Name", elementName.getText(), false);
		properties.addProperty(FieldComposite.DATABASE_NAME, elementName.getText(), false);

		setPropertyName("newProperty");

		TableCellTextFieldBinding
				.createBinding(properties.getTableModel(), elementName, "Name");
		TableCellTextFieldBinding
				.createBinding(properties.getTableModel(), propertyTypeEditor, "Data Type");
	}

	public FieldProperty(String propertyVisibility,
			String propertyType,
			String propertyName,
			boolean idProperty, String dbName) {
		this();
		this.idProperty = idProperty;
		setPropertyVisibility(propertyVisibility);
		setPropertyType(propertyType);
		setPropertyName(propertyName);
		properties.addProperty(FieldComposite.DATABASE_NAME, dbName, true);
	}

	public String getPropertyName() {
		return elementName.getText();
	}

	public void setPropertyName(String propertyName) {
		elementName.setText(propertyName);
		properties.addProperty("Name", propertyName, true);
	}

	public String getFullPropertyType() {
		String ret = null;
		Object selItem = elementType.getSelectedItem();
		if (selItem != null) {
			ret = selItem.toString();
			if (relationLineRef != null) {
				FieldComposite relation = relationLineRef.getEndComposite();
				BoundedContext bc = relation.boundedContext;
				if (bc != null) {
					String packageName = bc.getPackageName(relation) + ".";
					if (ret.startsWith("List<")) {
						int posOfList = ret.indexOf("List<");
						String endType = ret.substring(posOfList + 5);
						ret = "List<" + packageName + endType;
					}
					else {
						ret = packageName + selItem.toString();
					}
				}
			}
		}
		return ret;
	}

	public String getPropertyType() {
		Object selItem = elementType.getSelectedItem();
		if (selItem != null) {
			return selItem.toString();
		}
		else {
			return null;
		}
	}

	public void setPropertyType(String propertyType) {
		elementType.setSelectedItem(propertyType);
	}

	public void setIdProperty(boolean idProperty) {
		if (idProperty == true) {
			FieldProperty oldIdProperty = getParentFieldComposite().getIDProperty();
			if (oldIdProperty != null) {
				oldIdProperty.setIdProperty(false);
			}
		}
		// if (this.idProperty == false && idProperty == true) {
		// DEFAULT_TYPES.add(0, UNIQUE_ID);
		// }
		// else if (this.idProperty == true && idProperty == false) {
		// DEFAULT_TYPES.remove(UNIQUE_ID);
		// }
		this.idProperty = idProperty;

		if (idProperty) {
			setZoomLevel(getParentFieldComposite().getZoom());
			keyButton.setIcon(new PrimaryKeyButton(HEIGHT, HEIGHT));
		}
		else {
			keyButton.setIcon(null);
			properties.addProperty("Primary Key", "false", true);
		}

	}

	public String getPropertyVisibility() {
		return elementVisibility.getSelectedItem().toString();
	}

	public String getPropertyDatabaseName() {
		return properties.getTableProperty(FieldComposite.DATABASE_NAME);
	}

	public void setPropertyVisibility(String propertyVisibility) {
		elementVisibility.setSelection(propertyVisibility);
	}

	@Override
	protected void updateCoordinates(Graphics g, int width) {
		int startY = 2;
		int[] realWidths = new int[PERCENT_WIDTHS.length];
		int[] fixedWidths = new int[FIXED_WIDTHS.length];
		for (int i = 0; i < fixedWidths.length; i++) {
			fixedWidths[i] = (int) (currentZoomLevel * FIXED_WIDTHS[i]);
		}
		int percentFullWidth = width;
		for (int fixedWidth : fixedWidths) {
			percentFullWidth -= fixedWidth;
		}
		int j = 0;
		for (int i = 0; i < realWidths.length; i++) {
			if (PERCENT_WIDTHS[i] > 0) {
				realWidths[i] = (int) (percentFullWidth * ((double) PERCENT_WIDTHS[i] / 100));
			}
			else {
				realWidths[i] = fixedWidths[j];
				j++;
			}
		}
		int offsetX = 0;
		leftConnectionButton.setBounds(offsetX, startY, realWidths[0], HEIGHT);
		offsetX += realWidths[0];
		keyButton.setBounds(offsetX, startY, realWidths[1], HEIGHT);
		offsetX += realWidths[1];
		elementVisibility.setBounds(offsetX, startY, realWidths[2], HEIGHT);
		offsetX += realWidths[2];
		elementName.setBounds(offsetX, startY, realWidths[3], HEIGHT);
		offsetX += realWidths[3];
		if (g != null) {
			g.setFont(elementFont);
			g.drawString(":", offsetX, (int) (currentZoomLevel * 17));
		}
		offsetX += 5;
		elementType.setBounds(offsetX, startY, realWidths[4], HEIGHT);
		offsetX += realWidths[4] - 3;
		// if (!idProperty) {
		elementRemove.setBounds(offsetX, startY, realWidths[5], HEIGHT);
		offsetX += realWidths[5];
		rightConnectionButton.setBounds(width - realWidths[6], startY, realWidths[6], HEIGHT);
		// }

		// int nameWidth = width - WIDTHS[0] - WIDTHS[2] - WIDTHS[3];
		// propertyName.setBounds(WIDTHS[0], 0, nameWidth, HEIGHT);
		// if (g != null) {
		// g.drawString(":", WIDTHS[0] + nameWidth, 20);
		// }
		// propertyType.setBounds(width - WIDTHS[3] - WIDTHS[2] + 5, 0, WIDTHS[2] - 5, HEIGHT);
		// if (!idProperty) {
		// removeButton.setBounds(width - WIDTHS[3], 0, WIDTHS[3], HEIGHT);
		// }
	}

	public void setSelection(boolean selection) {
		this.selection = selection;
	}

	@Override
	public void paint(Graphics g) {
		updateCoordinates(g, getBounds().width);
		super.paint(g);
		if (selection) {
			Color originalColor = g.getColor();
			g.setColor(Color.BLUE);
			g.drawRect(0, 1, getBounds().width - 2, HEIGHT);
			g.setColor(originalColor);
		}
	}

	@Override
	public void print(Graphics g) {
		updateCoordinates(g, getBounds().width);
		super.print(g);
	}

	public void setRemovedListener(ActionListener actionListener) {
		removeListener = actionListener;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == elementRemove) {
			DrawPanel dp = CurrentDiagram.getInstance().getDiagramHandler().getDrawPanel();
			if (relationLineRef != null) {
				dp.removeRelation(relationLineRef);
				relationLineRef = null;
				dp.repaint();
			}
			if (removeListener != null) {
				removeListener.actionPerformed(new ActionEvent(this, 0, REMOVED_COMMAND));
			}
		}
		else if (e.getSource() == keyButton) {
			getParentFieldComposite().selectProperty(this);
		}
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		if (e.getSource() == elementType) {
			// FieldComposite fc = propertyType.getSelection();
			// DrawPanel dp = CurrentDiagram.getInstance().getDiagramHandler().getDrawPanel();
			// if (fc != null) {
			// if (relationLineRef != null) {
			// dp.removeRelation(relationLineRef);
			// }
			// relationLineRef = DDDRelation.createRelation(this, fc);
			// dp.addRelation(relationLineRef);
			// }
			// else {
			// if (relationLineRef != null) {
			// dp.removeRelation(relationLineRef);
			// relationLineRef = null;
			// }
			// }
			getParentFieldComposite().getComponent().requestFocus();
			// dp.repaint();
		}
		else if (e.getSource() == elementVisibility) {
			Controller controller = CurrentDiagram.getInstance().getDiagramHandler().getController();
			controller.executeCommand(new ComboBoxChange((JComboBox<?>) e.getSource(), originalSelection));
		}
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		if (e.getSource() == elementType) {
			addPropertyTypes();
		}
		originalSelection = ((JComboBox<?>) e.getSource()).getSelectedItem();
	}

	public java.awt.Point getAbsolutePosition(boolean right) {
		Point p = new Point();
		// getAbsolutePositionRecursively(this, p);
		FieldComposite fieldComposite = getParentFieldComposite();
		Rectangle rect = fieldComposite.getRealRectangle();

		p.x = rect.x;
		p.y = rect.y;
		if (fieldComposite.arePropertiesCollapsed()) {
			p.y += fieldComposite.zoom(getParent().getBounds().y + leftConnectionButton.getBounds().height / 2);
		}
		else {
			p.y += fieldComposite.zoom(getBounds().y + getParent().getBounds().y + leftConnectionButton.getBounds().height / 2);
		}
		if (right) {
			p.x += fieldComposite.zoom(getParent().getWidth() - rightConnectionButton.getBounds().width / 2);
		}
		else {
			p.x += fieldComposite.zoom(leftConnectionButton.getBounds().width / 2);
		}

		return p;
	}

	protected abstract void addPropertyTypes();

	public DDDRelation getRelation() {
		return relationLineRef;
	}

	public void setRelation(DDDRelation relation) {
		relationLineRef = relation;
		validateType();
	}

	@Override
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
		if (source instanceof JTextField) {
			originalString = ((JTextField) source).getText();
		}
		getParentFieldComposite().selectProperty(this);
		repaint();
	}

	@Override
	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
		if (e.getSource() == propertyTypeEditor) {
			FieldComposite fc = null;
			DataTypeItem selectionItem = null;
			if (elementType.getSelectedItem() instanceof DataTypeItem) {
				selectionItem = (DataTypeItem) elementType.getSelectedItem();
				fc = selectionItem.getDescription();
			}
			DrawPanel dp = getParentFieldComposite().getComponent().getDrawPanel();
			boolean collection = false;
			if (fc == null) {
				for (FieldComposite fieldComposite : dp.getHelperAndSub(FieldComposite.class)) {
					String comparingString = null;
					if (getParentFieldComposite().isInSameBoundedContext(fieldComposite)) {
						comparingString = fieldComposite.getName();
					}
					else {
						comparingString = fieldComposite.getFullName();
					}
					if (comparingString.equals(propertyTypeEditor.getText())) {
						fc = fieldComposite;
					}
				}
			}
			else {
				if (selectionItem != null) {
					collection = selectionItem.isCollection();
				}
			}
			getParentFieldComposite()
					.getComponent()
					.getController()
					.executeCommand(new PropertyDataTypeChange(this, fc, dp, relationLineRef, elementType, originalString, collection));
			// if (fc != null) {
			// if (relationLineRef != null) {
			// dp.removeRelation(relationLineRef);
			// }
			// relationLineRef = DDDRelation.createRelation(this, fc);
			// dp.addRelation(relationLineRef);
			// }
			// else {
			// if (relationLineRef != null) {
			// dp.removeRelation(relationLineRef);
			// relationLineRef = null;
			// }
			// }
			// dp.repaint();
			// setSelection(false);
			validateType();
			repaint();
		}
		else if (source instanceof JTextField) {
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

	public boolean setNameValidity(FieldProperty previous, FieldProperty previousDB) {
		StringBuffer errorMessage = new StringBuffer();
		if (previous != null) {
			errorMessage.append("Duplicated Name: ").append(getPropertyName());
		}
		boolean validateName = VariableNameHelper.validateVariableName(getPropertyName());
		if (!validateName) {
			if (errorMessage.length() > 0) {
				errorMessage.append(", ");
			}
			errorMessage.append("Invalid Name: ").append(getPropertyName());
		}

		if (previousDB != null) {
			if (errorMessage.length() > 0) {
				errorMessage.append(", ");
			}
			errorMessage.append("Duplicated Column Name: ").append("\"").append(getDatabaseName()).append("\"");
		}

		if (errorMessage.length() == 0) {
			elementName.setForeground(Color.BLACK);
			elementName.setToolTipText(null);
			return true;
		}
		else {
			elementName.setForeground(Color.RED);
			elementName.setToolTipText(errorMessage.toString());
			return false;
		}
	}

	private void updateTableName() {
		String text = getPropertyName();
		text = convertJavaVariableToDatabase(text);
		properties.addProperty(FieldComposite.DATABASE_NAME, text, true);
	}

	public String convertJavaVariableToDatabase(String text) {
		StringBuffer newText = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (Character.isUpperCase(c)) {
				newText.append('_');
			}
			c = Character.toUpperCase(c);
			newText.append(c);
		}
		return newText.toString();
	}

	private void updateValidation() {
		FieldComposite fc = getParentFieldComposite();
		if (fc != null) {
			fc.validateElementNames();
		}
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		updateValidation();
		updateTableName();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		updateValidation();
		updateTableName();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		updateValidation();
		updateTableName();
	}

	public PropertiesGridElement getProperties() {
		return properties;
	}

	@Override
	public void setZoomLevel(double zoomLevel) {
		currentZoomLevel = zoomLevel;
		HEIGHT = (int) (zoomLevel * DEFAULT_HEIGHT);
		int circleWidth = (int) (zoomLevel * RING_WIDTH);
		leftConnectionButton.setIcon(new CircleIcon(circleWidth, circleWidth));
		rightConnectionButton.setIcon(new CircleIcon(circleWidth, circleWidth));
		super.setZoomLevel(zoomLevel);
	}

	public String getDatabaseName() {
		return properties.getTableProperty(FieldComposite.DATABASE_NAME);
	}

	public String getDatabaseType() {
		// TODO generate real database type.
		if (idProperty) {
			return "VARCHAR2(1024)";
		}
		else if (relationLineRef != null) {
			if (relationLineRef.relationToValueObject() || relationLineRef.multipleRelation()) {
				return "CLOB";
			}
			else {
				return "VARCHAR2(1024)";
			}
		}
		else {
			return "CLOB";
		}
	}

	public boolean isPrimaryProperty() {
		return idProperty;
	}

	public boolean validateType() {
		String currentPropertyType = getPropertyType();
		boolean foundElement = false;
		if (currentPropertyType != null) {
			if (CurrentDiagram
					.getInstance()
					.getDiagramHandler() != null) {
				addPropertyTypes();
			}
			else {
				foundElement = true;
			}
			for (int i = 0; i < elementType.getItemCount() && !foundElement; i++) {
				DataTypeItem item = elementType.getItemAt(i);
				if (currentPropertyType.equals(item.toString())) {
					foundElement = true;
				}
			}
		}
		if (foundElement) {
			elementType.setForeground(Color.BLACK);
			elementType.setToolTipText(null);
			return true;
		}
		else {
			elementType.setForeground(Color.RED);
			elementType.setToolTipText("Invalid type: " + currentPropertyType);
			return false;
		}
	}
}
