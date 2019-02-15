package com.baselet.element.ddd;

import java.awt.image.BufferedImage;

import com.baselet.design.metal.DDDImageIcon;

public class CircleIcon extends DDDImageIcon {

	private static final String ICON_FILE = "img/disc.png";
	private static BufferedImage image;

	public CircleIcon(int width, int height) {
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
