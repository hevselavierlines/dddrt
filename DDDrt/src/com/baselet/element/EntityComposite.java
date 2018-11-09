package com.baselet.element;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import com.baselet.control.basics.geom.PointDouble;
import com.baselet.control.basics.geom.Rectangle;
import com.baselet.control.enums.AlignHorizontal;
import com.baselet.control.enums.ElementId;
import com.baselet.control.enums.LineType;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.element.facet.Facet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.element.facet.Settings;
import com.baselet.element.facet.common.SeparatorLineWithHalignChangeFacet;
import com.baselet.element.facet.specific.ActiveClassFacet;
import com.baselet.element.facet.specific.InnerClassFacet;
import com.baselet.element.facet.specific.TemplateClassFacet;
import com.baselet.element.interfaces.Component;
import com.baselet.element.interfaces.DrawHandlerInterface;
import com.baselet.element.settings.SettingsManualResizeTop;

public class EntityComposite extends NewGridElement {

	@Override
	public ElementId getId() {
		return ElementId.DDDEntity;
	}

	private final List<EntityProperty> properties;
	private final JButton addButton;

	public EntityComposite() {
		properties = new ArrayList<EntityProperty>();
		addButton = new JButton("+");
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// component.addComponent(new EntityProperty());
				EntityProperty newProperty = new EntityProperty();
				component.addComponent(newProperty);
				String attributes = getPanelAttributes();
				attributes += "\n" + newProperty.toString();
				setPanelAttributes(attributes);

			}
		});

	}

	@Override
	public void init(Rectangle bounds, String panelAttributes, String additionalAttributes, Component component, DrawHandlerInterface handler) {
		super.init(bounds, panelAttributes, additionalAttributes, component, handler);

		for (String line : getPanelAttributesAsList()) {
			if (line.startsWith("prop")) {
				EntityProperty newProperty = EntityProperty.createFromString(line);
				if (newProperty != null) {
					component.addComponent(newProperty);
				}
			}
		}
		component.addComponent(addButton);
	}

	@Override
	protected void drawCommonContent(PropertiesParserState state) {
		int elementWidth = getRealSize().width;
		int elementHeight = getRealSize().height;
		DrawHandler drawer = state.getDrawer();
		double originalFontSize = drawer.getFontSize();
		drawer.setFontSize(10.0);
		drawer.print("<<Entity>>", new PointDouble(getRealSize().width / 2, 15), AlignHorizontal.CENTER);

		String title = "";
		if (panelAttributes != null && panelAttributes.size() > 0) {
			title = panelAttributes.get(0);
		}
		drawer.setFontSize(20.0);
		drawer.print(title, new PointDouble(getRealSize().width / 2, 35), AlignHorizontal.CENTER);
		drawer.setLineType(LineType.DOTTED);
		drawer.drawLine(0, 45, getRealSize().width, 45);

		drawer.setFontSize(12.0);
		drawer.print("Properties", new PointDouble(5, 56), AlignHorizontal.LEFT);

		if (properties != null) {
			int startHeight = 60;
			int addHeight = 0;
			for (java.awt.Component comp : component.getAllComponents()) {
				if (comp instanceof EntityProperty) {
					comp.setBounds(2, startHeight + addHeight, elementWidth - 4, 40);
					addHeight += 40;
				}
			}
			double originalLineWidth = drawer.getLineWidth();
			addButton.setBounds(2, startHeight + addHeight, elementWidth - 4, 30);
			drawer.setLineType(LineType.DASHED);
			drawer.setLineWidth(2.0);
			drawer.drawRectangle(0, 45, elementWidth, addHeight + 15 + 30);
			drawer.setLineWidth(originalLineWidth);
		}

		drawer.setLineType(LineType.SOLID);
		drawer.setFontSize(originalFontSize);
		drawer.drawRectangle(0, 0, elementWidth, elementHeight);
	}

	@Override
	protected Settings createSettings() {
		return new SettingsManualResizeTop() {
			@Override
			protected List<Facet> createFacets() {
				return listOf(super.createFacets(), InnerClassFacet.INSTANCE, SeparatorLineWithHalignChangeFacet.INSTANCE, ActiveClassFacet.INSTANCE, TemplateClassFacet.INSTANCE);
			}
		};
	}

}
