package com.baselet.design.metal;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import com.baselet.element.ddd.FieldComposite;
import com.baselet.element.ddd.FieldMethod;
import com.baselet.element.ddd.FieldProperty;

public class MetalTextField extends JTextField implements FocusListener {

	private static final long serialVersionUID = -2885003147095962742L;
	private FieldComposite parentFieldComposite;
	private FieldProperty parentFieldProperty;
	private FieldMethod parentFieldMethod;

	public MetalTextField() {
		super();
		init();

		setBorder(new DDDRoundBorder());
	}

	public MetalTextField(String arg0) {
		super(arg0);
		init();
	}

	private void init() {
		addFocusListener(this);

		findParentFields(this);
		if (parentFieldProperty != null) {
			parentFieldComposite = parentFieldProperty.getParentFieldComposite();
		}
		if (parentFieldMethod != null) {
			parentFieldComposite = parentFieldMethod.getParentFieldComposite();
		}
	}

	private void findParentFields(Component currentComponent) {
		if (currentComponent != null) {
			if (currentComponent instanceof FieldProperty) {
				parentFieldProperty = (FieldProperty) currentComponent;
			}
			else if (currentComponent instanceof FieldMethod) {
				parentFieldMethod = (FieldMethod) currentComponent;
			}
			else {
				findParentFields(currentComponent.getParent());
			}
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (parentFieldProperty != null && parentFieldComposite != null) {
			parentFieldComposite.selectProperty(parentFieldProperty);
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (parentFieldProperty != null && parentFieldComposite != null) {
			parentFieldComposite.deselectAll();
		}
	}
}
