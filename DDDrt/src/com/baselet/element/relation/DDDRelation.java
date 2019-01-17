package com.baselet.element.relation;

import com.baselet.control.SharedUtils;
import com.baselet.control.basics.geom.Rectangle;
import com.baselet.control.enums.ElementId;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.diagram.DrawPanel;
import com.baselet.diagram.draw.helper.ColorOwn;
import com.baselet.element.ElementFactorySwing;
import com.baselet.element.ddd.AggregateComposite;
import com.baselet.element.ddd.FieldComposite;
import com.baselet.element.ddd.FieldProperty;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.element.relation.helper.RelationPointHandler;
import com.baselet.element.relation.helper.RelationPointList;

public class DDDRelation extends Relation {

	private FieldProperty startProperty;
	private FieldComposite endComposite;
	private String relationString;

	@Override
	public ElementId getId() {
		return ElementId.DDDRelation;
	}

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
		DDDRelation dddRelation = (DDDRelation) ElementFactorySwing.create(ElementId.DDDRelation, rect, "lt=<-", null, CurrentDiagram.getInstance().getDiagramHandler(), null);
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
			String[] spliter = additionalAttributes.split(";;;");
			super.setAdditionalAttributes(spliter[0]);

			if (spliter.length >= 2) {
				relationString = spliter[1];
			}
		}
	}

	@Override
	public String getAdditionalAttributes() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.getAdditionalAttributes());
		if (endComposite != null) {
			sb.append(";;;");
			sb.append(endComposite.getUUID());
			sb.append(';');
			sb.append(startProperty.getParentFieldComposite().getUUID());
			sb.append(';');
			sb.append(startProperty.getPropertyName());
		}
		return sb.toString();
	}

	public void createRelationLine() {
		java.awt.Point startPoint = startProperty.getAbsolutePosition(false);
		java.awt.Point endPoint = endComposite.getAbsolutePosition();
		if (startPoint.x + startProperty.getWidth() / 2 < endPoint.x) {
			startPoint = startProperty.getAbsolutePosition(true);
		}
		int minX = getRectangle().x;
		int minY = getRectangle().y;
		endPoint.x -= minX;
		endPoint.y -= minY;
		startPoint.x -= minX;
		startPoint.y -= minY;

		endPoint.x = SharedUtils.realignToGridRoundToNearest(true, endPoint.x);
		endPoint.y = SharedUtils.realignToGridRoundToNearest(true, endPoint.y);
		startPoint.x = SharedUtils.realignToGridRoundToNearest(true, startPoint.x);
		startPoint.y = SharedUtils.realignToGridRoundToNearest(true, startPoint.y);

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

	public void initRelation(DrawPanel drawPanel) {
		String[] relationParams = relationString.split(";");
		if (relationParams.length == 3) {
			String endCompUUID = relationParams[0];
			String startCompUUID = relationParams[1];
			String startCompProp = relationParams[2];

			endComposite = (FieldComposite) drawPanel.getElementById(endCompUUID);
			FieldComposite startComposite = (FieldComposite) drawPanel.getElementById(startCompUUID);
			startProperty = startComposite.getPropertyByName(startCompProp);
			startProperty.setRelation(this);
		}
	}

	public boolean isConnectedToFieldComposite(FieldComposite fieldComposite) {
		if (endComposite != null && endComposite == fieldComposite) {
			return true;
		}
		if (getStartComposite() != null && getStartComposite() == fieldComposite) {
			return true;
		}
		return false;
	}

	public FieldComposite getEndComposite() {
		return endComposite;
	}

	public FieldComposite getStartComposite() {
		if (startProperty != null) {
			return startProperty.getParentFieldComposite();
		}
		else {
			return null;
		}
	}

	protected boolean isValidRelation() {
		FieldComposite startComposite = getStartComposite();
		FieldComposite endComposite = getEndComposite();
		if (startComposite == null || endComposite == null) {
			return true;
		}
		if (!startComposite.isInSameBoundedContext(endComposite)) {
			if (endComposite instanceof AggregateComposite) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return true;
		}
	}

	public void validateRelation() {
		updateModelFromText();
	}

	@Override
	protected void drawCommonContent(PropertiesParserState state) {
		super.drawCommonContent(state);

		if (isValidRelation()) {
			state.getDrawer().setForegroundColor(ColorOwn.BLACK);
		}
		else {
			state.getDrawer().setForegroundColor(ColorOwn.RED);
		}
	}

}
