package com.baselet.design.metal;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class DDDComboBoxIcon implements Icon {

	private JComponent currentComponent;

	/**
	 * Paints the horizontal bars for the
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		currentComponent = (JComponent) c;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int iconWidth = getIconWidth();
		try {
			BufferedImage dropDownButton = ImageIO.read(new File("img/dropdown.png"));
			g.drawImage(dropDownButton, x, y, iconWidth, iconWidth, null);
		} catch (Exception ex) {
			ex.printStackTrace();
			g.translate(x, y);

			g.setColor(currentComponent.isEnabled()
					? MetalLookAndFeel.getControlInfo()
					: MetalLookAndFeel.getControlShadow());
			g.fillPolygon(new int[] { 0, 5, iconWidth - 5, iconWidth },
					new int[] { 0, 5, 5, 0 }, 4);
			g.translate(-x, -y);
		}
	}

	/**
	 * Created a stub to satisfy the interface.
	 */
	@Override
	public int getIconWidth() {
		if (currentComponent != null) {
			return currentComponent.getHeight() - 4;
		}
		return 14;
	}

	/**
	 * Created a stub to satisfy the interface.
	 */
	@Override
	public int getIconHeight() {
		if (currentComponent != null) {
			return currentComponent.getHeight() - 4;
		}
		return 14;
	}

}
