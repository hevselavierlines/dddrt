package com.baselet.element.ddd;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.json.JSONObject;

import com.baselet.design.metal.DataTypeComboBox;
import com.baselet.design.metal.VisibilityComboBox;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.diagram.DrawPanel;
import com.baselet.element.ComponentSwing;
import com.baselet.element.NewGridElement;
import com.baselet.element.relation.DDDRelation;

public abstract class FieldProperty extends JLayeredPane implements ActionListener, PopupMenuListener {

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
	private final int[] WIDTHS = { 40, -1, 120, 40 };
	private ActionListener removeListener;
	protected final List<String> DEFAULT_TYPES;
	private DDDRelation relationLineRef;
	private final Font propertyFont;
	public static final String FONT_NAME = "Tahoma";

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
		propertyFont = new Font(FONT_NAME, Font.PLAIN, 15);

		propertyVisibility = new VisibilityComboBox();
		propertyVisibility.setFont(propertyFont);
		add(propertyVisibility);

		propertyType = new DataTypeComboBox();
		DEFAULT_TYPES = new LinkedList<String>();
		DEFAULT_TYPES.add("String");
		DEFAULT_TYPES.add("int");
		DEFAULT_TYPES.add("boolean");
		DEFAULT_TYPES.add("long");
		DEFAULT_TYPES.add("byte");
		DEFAULT_TYPES.add("char");
		DEFAULT_TYPES.add("short");
		DEFAULT_TYPES.add("Object");
		DEFAULT_TYPES.add("List");
		DEFAULT_TYPES.add("Map");
		propertyType.setEditable(true);
		propertyType.addPopupMenuListener(this);
		propertyType.setFont(propertyFont);
		add(propertyType);

		propertyName = new JTextField("newProperty");
		propertyName.setFont(propertyFont);
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
		propertyVisibility.setBounds(0, 0, WIDTHS[0], HEIGHT);

		int nameWidth = width - WIDTHS[0] - WIDTHS[2] - WIDTHS[3];
		propertyName.setBounds(WIDTHS[0], 0, nameWidth, HEIGHT);
		if (g != null) {
			g.drawString(":", WIDTHS[0] + nameWidth, 20);
		}
		propertyType.setBounds(width - WIDTHS[3] - WIDTHS[2] + 5, 0, WIDTHS[2] - 5, HEIGHT);
		if (!idProperty) {
			removeButton.setBounds(width - WIDTHS[3], 0, WIDTHS[3], HEIGHT);
		}
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
		FieldComposite fc = propertyType.getSelection();
		DrawPanel dp = CurrentDiagram.getInstance().getDiagramHandler().getDrawPanel();
		if (fc != null) {
			if (relationLineRef != null) {
				dp.removeRelation(relationLineRef);
			}
			relationLineRef = DDDRelation.createRelation(this, fc);
			dp.addRelation(relationLineRef);
		}
		else {
			if (relationLineRef != null) {
				dp.removeRelation(relationLineRef);
				relationLineRef = null;
			}
		}
		dp.repaint();
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		addPropertyTypes();
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
}
