package com.baselet.element.ddd;

import java.awt.Polygon;
import java.util.Collection;
import java.util.List;

import javax.swing.JTextArea;

import org.json.JSONObject;

import com.baselet.control.basics.XValues;
import com.baselet.control.basics.geom.Point;
import com.baselet.control.basics.geom.Rectangle;
import com.baselet.control.enums.Direction;
import com.baselet.control.enums.ElementId;
import com.baselet.control.enums.LineType;
import com.baselet.diagram.DrawPanel;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.element.PropertiesGridElement;
import com.baselet.element.TableCellTextFieldBinding;
import com.baselet.element.facet.Facet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.element.facet.Settings;
import com.baselet.element.facet.common.SeparatorLineFacet;
import com.baselet.element.interfaces.Component;
import com.baselet.element.interfaces.DrawHandlerInterface;
import com.baselet.element.settings.SettingsManualresizeCenter;
import com.baselet.element.sticking.StickableMap;
import com.baselet.element.sticking.StickingPolygon;
import com.baselet.element.sticking.polygon.StickingPolygonGenerator;

public class Service extends PropertiesGridElement {

	private final JTextArea serviceName;
	private JSONObject jsonAttributes;
	private BoundedContext boundedContext;

	private final StickingPolygonGenerator stickingPolygonGenerator = new StickingPolygonGenerator() {
		@Override
		public StickingPolygon generateStickingBorder(Rectangle rect) {
			StickingPolygon p = new StickingPolygon(rect.x, rect.y);

			p.addPoint(0, 0);
			p.addPoint(rect.width, 0);
			p.addPoint(rect.width, rect.height);
			p.addPoint(0, rect.height);
			p.addPoint(0, 0);

			return p;
		}
	};

	public Service() {
		serviceName = new JTextArea();
		serviceName.setLineWrap(true);
		serviceName.setAlignmentY(0);
		serviceName.setBorder(null);
	}

	@Override
	public void drag(Collection<Direction> resizeDirection, int diffX, int diffY, Point mousePosBeforeDrag, boolean isShiftKeyDown, boolean firstDrag, StickableMap stickables, boolean undoable, boolean insideElement) {
		super.drag(resizeDirection, diffX, diffY, mousePosBeforeDrag, isShiftKeyDown, firstDrag, stickables, undoable, insideElement);
		if (!insideElement) {
			DrawPanel drawPanel = component.getDrawPanel();
			if (drawPanel != null) {
				for (BoundedContext boundedContext : drawPanel.getHelper(BoundedContext.class)) {
					if (boundedContext.getRectangle().intersects(getRectangle())) {
						boundedContext.setBorderThick();
					}
					else {
						boundedContext.setBorderNormal();
					}
				}
			}
		}
	}

	@Override
	public void dragEnd() {
		DrawPanel drawPanel = getHandler().getDrawPanel();
		Rectangle rect = getRectangle();
		BoundedContext rightContext = null;
		for (BoundedContext boundedContext : drawPanel.getHelper(BoundedContext.class)) {
			boundedContext.setBorderNothing();
			if (boundedContext.getRectangle().intersects(rect)) {
				rightContext = boundedContext;
			}
		}
		// update old bounded context
		boundedContext = rightContext;

		// update new bounded context
		drawPanel.validateNames();
	}

	public boolean isInBoundedContext(BoundedContext boundedContext) {
		return boundedContext != null && this.boundedContext != null &&
				this.boundedContext == boundedContext;
	}

	@Override
	public void init(Rectangle bounds, String panelAttributes, String additionalAttributes, Component component, DrawHandlerInterface handler, String uuid) {
		super.init(bounds, panelAttributes, additionalAttributes, component, handler, uuid);
		String name = "Service", boundedContext = "";
		try {
			jsonAttributes = new JSONObject(additionalAttributes);
			name = jsonAttributes.getString("name");
			boundedContext = jsonAttributes.getString("boundedContext");
		} catch (Exception ex) {
			jsonAttributes = new JSONObject();
			jsonAttributes.put("name", name);
			jsonAttributes.put("boundedContext", boundedContext);
		}

		serviceName.setText(name);
		component.addComponent(serviceName);

		addProperty("Service Name", name);
		TableCellTextFieldBinding.createBinding(getTableModel(), serviceName, "Service Name");
	}

	public void initBoundedContext(DrawPanel drawPanel) {
		String uuidEntities = jsonAttributes.getString("boundedContext");
		if (uuidEntities != null && uuidEntities.length() > 0) {
			boundedContext = (BoundedContext) drawPanel.getElementById(uuidEntities);
		}
	}

	@Override
	public ElementId getId() {
		return ElementId.DDDService;
	}

	@Override
	public Polygon getBoundingPolygon() {
		Rectangle rect = getRectangle();
		Polygon p = new Polygon();
		p.addPoint(0, 0);
		p.addPoint(rect.width, 0);
		p.addPoint(rect.width, rect.height);
		p.addPoint(0, rect.height);

		p.translate(rect.x, rect.y);

		return p;
	}

	@Override
	protected Settings createSettings() {
		return new SettingsManualresizeCenter() {
			@Override
			public XValues getXValues(double y, int height, int width) {
				return XValues.createForEllipse(y, height, width);
			}

			@Override
			protected List<Facet> createFacets() {
				return listOf(super.createFacets(), SeparatorLineFacet.INSTANCE);
			}
		};
	}

	@Override
	protected void drawCommonContent(PropertiesParserState state) {
		state.setStickingPolygonGenerator(stickingPolygonGenerator);
		serviceName.setBounds(new java.awt.Rectangle(10, 10, getRectangle().width - 20, getRectangle().height - 20));
		DrawHandler drawer = state.getDrawer();

		LineType originalLineType = drawer.getLineType();
		drawer.setLineType(LineType.DOTTED);
		drawer.drawRectangleRound(0, 0, getRectangle().width, getRectangle().height, 10);
		drawer.setLineType(originalLineType);
	}

	@Override
	public String getAdditionalAttributes() {
		jsonAttributes.put("name", serviceName.getText());
		String boundedContextID = "";
		if (boundedContext != null) {
			boundedContextID = boundedContext.getUUID();
		}
		jsonAttributes.put("boundedContext", boundedContextID);
		return jsonAttributes.toString(1);
	}

}
