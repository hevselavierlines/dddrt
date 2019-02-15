package com.baselet.design.metal;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.border.TitledBorder;

public class ElementPanelBorder extends TitledBorder {

	private static final long serialVersionUID = -107235449845319303L;
	private final Insets insets;

	public ElementPanelBorder(ElementPanelBorder copy, int insetTop, int insetBottom) {
		this(insetTop, insetBottom);
		title = copy.title;
	}

	public ElementPanelBorder(int intsetTop, int insetBottom) {
		super("Title");
		insets = new Insets(intsetTop, 0, insetBottom, 0);
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		return this.insets;
	}

}
