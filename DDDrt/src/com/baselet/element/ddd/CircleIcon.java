package com.baselet.element.ddd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;

public class CircleIcon implements Icon {

	private final int width;
	private final int height;

	public CircleIcon(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public int getIconHeight() {
		return height;
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int middleX = width / 2;
		int middleY = height / 2;
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(1.0f));
		g.drawOval(middleX - width / 2, middleY - width / 2, width - 3, width - 3);
	}

}
