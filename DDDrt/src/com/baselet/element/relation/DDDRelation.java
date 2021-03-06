package com.baselet.element.relation;

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

import tk.baumi.main.CompositeType;
import tk.baumi.main.IDDDRelation;

public class DDDRelation extends Relation implements IDDDRelation {

	private FieldProperty startProperty;
	private FieldComposite endComposite;
	private String relationString;
	private boolean collection;

	@Override
	public ElementId getId() {
		return ElementId.DDDRelation;
	}

	public void changeFieldComposite(FieldComposite oldComposite, FieldComposite newComposite) {
		FieldComposite startComposite = startProperty.getParentFieldComposite();
		if (startComposite != null && startComposite == oldComposite) {
			FieldProperty newFieldProperty = newComposite.getPropertyByName(startProperty.getPropertyName());
			if (newFieldProperty != null) {
				startProperty.setRelation(null);
				newFieldProperty.setRelation(this);
				startProperty = newFieldProperty;
			}
		}
		else if (endComposite == oldComposite) {
			endComposite = newComposite;
		}
		updateModelFromText();
	}

	public static DDDRelation createRelation(FieldProperty startProperty, FieldComposite endComposite, boolean manyToManyRelation) {
		java.awt.Point startPoint = startProperty.getAbsolutePosition(false);
		java.awt.Point endPoint = endComposite.getAbsolutePosition(false);
		if (startPoint.x < endPoint.x) {
			startPoint = startProperty.getAbsolutePosition(true);
		}
		if (startPoint.y > endPoint.y + endComposite.getRectangle().height / 2) {
			endPoint = endComposite.getAbsolutePosition(true);
		}
		Rectangle boundingRectangle = createBoundingRectangle(startPoint, endPoint);
		DDDRelation dddRelation = (DDDRelation) ElementFactorySwing.create(
				ElementId.DDDRelation,
				boundingRectangle,
				manyToManyRelation ? "lt=<-\nm1=*\nm2=*" : "lt=<-\nm1=1\nm2=*",
				null,
				CurrentDiagram.getInstance().getDiagramHandler(),
				null);
		dddRelation.startProperty = startProperty;
		dddRelation.endComposite = endComposite;
		dddRelation.collection = manyToManyRelation;
		dddRelation.createRelationLine();
		return dddRelation;
	}

	public static Rectangle createBoundingRectangle(java.awt.Point startPoint, java.awt.Point endPoint) {
		int minX = Math.min(startPoint.x, endPoint.x);
		int minY = Math.min(startPoint.y, endPoint.y);
		int maxX = Math.max(startPoint.x, endPoint.x);
		int maxY = Math.max(startPoint.y, endPoint.y);
		Rectangle rect = new Rectangle(minX, minY, maxX - minX, maxY - minY);
		return rect;
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
			sb.append(';');
			sb.append(collection);
		}
		return sb.toString();
	}

	public void createRelationLine() {
		java.awt.Point startPoint = startProperty.getAbsolutePosition(false);
		java.awt.Point endPoint = endComposite.getAbsolutePosition(false);
		if (startPoint.x + endComposite.zoom(startProperty.getWidth()) / 2 < endPoint.x) {
			startPoint = startProperty.getAbsolutePosition(true);
		}
		if (startPoint.y > endPoint.y + endComposite.getRectangle().height / 2) {
			endPoint = endComposite.getAbsolutePosition(true);
		}
		// endPoint.x -= minX;
		// endPoint.y -= minY;
		// startPoint.x -= minX;
		// startPoint.y -= minY;

		setRectangle(new Rectangle(0, 0, 0, 0));

		// endPoint.x = SharedUtils.realignToGridRoundToNearest(true, endPoint.x);
		// endPoint.y = SharedUtils.realignToGridRoundToNearest(true, endPoint.y);
		// startPoint.x = SharedUtils.realignToGridRoundToNearest(true, startPoint.x);
		// startPoint.y = SharedUtils.realignToGridRoundToNearest(true, startPoint.y);

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
		if (relationParams.length >= 3) {
			String endCompUUID = relationParams[0];
			String startCompUUID = relationParams[1];
			String startCompProp = relationParams[2];
			if (relationParams.length >= 4) {
				String collectionString = relationParams[3];
				collection = Boolean.parseBoolean(collectionString);
			}

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
		state.getDrawer().setForegroundColor(ColorOwn.BLACK);
		// if (isValidRelation()) {
		// state.getDrawer().setForegroundColor(ColorOwn.BLACK);
		// }
		// else {
		// state.getDrawer().setForegroundColor(ColorOwn.RED);
		// }
	}

	@Override
	public String getStartTableName() {
		return getStartComposite().getDatabaseName();
	}

	@Override
	public String getStartPropertyName() {
		return startProperty.getDatabaseName();
	}

	@Override
	public String getStartTableIDProperty() {
		return getStartComposite().getIDProperty().getDatabaseName();
	}

	@Override
	public String getEndTableName() {
		return getEndComposite().getDatabaseName();
	}

	@Override
	public String getEndPropertyName() {
		return getEndComposite().getIDProperty().getDatabaseName();
	}

	@Override
	public boolean relationToValueObject() {
		return getEndComposite().getType() == CompositeType.ValueObject;
	}

	@Override
	public boolean multipleRelation() {
		return collection;
	}

	@Override
	public String associateTableName() {
		StringBuffer tableName = new StringBuffer();
		// ASSOC_AGGREGATE_1_ENTITY_2_ENTITY2
		tableName
				.append(getStartPropertyName())
				.append('_')
				.append(getStartTableName())
				.append('_')
				.append(getEndTableName());
		if (tableName.length() > 30) {
			tableName.setLength(30);
		}
		return tableName.toString();
	}

	@Override
	public String getStartPropertyType() {
		return getStartComposite().getIDProperty().getDatabaseType();
	}

	@Override
	public String getEndPropertyType() {
		return endComposite.getIDProperty().getDatabaseType();
	}

}
