package com.baselet.element;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baselet.control.basics.geom.PointDouble;
import com.baselet.control.basics.geom.Rectangle;
import com.baselet.control.enums.AlignHorizontal;
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

public abstract class FieldComposite extends NewGridElement implements ActionListener, ICollapseListener {

	private final JButton propertyAddButton;
	private final JButton methodAddButton;
	private final JTextField fieldName;
	private final CollapsiblePanel propertiesPane;
	private final CollapsiblePanel methodsPane;
	protected JSONObject jsonAttributes;
	protected JSONArray jProperties;
	protected JSONArray jMethods;
	private int totalHeight;

	public FieldComposite() {
		fieldName = new JTextField("Name");
		fieldName.setHorizontalAlignment(SwingConstants.CENTER);
		fieldName.setBorder(null);
		propertiesPane = new CollapsiblePanel("Properties");
		propertiesPane.addCollapseListener(this);
		propertiesPane.setLayout(new GridLayout(0, 1));
		methodsPane = new CollapsiblePanel("Methods");
		methodsPane.setLayout(new GridLayout(0, 1));
		methodsPane.addCollapseListener(this);
		propertyAddButton = new JButton("+");
		propertyAddButton.addActionListener(this);

		methodAddButton = new JButton("+");
		methodAddButton.addActionListener(this);
	}

	@Override
	public String getAdditionalAttributes() {
		jsonAttributes.getJSONObject("entities").put("name", fieldName.getText());
		jProperties.clear();
		jMethods.clear();
		for (java.awt.Component property : propertiesPane.getComponents()) {
			if (property instanceof FieldProperty) {
				jProperties.put(((FieldProperty) property).exportToJSON());
			}
		}
		for (java.awt.Component method : methodsPane.getComponents()) {
			if (method instanceof FieldMethod) {
				jMethods.put(((FieldMethod) method).exportToJSON());
			}
		}
		return jsonAttributes.toString(1);
	}

	@Override
	public void init(Rectangle bounds, String panelAttributes, String additionalAttributes, Component component, DrawHandlerInterface handler) {
		super.init(bounds, panelAttributes, additionalAttributes, component, handler);

		jProperties = null;
		try {
			jsonAttributes = new JSONObject(additionalAttributes);
			JSONObject entities = jsonAttributes.getJSONObject("entities");
			fieldName.setText(entities.getString("name"));
			jProperties = entities.getJSONArray("properties");
			jMethods = entities.getJSONArray("methods");
		} catch (Exception ex) {
			createDefaultJSON();
		}

		component.addComponent(fieldName);

		component.addComponent(propertiesPane);
		component.addComponent(methodsPane);
		for (int i = 0; i < jProperties.length(); i++) {
			JSONObject property = jProperties.getJSONObject(i);
			FieldProperty newProperty = FieldProperty.createFromJSON(property);
			newProperty.addRemovedListener(this);
			propertiesPane.add(newProperty);
		}

		for (int i = 0; i < jMethods.length(); i++) {
			JSONObject method = jMethods.getJSONObject(i);
			FieldMethod newMethod = FieldMethod.createFromJSON(method);
			methodsPane.add(newMethod);
		}

		component.addComponent(propertyAddButton);
		component.addComponent(methodAddButton);
	}

	protected abstract void createDefaultJSON();

	@Override
	protected void drawCommonContent(PropertiesParserState state) {
		int elementWidth = getRealSize().width;
		int elementHeight = getRealSize().height;
		DrawHandler drawer = state.getDrawer();
		double originalFontSize = drawer.getFontSize();
		drawer.setFontSize(10.0);
		drawer.print(getTitle(), new PointDouble(getRealSize().width / 2, 15), AlignHorizontal.CENTER);

		drawer.setFontSize(20.0);
		fieldName.setBounds(10, 15, getRealRectangle().getWidth() - 20, 30);

		drawer.setLineType(LineType.DOTTED);
		drawer.drawLine(0, 45, getRealSize().width, 45);

		drawer.setFontSize(12.0);
		// drawer.print("Properties", new PointDouble(5, 56), AlignHorizontal.LEFT);

		int startHeight = 45;
		int addHeight = 0;
		// properties

		addHeight = propertiesPane.getComponentCount() * FieldProperty.HEIGHT + propertiesPane.getTitleHeight();
		if (propertiesPane.isCollapsed()) {
			addHeight = propertiesPane.getTitleHeight();
		}
		propertiesPane.setBounds(0, startHeight, elementWidth, addHeight);
		// for (java.awt.Component comp : component.getAllComponents()) {
		// if (comp instanceof FieldProperty) {
		// comp.setBounds(2, startHeight + addHeight, elementWidth - 4, FieldProperty.HEIGHT);
		// addHeight += FieldProperty.HEIGHT;
		// }
		// }
		double originalLineWidth = drawer.getLineWidth();
		propertyAddButton.setBounds(10, startHeight + addHeight, elementWidth - 20, 30);

		// methods
		startHeight += addHeight + 45;
		addHeight = methodsPane.getComponentCount() * FieldMethod.HEIGHT + methodsPane.getTitleHeight();
		if (methodsPane.isCollapsed()) {
			addHeight = methodsPane.getTitleHeight();
		}
		methodsPane.setBounds(0, startHeight, elementWidth, addHeight);
		drawer.setFontSize(12.0);
		// drawer.print("Methods", new PointDouble(2, startHeight - 2), AlignHorizontal.LEFT);
		for (java.awt.Component comp : component.getAllComponents()) {
			if (comp instanceof FieldMethod) {
				comp.setBounds(2, startHeight + addHeight, elementWidth - 4, FieldMethod.HEIGHT);
				addHeight += FieldMethod.HEIGHT;
			}
		}
		methodAddButton.setBounds(10, startHeight + addHeight, elementWidth - 20, 30);

		drawer.setLineWidth(originalLineWidth);
		drawer.setLineType(LineType.SOLID);
		drawer.setFontSize(originalFontSize);
		drawer.drawRectangle(0, 0, elementWidth, elementHeight);

		totalHeight = startHeight + addHeight + FieldMethod.HEIGHT;
		updateCompositeHeight();
	}

	protected void updateCompositeHeight() {
		Rectangle newRect = getRectangle();
		newRect.height = totalHeight;
		setRectangle(newRect);

	}

	protected abstract String getTitle();

	@Override
	protected Settings createSettings() {
		return new SettingsManualResizeTop() {
			@Override
			protected List<Facet> createFacets() {
				return listOf(super.createFacets(), InnerClassFacet.INSTANCE, SeparatorLineWithHalignChangeFacet.INSTANCE, ActiveClassFacet.INSTANCE, TemplateClassFacet.INSTANCE);
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == propertyAddButton) {
			FieldProperty newProperty = new FieldProperty();
			newProperty.addRemovedListener(this);
			propertiesPane.add(newProperty);

			updateModelFromText();

		}
		else if (e.getSource() == methodAddButton) {
			FieldMethod newMethod = new FieldMethod();
			methodsPane.add(newMethod);

			updateModelFromText();
		}
		else if ("removed".equals(e.getActionCommand())) {
			propertiesPane.remove((java.awt.Component) e.getSource());

			updateModelFromText();
		}
	}

	@Override
	public void collapseStateChange(boolean collapsed) {
		updateModelFromText();
	}
}
