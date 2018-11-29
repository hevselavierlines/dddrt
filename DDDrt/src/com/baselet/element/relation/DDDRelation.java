package com.baselet.element.relation;

import com.baselet.control.basics.geom.Rectangle;
import com.baselet.control.enums.ElementId;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.element.ElementFactorySwing;
import com.baselet.element.FieldComposite;
import com.baselet.element.FieldProperty;
import com.baselet.element.relation.helper.RelationPointHandler;
import com.baselet.element.relation.helper.RelationPointList;

public class DDDRelation extends Relation {

	private FieldProperty startProperty;
	private FieldComposite endComposite;

	public static DDDRelation createRelation(FieldProperty startProperty, FieldComposite endComposite) {
		java.awt.Point startPoint = startProperty.getAbsolutePosition(false);
		java.awt.Point endPoint = endComposite.getAbsolutePosition();
		if (startPoint.x < endPoint.x) {
			startPoint = startProperty.getAbsolutePosition(true);
		}

		int minX = Math.min(startPoint.x, endPoint.x);
		int minY = Math.min(startPoint.y, endPoint.y);

		int maxX = Math.max(startPoint.x, endPoint.x);
		int maxY = Math.max(startPoint.y, endPoint.y);
		Rectangle rect = new Rectangle(minX, minY, maxX - minX, maxY - minY);
		DDDRelation dddRelation = (DDDRelation) ElementFactorySwing.create(ElementId.DDDRelation, rect, "lt=<-", null, CurrentDiagram.getInstance().getDiagramHandler());
		dddRelation.startProperty = startProperty;
		dddRelation.endComposite = endComposite;
		dddRelation.createRelationLine();
		return dddRelation;
	}

	@Override
	public void setAdditionalAttributes(String additionalAttributes) {
		if (additionalAttributes == null) {
			RelationPointList pointList = new RelationPointList();
			pointList.add(0, 0);
			pointList.add(0, 0);
			relationPoints = new RelationPointHandler(this, pointList);
		}
		else {
			super.setAdditionalAttributes(additionalAttributes);
		}
	}

	public void createRelationLine() {
		java.awt.Point startPoint = startProperty.getAbsolutePosition(false);
		java.awt.Point endPoint = endComposite.getAbsolutePosition();
		if (startPoint.x < endPoint.x) {
			startPoint = startProperty.getAbsolutePosition(true);
		}
		int minX = getRectangle().x;
		int minY = getRectangle().y;
		endPoint.x -= minX;
		endPoint.y -= minY;
		startPoint.x -= minX;
		startPoint.y -= minY;
		RelationPointList pointList = new RelationPointList();
		pointList.add(endPoint.x, endPoint.y);
		pointList.add(startPoint.x, startPoint.y);
		relationPoints = new RelationPointHandler(this, pointList);
		if (getHandler().isInitialized()) {
			relationPoints.resizeRectAndReposPoints();
		}
		updateModelFromText();
	}

	public void moveRelationLine() {}

}
