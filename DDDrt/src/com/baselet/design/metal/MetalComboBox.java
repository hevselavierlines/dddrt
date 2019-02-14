package com.baselet.design.metal;

import javax.swing.JComboBox;

public class MetalComboBox<T> extends JComboBox<T> {
	private static final long serialVersionUID = -8192247366525092104L;
	private final DDDComboBoxUI comboUI;

	public MetalComboBox() {
		super();
		comboUI = new DDDComboBoxUI();
		this.setUI(comboUI);
		setOpaque(false);
		setBorder(new DDDRoundBorder());
	}

	public void setPopupMinimumSize(int width, int height) {
		comboUI.setPopupMinimumSize(width, height);
	}

}
