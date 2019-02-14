package com.baselet.design.metal;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.AbstractBorder;

public class DDDRoundBorder extends AbstractBorder {
	private static final long serialVersionUID = 4444142534246231540L;

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawRoundRect(x + 2, y + 2, width - 4, height - 4, 10, 10);
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(1, 4, 1, 0);
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		return new Insets(0, 4, 0, 4);
	}
}
