package com.baselet.design.metal;

import java.awt.image.BufferedImage;

public class DeleteButton extends DDDImageIcon {
	private static final String ICON_FILE = "img/x-button.png";
	private static BufferedImage image;

	public DeleteButton(int width, int height) {
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
