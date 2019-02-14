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
	// protected static final int DEFAULT_ICON_WIDTH = 18;
	protected static final int DEFAULT_ICON_WIDTH = 10;
	protected static BufferedImage DROP_DOWN_IMAGE;
	protected float zoomFactor;

	/**
	 * Paints the horizontal bars for the
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		currentComponent = (JComponent) c;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int iconWidth = getIconSideLength();
		try {
			if (DROP_DOWN_IMAGE == null) {
				DROP_DOWN_IMAGE = ImageIO.read(new File("img/dropdown.png"));
			}

			// g.drawImage(DROP_DOWN_IMAGE, x + 10, y + 2, iconWidth - 6, iconWidth - 6, null);
			g.drawImage(DROP_DOWN_IMAGE, x, y, iconWidth, iconWidth, null);
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

	@Override
	public int getIconWidth() {
		return getIconSideLength();
	}

	@Override
	public int getIconHeight() {
		return getIconSideLength();
	}

	public int getIconSideLength() {
		// zoomFactor = 1.0f;
		// int width = DEFAULT_ICON_WIDTH;
		// if (currentComponent != null) {
		// FieldElement fe = (FieldElement) currentComponent.getParent().getParent();
		// zoomFactor = fe.getParentFieldComposite().getZoom();
		//
		// Rectangle rectangle = currentComponent.getBounds();
		// width = (int) (DEFAULT_ICON_WIDTH * zoomFactor);
		// rectangle.width = width - 10;
		// rectangle.height = width;
		// currentComponent.setBounds(rectangle);
		// }
		// return width;
		return DEFAULT_ICON_WIDTH;
	}
}
