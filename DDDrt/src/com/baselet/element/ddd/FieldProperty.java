package com.baselet.element.ddd;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;

import org.json.JSONObject;

import com.baselet.control.basics.geom.Rectangle;
import com.baselet.design.metal.DataTypeComboBox;
import com.baselet.design.metal.VisibilityComboBox;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.diagram.DrawPanel;
import com.baselet.element.ComponentSwing;
import com.baselet.element.NewGridElement;
import com.baselet.element.PropertiesGridElement;
import com.baselet.element.TableCellTextFieldBinding;
import com.baselet.element.relation.DDDRelation;
import com.baselet.gui.command.ComboBoxChange;
import com.baselet.gui.command.Controller;
import com.baselet.gui.command.PropertyDataTypeChange;
import com.baselet.gui.command.TextFieldChange;

public abstract class FieldProperty extends JLayeredPane implements ActionListener, PopupMenuListener, FocusListener, DocumentListener {

	protected static final String REMOVED_COMMAND = "removed";
	protected static final String JSON_IDPROPERTY = "idproperty";
	protected static final String JSON_NAME = "name";
	protected static final String JSON_TYPE = "type";
	protected static final String JSON_VISIBILITY = "visibility";
	protected static String IDENTIFIER = "prop";
	private static final long serialVersionUID = -6900199799847961883L;
	private final JTextField propertyName;
	protected final DataTypeComboBox propertyType;
	private final VisibilityComboBox propertyVisibility;
	private final JButton removeButton;
	protected final JButton keyButton;
	protected boolean idProperty;
	protected final static String UNIQUE_ID = "UUID";
	protected int HEIGHT = 20;
	public final static int DEFAULT_HEIGHT = 20;
	public final static int DEFAULT_FONT_SIZE = 12;
	private final int[] PERCENT_WIDTHS = { -1, -1, 60, 40, -1 };
	protected int[] FIXED_WIDTHS = { 30, 40, 30 };
	private ActionListener removeListener;
	protected final List<String> DEFAULT_TYPES;
	private DDDRelation relationLineRef;
	private Font propertyFont;
	private String originalString;
	private Object originalSelection;
	private FieldComposite parentFieldComposite;
	private final JTextComponent propertyTypeEditor;
	private boolean selection;
	protected final PropertiesGridElement properties;
	protected Image primaryKeyIcon;
	private double currentZoomLevel;
	public static Image deleteButton;
	public static Image primaryKeyButton;

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
		propertyFont = new Font(FieldComposite.FONT_NAME, Font.PLAIN, 12);

		keyButton = new JButton("");
		keyButton.setBorderPainted(false);
		keyButton.setFocusPainted(false);
		keyButton.setContentAreaFilled(false);
		keyButton.addActionListener(this);
		add(keyButton);

		propertyVisibility = new VisibilityComboBox();
		propertyVisibility.setFont(propertyFont);
		propertyVisibility.addPopupMenuListener(this);
		add(propertyVisibility);

		propertyType = new DataTypeComboBox();
		DEFAULT_TYPES = loadDefaultTypes();
		propertyType.setEditable(true);
		propertyType.addPopupMenuListener(this);
		propertyTypeEditor = (JTextComponent) propertyType.getEditor().getEditorComponent();
		propertyTypeEditor.addFocusListener(this);

		propertyType.setFont(propertyFont);
		propertyType.setSelectedItem("String");
		add(propertyType);

		propertyName = new JTextField("newProperty");
		propertyName.setFont(propertyFont);
		propertyName.addFocusListener(this);
		propertyName.getDocument().addDocumentListener(this);
		add(propertyName);

		removeButton = new JButton("");
		try {
			if (deleteButton == null) {
				deleteButton = ImageIO.read(new File("img/x-button.png"));
			}
			Image img = deleteButton.getScaledInstance(HEIGHT, HEIGHT, Image.SCALE_FAST);
			removeButton.setIcon(new ImageIcon(img));
			removeButton.setBorderPainted(false);
			removeButton.setFocusPainted(false);
			removeButton.setContentAreaFilled(false);
		} catch (Exception ex) {
			removeButton.setText("X");
			removeButton.setFont(propertyFont);
			System.out.println(ex);
		}
		removeButton.addActionListener(this);
		add(removeButton);

		updateCoordinates(null, 200);
		properties = new PropertiesGridElement();
		properties.addProperty("Name", propertyName.getText(), false);
		properties.addProperty("Visibility", getPropertyVisibility(), false);
		properties.addProperty("Data Type", getPropertyType(), false);

