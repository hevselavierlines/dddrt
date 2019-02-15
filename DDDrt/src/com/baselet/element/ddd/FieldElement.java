package com.baselet.element.ddd;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JLayeredPane;

import com.baselet.design.metal.DataTypeComboBox;
import com.baselet.design.metal.DeleteButton;
import com.baselet.design.metal.MetalTextField;
import com.baselet.design.metal.VisibilityComboBox;
import com.baselet.element.ComponentSwing;
import com.baselet.element.NewGridElement;

public abstract class FieldElement extends JLayeredPane {
	private static final long serialVersionUID = -2908950174661187997L;

	protected final MetalTextField elementName;
	protected final DataTypeComboBox elementType;
	protected final VisibilityComboBox elementVisibility;
	protected final JButton elementRemove;
	protected double currentZoomLevel;
	public final static int DEFAULT_HEIGHT = 20;

	private FieldComposite parentFieldComposite;
	public final static int DEFAULT_FONT_SIZE = 12;
	protected Font elementFont;

	public FieldElement() {
		elementFont = new Font(FieldComposite.FONT_NAME, Font.PLAIN, DEFAULT_FONT_SIZE);
		elementVisibility = new VisibilityComboBox();
		elementName = new MetalTextField();
		elementType = new DataTypeComboBox();
		elementRemove = new JButton();
	}

	public FieldComposite getParentFieldComposite() {
		if (parentFieldComposite != null) {
			return parentFieldComposite;
		}
		NewGridElement element = getParentFieldCompositeRecursively(this);
		if (element instanceof FieldComposite) {
			return (FieldComposite) element;
		}
		else {
			return null;
		}
	}

	private NewGridElement getParentFieldCompositeRecursively(java.awt.Component currentComponent) {
		if (currentComponent != null) {
			if (currentComponent instanceof ComponentSwing) {
				ComponentSwing swing = (ComponentSwing) currentComponent;
				return swing.getGridElement();
			}
			else {
				return getParentFieldCompositeRecursively(currentComponent.getParent());
			}
		}
		else {
			return null;
		}
	}

	@Override
	public void paint(Graphics g) {
		if (parentFieldComposite == null) {
			parentFieldComposite = getParentFieldComposite();
			elementVisibility.setBackground(parentFieldComposite.getBackgroundColor());
			elementName.setBackground(parentFieldComposite.getBackgroundColor());
			elementType.setBackground(parentFieldComposite.getBackgroundColor());
		}
		super.paint(g);
	}

	protected void updateCoordinates(Graphics g, int width) {
		// int startY = 2;
		// int[] realWidths = new int[PERCENT_WIDTHS.length];
		// int[] fixedWidths = new int[FIXED_WIDTHS.length];
		// for (int i = 0; i < fixedWidths.length; i++) {
		// fixedWidths[i] = (int) (currentZoomLevel * FIXED_WIDTHS[i]);
		// }
		// int percentFullWidth = width - fixedWidths[0] - fixedWidths[1] - fixedWidths[2];
		// for (int i = 0; i < realWidths.length; i++) {
		// if (PERCENT_WIDTHS[i] > 0) {
		// realWidths[i] = (int) (percentFullWidth * ((double) PERCENT_WIDTHS[i] / 100));
		// }
		// }
		// realWidths[0] = fixedWidths[0];
		// realWidths[1] = fixedWidths[1];
		// realWidths[4] = fixedWidths[2];
		// int offsetX = 0;
		// keyButton.setBounds(offsetX, startY, realWidths[0], HEIGHT);
		// offsetX += realWidths[0];
		// elementVisibility.setBounds(offsetX, startY, realWidths[1], HEIGHT);
		// offsetX += realWidths[1];
		// elementName.setBounds(offsetX, startY, realWidths[2], HEIGHT);
		// offsetX += realWidths[2];
		// if (g != null) {
		// g.setFont(elementFont);
		// g.drawString(":", offsetX, (int) (currentZoomLevel * 17));
		// }
		// offsetX += 5;
		// elementType.setBounds(offsetX, startY, realWidths[3], HEIGHT);
		// // if (!idProperty) {
		// elementRemove.setBounds(width - realWidths[4] + 5, startY, realWidths[4] - 5, HEIGHT);
		// // }

		// int nameWidth = width - WIDTHS[0] - WIDTHS[2] - WIDTHS[3];
		// propertyName.setBounds(WIDTHS[0], 0, nameWidth, HEIGHT);
		// if (g != null) {
		// g.drawString(":", WIDTHS[0] + nameWidth, 20);
		// }
		// propertyType.setBounds(width - WIDTHS[3] - WIDTHS[2] + 5, 0, WIDTHS[2] - 5, HEIGHT);
		// if (!idProperty) {
		// removeButton.setBounds(width - WIDTHS[3], 0, WIDTHS[3], HEIGHT);
		// }
	}

	public void setZoomLevel(double zoomLevel) {
		currentZoomLevel = zoomLevel;
		int newHeight = (int) (zoomLevel * DEFAULT_HEIGHT);

		int newFontSize = (int) (zoomLevel * DEFAULT_FONT_SIZE);
		elementFont = elementFont.deriveFont(Font.PLAIN, newFontSize);

		elementName.setFont(elementFont);
		elementVisibility.setFont(elementFont);
		elementType.setFont(elementFont);

		elementRemove.setIcon(new DeleteButton(newHeight, newHeight));
	}

}
