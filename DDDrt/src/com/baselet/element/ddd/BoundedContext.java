package com.baselet.element.ddd;

import java.awt.Font;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.baselet.control.basics.XValues;
import com.baselet.control.basics.geom.Rectangle;
import com.baselet.control.enums.ElementId;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.element.ComponentSwing;
import com.baselet.element.NewGridElement;
import com.baselet.element.facet.Facet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.element.facet.Settings;
import com.baselet.element.facet.common.SeparatorLineFacet;
import com.baselet.element.interfaces.Component;
import com.baselet.element.interfaces.DrawHandlerInterface;
import com.baselet.element.settings.SettingsManualresizeCenter;
import com.baselet.element.sticking.StickingPolygon;
import com.baselet.element.sticking.polygon.StickingPolygonGenerator;

public class BoundedContext extends NewGridElement {

	private final JTextField contextName;
	private final JTextField packageName;
	private ComponentSwing component;

	public BoundedContext() {
		super();
		contextName = new JTextField("BoundedContext1");
		contextName.setHorizontalAlignment(SwingConstants.CENTER);
		String originalFontName = contextName.getFont().getFontName();
		Font fontName = new Font(originalFontName, Font.PLAIN, 15);
		contextName.setFont(fontName);
		contextName.setBorder(null);

		packageName = new JTextField("com.example.model.context1");
		packageName.setHorizontalAlignment(SwingConstants.CENTER);
		Font fontPackage = new Font(originalFontName, Font.PLAIN, 10);
		packageName.setFont(fontPackage);
		packageName.setBorder(null);
	}

	@Override
	public void init(Rectangle bounds, String panelAttributes, String additionalAttributes, Component component, DrawHandlerInterface handler, String uuid) {
		super.init(bounds, panelAttributes, additionalAttributes, component, handler, uuid);

		this.component = (ComponentSwing) component;
		this.component.add(contextName);
		this.component.add(packageName);
	}

	private final StickingPolygonGenerator stickingPolygonGenerator = new StickingPolygonGenerator() {
		@Override
		public StickingPolygon generateStickingBorder(Rectangle rect) {
			StickingPolygon p = new StickingPolygon(rect.x, rect.y);

			p.addPoint(rect.width / 4.0, 0);
			p.addPoint(rect.width * 3.0 / 4, 0);

			p.addPoint(rect.width, rect.height / 4.0);
			p.addPoint(rect.width, rect.height * 3.0 / 4);

			p.addPoint(rect.width * 3.0 / 4, rect.height);
			p.addPoint(rect.width / 4.0, rect.height);

			p.addPoint(0, rect.height * 3.0 / 4);
			p.addPoint(0, (int) (rect.height / 4.0), true);

			return p;
		}
	};

	@Override
	public ElementId getId() {
		return ElementId.DDDBoundedContext;
	}

	@Override
	protected void drawCommonContent(PropertiesParserState state) {
		DrawHandler drawer = state.getDrawer();
		drawer.drawEllipse(0, 0, getRealSize().width, getRealSize().height);

		state.setStickingPolygonGenerator(stickingPolygonGenerator);

		Rectangle boundsRect = getRectangle();
		contextName.setBounds(boundsRect.width / 2 - 100, 20, 200, 15);
		packageName.setBounds(boundsRect.width / 2 - 150, 35, 300, 10);
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

}
