package com.baselet.design.metal;

import javax.swing.JButton;
import javax.swing.plaf.metal.MetalButtonUI;

public class MetalButton extends JButton {
	private static final long serialVersionUID = 8876020186681491112L;

	public MetalButton(String title) {
		super(title);
		setUI(new MetalButtonUI());
	}
}
