package com.baselet.design.metal;

import javax.swing.JComboBox;
import javax.swing.plaf.metal.MetalComboBoxUI;

public class MetalComboBox extends JComboBox<String> {
	private static final long serialVersionUID = -8192247366525092104L;

	public MetalComboBox() {
		super();
		setUI(new MetalComboBoxUI());
	}
}