		TableCellTextFieldBinding
				.createBinding(properties.getTableModel(), propertyName, "Name");
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
		return propertyName.getText();
	}

	public void setPropertyName(String propertyName) {
		this.propertyName.setText(propertyName);
		properties.addProperty("Name", propertyName, true);
	}

	public String getPropertyType() {
		Object selItem = propertyType.getSelectedItem();
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
		this.propertyType.setSelectedItem(propertyType);
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
			keyButton.setIcon(new ImageIcon(primaryKeyIcon));
		}
		else {
			keyButton.setIcon(null);
		}

	}

	public String getPropertyVisibility() {
		return propertyVisibility.getSelectedItem().toString();
	}

	public void setPropertyVisibility(String propertyVisibility) {
		this.propertyVisibility.setSelection(propertyVisibility);
		properties.addProperty("Visibility", propertyVisibility, true);
	}

	protected void updateCoordinates(Graphics g, int width) {
		int startY = 2;
		int[] realWidths = new int[PERCENT_WIDTHS.length];
		int[] fixedWidths = new int[FIXED_WIDTHS.length];
		for (int i = 0; i < fixedWidths.length; i++) {
			fixedWidths[i] = (int) (currentZoomLevel * FIXED_WIDTHS[i]);
		}
		int percentFullWidth = width - fixedWidths[0] - fixedWidths[1] - fixedWidths[2];
		for (int i = 0; i < realWidths.length; i++) {
			if (PERCENT_WIDTHS[i] > 0) {
				realWidths[i] = (int) (percentFullWidth * ((double) PERCENT_WIDTHS[i] / 100));
			}
		}
		realWidths[0] = fixedWidths[0];
		realWidths[1] = fixedWidths[1];
		realWidths[4] = fixedWidths[2];
		int offsetX = 0;
		keyButton.setBounds(offsetX, startY, realWidths[0], HEIGHT);
		offsetX += realWidths[0];
		propertyVisibility.setBounds(offsetX, startY, realWidths[1], HEIGHT);
		offsetX += realWidths[1];
		propertyName.setBounds(offsetX, startY, realWidths[2], HEIGHT);
		offsetX += realWidths[2];
		if (g != null) {
			g.setFont(propertyFont);
			g.drawString(":", offsetX, (int) (currentZoomLevel * 17));
		}
		offsetX += 5;
		propertyType.setBounds(offsetX, startY, realWidths[3], HEIGHT);
		// if (!idProperty) {
		removeButton.setBounds(width - realWidths[4] + 5, startY, realWidths[4] - 5, HEIGHT);
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
		if (e.getSource() == removeButton) {
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
		if (e.getSource() == propertyType) {
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
		else if (e.getSource() == propertyVisibility) {
			Controller controller = CurrentDiagram.getInstance().getDiagramHandler().getController();
			controller.executeCommand(new ComboBoxChange((JComboBox<?>) e.getSource(), originalSelection));
		}
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		if (e.getSource() == propertyType) {
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

		p.y += fieldComposite.zoom(getBounds().y + getParent().getBounds().y + HEIGHT / 2);
		if (right) {
			p.x += fieldComposite.zoom(getParent().getWidth());
		}
		// p.y += HEIGHT / 2;
		return p;
	}

	private void getAbsolutePositionRecursively(java.awt.Component currentComponent, java.awt.Point point) {
		if (currentComponent != null && !(currentComponent instanceof DrawPanel)) {
			point.x += currentComponent.getLocation().x;
			point.y += currentComponent.getLocation().y;
			getAbsolutePositionRecursively(currentComponent.getParent(), point);
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
			FieldComposite fc = propertyType.getSelection();
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
					.executeCommand(new PropertyDataTypeChange(this, fc, dp, relationLineRef, propertyType, originalString));
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
			propertyName.setBackground(Color.WHITE);
			propertyName.setForeground(Color.RED);
			propertyName.setToolTipText("Duplicated name " + previous.getPropertyName());
		}
		else {
			propertyName.setBackground(Color.WHITE);
			propertyName.setForeground(Color.BLACK);
			propertyName.setToolTipText(null);
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

	public void setZoomLevel(double zoomLevel) {
		currentZoomLevel = zoomLevel;
		HEIGHT = (int) (zoomLevel * DEFAULT_HEIGHT);

		int newFontSize = (int) (zoomLevel * DEFAULT_FONT_SIZE);
		propertyFont = propertyFont.deriveFont(Font.PLAIN, newFontSize);

		propertyName.setFont(propertyFont);
		propertyVisibility.setFont(propertyFont);
		propertyType.setFont(propertyFont);

		Image img = deleteButton.getScaledInstance(HEIGHT, HEIGHT, Image.SCALE_FAST);
		removeButton.setIcon(new ImageIcon(img));
	}

}
