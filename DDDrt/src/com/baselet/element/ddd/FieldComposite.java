package com.baselet.element.ddd;

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
import com.baselet.control.enums.ElementId;
import com.baselet.control.enums.LineType;
import com.baselet.design.metal.MetalButton;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.DrawPanel;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.element.ComponentSwing;
import com.baselet.element.ICollapseListener;
import com.baselet.element.NewGridElement;
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
	private ComponentSwing component;
	private BoundedContext boundedContext;

	public FieldComposite() {
		fieldName = new JTextField();
		fieldName.setHorizontalAlignment(SwingConstants.CENTER);
		fieldName.setBorder(null);
		propertiesPane = new CollapsiblePanel("Properties");
		propertiesPane.addCollapseListener(this);
		propertiesPane.setLayout(new GridLayout(0, 1));
		methodsPane = new CollapsiblePanel("Methods");
		methodsPane.setLayout(new GridLayout(0, 1));
		methodsPane.addCollapseListener(this);
		propertyAddButton = new MetalButton("+");
		propertyAddButton.addActionListener(this);

		methodAddButton = new MetalButton("+");
		methodAddButton.addActionListener(this);
	}

	@Override
	public String getAdditionalAttributes() {
		JSONObject entities = jsonAttributes.getJSONObject("entities");
		entities.put("name", fieldName.getText());
		entities.put("boundedContext", getBoundedContextUUID());
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

	public void init(Rectangle bounds, String panelAttributes, String additionalAttributes, Component component, DrawHandlerInterface handler) {
		init(bounds, panelAttributes, additionalAttributes, component, handler, null);
	}

	@Override
	public void init(Rectangle bounds, String panelAttributes, String additionalAttributes, Component component, DrawHandlerInterface handler, String uuid) {
		super.init(bounds, panelAttributes, additionalAttributes, component, handler, uuid);

		jProperties = null;
		try {
			jsonAttributes = new JSONObject(additionalAttributes);
			JSONObject entities = jsonAttributes.getJSONObject("entities");
			jProperties = entities.getJSONArray("properties");
			jMethods = entities.getJSONArray("methods");
		} catch (Exception ex) {
			createDefaultJSON();
		}

		JSONObject entities = jsonAttributes.getJSONObject("entities");
		fieldName.setText(entities.getString("name"));
		component.addComponent(fieldName);

		component.addComponent(propertiesPane);
		component.addComponent(methodsPane);
		for (int i = 0; i < jProperties.length(); i++) {
			JSONObject property = jProperties.getJSONObject(i);
			FieldProperty newProperty = addProperty(property);
			newProperty.setRemovedListener(this);
			propertiesPane.add(newProperty);
		}

		for (int i = 0; i < jMethods.length(); i++) {
			JSONObject method = jMethods.getJSONObject(i);
			FieldMethod newMethod = FieldMethod.createFromJSON(method);
			newMethod.setRemovedListener(this);
			methodsPane.add(newMethod);
		}

		component.addComponent(propertyAddButton);
		component.addComponent(methodAddButton);

		this.component = (ComponentSwing) component;
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

		int startHeight = 45;
		int addHeight = 0;

		// properties
		addHeight = propertiesPane.getComponentCount() * FieldProperty.HEIGHT + propertiesPane.getTitleHeight();
		if (propertiesPane.isCollapsed()) {
			addHeight = propertiesPane.getTitleHeight();
		}
		propertiesPane.setBounds(0, startHeight, elementWidth, addHeight);

		double originalLineWidth = drawer.getLineWidth();
		propertyAddButton.setBounds(10, startHeight + addHeight, elementWidth - 20, 30);

		// methods
		startHeight += addHeight + 35;
		addHeight = methodsPane.getComponentCount() * FieldMethod.HEIGHT + methodsPane.getTitleHeight();
		if (methodsPane.isCollapsed()) {
			addHeight = methodsPane.getTitleHeight();
		}
		methodsPane.setBounds(0, startHeight, elementWidth, addHeight);
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

	protected void updateBoundedContext() {
		CurrentDiagram diagram = CurrentDiagram.getInstance();
		if (diagram != null) {
			DiagramHandler handler = diagram.getDiagramHandler();
			if (handler != null) {
				Rectangle rect = getRectangle();

				DrawPanel drawPanel = handler.getDrawPanel();
				BoundedContext rightContext = null;
				for (BoundedContext boundedContext : drawPanel.getHelper(BoundedContext.class)) {
					if (boundedContext.getRectangle().contains(rect)) {
						rightContext = boundedContext;
					}
				}

				boundedContext = rightContext;
			}
		}
	}

	protected void updateCompositeHeight() {
		Rectangle newRect = getRectangle();
		newRect.height = totalHeight;
		setRectangle(newRect);
	}

	@Override
	public ElementId getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dragEnd() {
		updateBoundedContext();
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

	protected abstract FieldProperty addProperty(JSONObject jsonObject);

	protected abstract FieldProperty createProperty();

	protected abstract FieldMethod createMethod();

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == propertyAddButton) {
			FieldProperty newProperty = createProperty();
			newProperty.setRemovedListener(this);
			propertiesPane.add(newProperty);

			updateModelFromText();

		}
		else if (e.getSource() == methodAddButton) {
			FieldMethod newMethod = createMethod();
			newMethod.setRemovedListener(this);
			methodsPane.add(newMethod);

			updateModelFromText();
		}
		else if (FieldProperty.REMOVED_COMMAND.equals(e.getActionCommand())) {
			removeComponent(e);
		}
	}

	protected void removeComponent(ActionEvent actionEvent) {
		Object source = actionEvent.getSource();
		if (source instanceof java.awt.Component) {
			java.awt.Component deleteComponent = (java.awt.Component) source;
			propertiesPane.remove(deleteComponent);
			propertiesPane.updateBorderTitle();

			methodsPane.remove(deleteComponent);
			methodsPane.updateBorderTitle();
			updateModelFromText();
		}
	}

	@Override
	public void collapseStateChange(boolean collapsed) {
		updateModelFromText();
	}

	public String getName() {
		return fieldName.getText();
	}

	public java.awt.Point getAbsolutePosition() {
		java.awt.Point p = new java.awt.Point();
		getAbsolutePositionRecursively(component, p);
		p.x += component.getWidth() / 2;
		return p;
	}

	private void getAbsolutePositionRecursively(java.awt.Component currentComponent, java.awt.Point point) {
		if (currentComponent != null && !(currentComponent instanceof DrawPanel)) {
			point.x += currentComponent.getLocation().x;
			point.y += currentComponent.getLocation().y;
			getAbsolutePositionRecursively(currentComponent.getParent(), point);
		}
	}

	public FieldProperty getPropertyByName(String name) {
		FieldProperty element = null;
		for (java.awt.Component property : propertiesPane.getComponents()) {
			if (property instanceof FieldProperty) {
				FieldProperty fieldProperty = (FieldProperty) property;
				if (name.equals(fieldProperty.getPropertyName())) {
					element = fieldProperty;
				}
			}
		}
		return element;
	}

	public boolean isInBoundedContext(BoundedContext boundedContext) {
		return boundedContext != null && this.boundedContext != null &&
				this.boundedContext == boundedContext;
	}

	public String getBoundedContextUUID() {
		if (boundedContext != null) {
			return boundedContext.getUUID();
		}
		else {
			return "";
		}
	}

	public void initBoundedContext(DrawPanel dp) {
		JSONObject entities = jsonAttributes.getJSONObject("entities");
		String uuidEntities = entities.getString("boundedContext");
		if (uuidEntities != null && uuidEntities.length() > 0) {
			boundedContext = (BoundedContext) dp.getElementById(uuidEntities);
		}
	}
}
