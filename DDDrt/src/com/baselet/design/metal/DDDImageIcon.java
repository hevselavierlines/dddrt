package com.baselet.design.metal;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;

public abstract class DDDImageIcon implements Icon {
	protected int width, height;

	public DDDImageIcon(int width, int height) {
		this.width = width;
		this.height = height;
	}

	protected static BufferedImage loadImage(String filePath) {
		BufferedImage image = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				throw new FileNotFoundException(filePath +
												" cannot be found." +
												" Please check the folders.");
			}
			image = ImageIO.read(new File(filePath));
		} catch (IOException e) {
			System.err.println("Error loading image: " + e.getMessage());
		}
		return image;
	}

	@Override
	public int getIconHeight() {
		return height;
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	protected abstract BufferedImage getImage();

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		BufferedImage image = getImage();
		if (image != null) {
			g2d.drawImage(image, x, y, width, height, null);
		}
	}

}
