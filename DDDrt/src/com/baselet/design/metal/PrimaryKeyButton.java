package com.baselet.design.metal;

import java.awt.image.BufferedImage;

public class PrimaryKeyButton extends DDDImageIcon {
	private static final String ICON_FILE = "img/primarykey.png";
	private static BufferedImage image;

	public PrimaryKeyButton(int width, int height) {
		super(width, height);
		if (image == null) {
			image = loadImage(ICON_FILE);
		}
	}

	@Override
	protected BufferedImage getImage() {
		return image;
	}

}
