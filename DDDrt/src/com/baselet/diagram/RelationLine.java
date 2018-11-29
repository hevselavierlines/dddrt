package com.baselet.diagram;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import com.baselet.element.FieldComposite;
import com.baselet.element.FieldProperty;

public class RelationLine {
	private java.awt.Point startPoint;
	private java.awt.Point endPoint;

	private final FieldProperty startProperty;
	private final FieldComposite endComposite;

	public RelationLine(FieldProperty startProperty, FieldComposite endComposite) {
		super();
		this.startProperty = startProperty;
		this.endComposite = endComposite;

		updatePoints();
	}

	public void updatePoints() {
		startPoint = startProperty.getAbsolutePosition(false);
		endPoint = endComposite.getAbsolutePosition();
		if (startPoint.x < endPoint.x) {
			startPoint = startProperty.getAbsolutePosition(true);
		}
	}

	public void paint(Graphics2D graphics) {
		java.awt.Color origColor = graphics.getColor();
		java.awt.Stroke origStroke = graphics.getStroke();

		graphics.setColor(Color.BLACK);
		graphics.setStroke(new BasicStroke(2.0f));

		graphics.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);

		graphics.setColor(origColor);
		graphics.setStroke(origStroke);
	}
}
