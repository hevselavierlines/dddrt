package com.baselet.element.ddd;

import java.awt.Color;
import java.awt.Font;
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
import javax.swing.JLayeredPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;

import org.json.JSONObject;

import com.baselet.design.metal.DataTypeComboBox;
import com.baselet.design.metal.VisibilityComboBox;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.diagram.DrawPanel;
import com.baselet.element.ComponentSwing;
import com.baselet.element.NewGridElement;
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
	private boolean idProperty;
	protected final static String UNIQUE_ID = "UUID";
	public final static int HEIGHT = 25;
	private final int[] PERCENT_WIDTHS = { -1, 60, 40, -1 };
	private final int[] FIXED_WIDTHS = { 40, 50 };
	private ActionListener removeListener;
	protected final List<String> DEFAULT_TYPES;
	private DDDRelation relationLineRef;
	private final Font propertyFont;
	private String originalString;
	private Object originalSelection;
	private FieldComposite parentFieldComposite;
	private final JTextComponent propertyTypeEditor;

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
		propertyFont = new Font(FieldComposite.FONT_NAME, Font.PLAIN, 15);

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

		removeButton = new JButton("x");
		removeButton.setFont(propertyFont);
		removeButton.addActionListener(this);
		add(removeButton);

		updateCoordinates(null, 200);
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
	}

	public String getPropertyVisibility() {
		return propertyVisibility.getSelectedItem().toString();
	}

	public void setPropertyVisibility(String propertyVisibility) {
		this.propertyVisibility.setSelection(propertyVisibility);
	}

	protected void updateCoordinates(Graphics g, int width) {
		int[] realWidths = new int[PERCENT_WIDTHS.length];
		int percentFullWidth = width - FIXED_WIDTHS[0] - FIXED_WIDTHS[1];
		for (int i = 0; i < realWidths.length; i++) {
			if (PERCENT_WIDTHS[i] > 0) {
				realWidths[i] = (int) (percentFullWidth * ((double) PERCENT_WIDTHS[i] / 100));
			}
		}
		realWidths[0] = FIXED_WIDTHS[0];
		realWidths[3] = FIXED_WIDTHS[1];
		propertyVisibility.setBounds(0, 0, realWidths[0], HEIGHT);
		propertyName.setBounds(realWidths[0], 0, realWidths[1], HEIGHT);
		if (g != null) {
			g.drawString(":", realWidths[0] + realWidths[1], 20);
		}
		propertyType.setBounds(realWidths[0] + realWidths[1] + 5, 0, realWidths[2], HEIGHT);
		if (!idProperty) {
			removeButton.setBounds(width - realWidths[3], 0, realWidths[3], HEIGHT);
		}

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

	@Override
	public void paint(Graphics g) {
		updateCoordinates(g, getBounds().width);
		super.paint(g);
	}

	@Override
	public void print(Graphics g) {
		updateCoordinates(g, getWidth());
		super.print(g);
	}

	public void setRemovedListener(ActionListener actionListener) {
		removeListener = actionListener;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
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
		getAbsolutePositionRecursively(this, p);
		if (right) {
			p.x += getWidth() + 6;
		}
		p.y += HEIGHT / 2;
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

	public void setRelation(DDDRelation relation) {
		relationLineRef = relation;
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

}
