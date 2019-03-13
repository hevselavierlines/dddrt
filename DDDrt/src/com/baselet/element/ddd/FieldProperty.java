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

	public int getFieldHeight() {
		return HEIGHT + 3;
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

	public static List<String> loadDefaultTypes() {
		List<String> types = new LinkedList<String>();
		types.add("String");
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
		types.add("List");
		types.add("Map");
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
		properties.addProperty("Visibility", getPropertyVisibility(), false);
		properties.addProperty("Data Type", getPropertyType(), false);

		setPropertyName("newProperty");

		TableCellTextFieldBinding
				.createBinding(properties.getTableModel(), elementName, "Name");
		TableCellTextFieldBinding
				.createBinding(properties.getTableModel(), propertyTypeEditor, "Data Type");

	}

	public FieldProperty(String propertyVisibility,
			String propertyType,
			String propertyName,
			boolean idProperty) {
		this();
		this.idProperty = idProperty;
		setPropertyVisibility(propertyVisibility);
		setPropertyType(propertyType);
		setPropertyName(propertyName);
	}

	public String getPropertyName() {
		return elementName.getText();
	}

	public void setPropertyName(String propertyName) {
		elementName.setText(propertyName);
		properties.addProperty("Name", propertyName, true);
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
		if (idProperty) {
			DEFAULT_TYPES.add(0, UNIQUE_ID);
		}
		elementType.setSelectedItem(propertyType);
		properties.addProperty("Data Type", propertyType, true);
	}

	public void setIdProperty(boolean idProperty) {
		if (this.idProperty == false && idProperty == true) {
			DEFAULT_TYPES.add(0, UNIQUE_ID);
		}
		else if (this.idProperty == true && idProperty == false) {
			DEFAULT_TYPES.remove(UNIQUE_ID);
		}
		this.idProperty = idProperty;

		if (idProperty) {
			setZoomLevel(getParentFieldComposite().getZoom());
			keyButton.setIcon(new PrimaryKeyButton(HEIGHT, HEIGHT));
		}
		else {
			keyButton.setIcon(null);
		}

	}

	public String getPropertyVisibility() {
		return elementVisibility.getSelectedItem().toString();
	}

	public void setPropertyVisibility(String propertyVisibility) {
		elementVisibility.setSelection(propertyVisibility);
		properties.addProperty("Visibility", propertyVisibility, true);
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

		p.y += fieldComposite.zoom(getBounds().y + getParent().getBounds().y + leftConnectionButton.getBounds().height / 2);
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
			FieldComposite fc = elementType.getSelection();
			DrawPanel dp = getParentFieldComposite().getComponent().getDrawPanel();
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
			getParentFieldComposite()
					.getComponent()
					.getController()
					.executeCommand(new PropertyDataTypeChange(this, fc, dp, relationLineRef, elementType, originalString));
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

	public void setNameValidity(FieldProperty previous) {
		if (previous != null) {
			// elementName.setBackground(Color.WHITE);
			elementName.setForeground(Color.RED);
			elementName.setToolTipText("Duplicated name " + previous.getPropertyName());
		}
		else {
			// elementName.setBackground(Color.WHITE);
			elementName.setForeground(Color.BLACK);
			elementName.setToolTipText(null);
		}
	}

	private void updateValidation() {
		FieldComposite fc = getParentFieldComposite();
		if (fc != null) {
			fc.validateNames();
		}
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		updateValidation();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		updateValidation();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		updateValidation();
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
		return getPropertyName().toUpperCase();
	}

	public String getDatabaseType() {
		// TODO generate real database type.
		return "VARCHAR2(1024)";
	}

	public boolean isPrimaryProperty() {
		return idProperty;
	}
}
