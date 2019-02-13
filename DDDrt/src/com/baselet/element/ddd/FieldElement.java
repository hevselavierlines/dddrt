package com.baselet.element.ddd;

import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JLayeredPane;

import com.baselet.design.metal.DataTypeComboBox;
import com.baselet.design.metal.MetalTextField;
import com.baselet.design.metal.VisibilityComboBox;
import com.baselet.element.ComponentSwing;
import com.baselet.element.NewGridElement;

public abstract class FieldElement extends JLayeredPane {
	private static final long serialVersionUID = -2908950174661187997L;

	protected final MetalTextField elementName;
	protected final DataTypeComboBox elementType;
	protected final VisibilityComboBox elementVisibility;
	protected final JButton elementRemove;

	private FieldComposite parentFieldComposite;

	public FieldElement() {
		elementVisibility = new VisibilityComboBox();
		elementName = new MetalTextField();
		elementType = new DataTypeComboBox();
		elementRemove = new JButton();
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

	@Override
	public void paint(Graphics g) {
		if (parentFieldComposite == null) {
			parentFieldComposite = getParentFieldComposite();
			elementVisibility.setBackground(parentFieldComposite.getBackgroundColor());
			elementName.setBackground(parentFieldComposite.getBackgroundColor());
			elementType.setBackground(parentFieldComposite.getBackgroundColor());
		}
		super.paint(g);
	}

}
