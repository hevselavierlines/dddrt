package com.baselet.element.ddd;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baselet.control.basics.geom.Point;
import com.baselet.control.basics.geom.PointDouble;
import com.baselet.control.basics.geom.Rectangle;
import com.baselet.control.enums.AlignHorizontal;
import com.baselet.control.enums.Direction;
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
import com.baselet.element.relation.DDDRelation;
import com.baselet.element.settings.SettingsManualResizeTop;
import com.baselet.element.sticking.StickableMap;
import com.baselet.gui.command.AddFieldElement;
import com.baselet.gui.command.Controller;
import com.baselet.gui.command.RemoveFieldElement;
import com.baselet.gui.command.TextFieldChange;

import at.mic.dddrt.db.model.Table;

public abstract class FieldComposite extends NewGridElement implements ActionListener, ICollapseListener, FocusListener, DocumentListener, Comparable<FieldComposite> {

	public static final String FONT_NAME = "Tahoma";
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
	protected BoundedContext boundedContext;
	private boolean nameValid;
	private String originalString;
	private final Font compositeFont;

	public FieldComposite() {
		compositeFont = new Font(FieldComposite.FONT_NAME, Font.PLAIN, 15);

		fieldName = new JTextField();
		fieldName.setHorizontalAlignment(SwingConstants.CENTER);
		fieldName.setBorder(null);
		fieldName.getDocument().addDocumentListener(this);
		fieldName.addFocusListener(this);
		fieldName.setFont(compositeFont);
		propertiesPane = new CollapsiblePanel("Properties");
		propertiesPane.setTitleFont(compositeFont);
		propertiesPane.addCollapseListener(this);
		propertiesPane.setLayout(new GridLayout(0, 1));
		methodsPane = new CollapsiblePanel("Methods");
		methodsPane.setLayout(new GridLayout(0, 1));
		methodsPane.addCollapseListener(this);
		methodsPane.setTitleFont(compositeFont);
		propertyAddButton = new MetalButton("+");
		propertyAddButton.addActionListener(this);
		propertyAddButton.setFont(compositeFont);

		methodAddButton = new MetalButton("+");
		methodAddButton.setFont(compositeFont);
		methodAddButton.addActionListener(this);
		nameValid = true;
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

	public int measureHeight() {
		int startHeight = 0;
		int addHeight = 0;
		// properties
		addHeight = propertiesPane.getComponentCount() * FieldProperty.HEIGHT + propertiesPane.getTitleHeight() + 5;
		if (propertiesPane.isCollapsed()) {
			addHeight = propertiesPane.getTitleHeight();
		}
		startHeight += addHeight + 35;
		addHeight = methodsPane.getComponentCount() * FieldMethod.HEIGHT + methodsPane.getTitleHeight() + 5;
		if (methodsPane.isCollapsed()) {
			addHeight = methodsPane.getTitleHeight();
		}
		// methods
		startHeight += addHeight + 35;
		addHeight = methodsPane.getComponentCount() * FieldMethod.HEIGHT + methodsPane.getTitleHeight() + 5;
		if (methodsPane.isCollapsed()) {
			addHeight = methodsPane.getTitleHeight();
		}
		return startHeight + addHeight + FieldMethod.HEIGHT;
	}

	@Override
	protected void drawCommonContent(PropertiesParserState state) {
		double zoomLevel = getZoom();
		int elementWidth = getRectangle().width;
		int elementHeight = getRectangle().height;
		int realWidth = getRealRectangle().width;
		int realHeight = getRealRectangle().height;
		DrawHandler drawer = state.getDrawer();
		double originalFontSize = drawer.getFontSize();
		drawer.setFontSize(10.0);
		drawer.print(getTitle(), new PointDouble(realWidth / 2, 15), AlignHorizontal.CENTER);

		drawer.setFontSize(20.0);
		Font currentFont = new Font(compositeFont.getFontName(), compositeFont.getStyle(), (int) (compositeFont.getSize() * zoomLevel));
		fieldName.setFont(currentFont);
		fieldName.setBounds(10, 15, elementWidth - 20, (int) (30 * zoomLevel));
		if (nameValid) {
			fieldName.setBackground(Color.WHITE);
			fieldName.setForeground(Color.BLACK);
		}
		else {
			fieldName.setBackground(Color.WHITE);
			fieldName.setForeground(Color.RED);
		}

		drawer.setLineType(LineType.DOTTED);
		drawer.drawLine(0, 45, realWidth, 45);

		int startHeight = 45;
		int addHeight = 0;

		// properties
		addHeight = (int) (propertiesPane.getComponentCount() * FieldProperty.HEIGHT * zoomLevel + propertiesPane.getTitleHeight() + 5);
		if (propertiesPane.isCollapsed()) {
			addHeight = propertiesPane.getTitleHeight();
		}
		propertiesPane.setBounds(0, startHeight, elementWidth, addHeight);

		double originalLineWidth = drawer.getLineWidth();
		propertyAddButton.setBounds(10, startHeight + addHeight, elementWidth - 20, 30);

		// methods
		startHeight += addHeight + 35;
		addHeight = methodsPane.getComponentCount() * FieldMethod.HEIGHT + methodsPane.getTitleHeight() + 5;
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
		drawer.drawRectangle(0, 0, realWidth, realHeight);

		totalHeight = startHeight + addHeight + FieldMethod.HEIGHT;

		updateCompositeHeight();
		validateNames();
	}

	public void validateNames() {
		HashMap<String, FieldProperty> propertyNames = new HashMap<String, FieldProperty>();
		for (java.awt.Component comp : propertiesPane.getComponents()) {
			if (comp instanceof FieldProperty) {
				FieldProperty fieldProperty = (FieldProperty) comp;
				FieldProperty previous = propertyNames.put(fieldProperty.getPropertyName(), fieldProperty);
				fieldProperty.setNameValidity(previous);
			}
		}

		HashMap<String, FieldMethod> methodNames = new HashMap<String, FieldMethod>();
		for (java.awt.Component comp : methodsPane.getComponents()) {
			if (comp instanceof FieldMethod) {
				FieldMethod fieldMethod = (FieldMethod) comp;
				FieldMethod previous = methodNames.put(fieldMethod.getMethodName(), fieldMethod);
				fieldMethod.setNameValidity(previous);
			}
		}
	}

	public void updateBoundedContextBorder() {
		DrawPanel drawPanel = component.getDrawPanel();
		if (drawPanel != null) {
			for (BoundedContext boundedContext : drawPanel.getHelper(BoundedContext.class)) {
				if (boundedContext.getRectangle().contains(getRectangle())) {
					boundedContext.setBorderThick();
				}
				else {
					boundedContext.setBorderNormal();
				}
			}
		}
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
					boundedContext.setBorderNothing();
					if (boundedContext.getRectangle().contains(rect)) {
						rightContext = boundedContext;
					}
				}

				// update old bounded context
				if (boundedContext != null) {
					boundedContext.validateNames();
				}

				boundedContext = rightContext;

				// update new bounded context
				if (boundedContext != null) {
					boundedContext.validateNames();
				}
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
		return null;
	}

	@Override
	public void drag(Collection<Direction> resizeDirection, int diffX, int diffY, Point mousePosBeforeDrag, boolean isShiftKeyDown, boolean firstDrag, StickableMap stickables, boolean undoable, boolean insideElement) {
		super.drag(resizeDirection, diffX, diffY, mousePosBeforeDrag, isShiftKeyDown, firstDrag, stickables, undoable, insideElement);
		if (!insideElement) {
			updateBoundedContextBorder();
		}
	}

	@Override
	public void setRectangleDifference(int diffx, int diffy, int diffw, int diffh, boolean firstDrag, StickableMap stickables, boolean undoable, boolean insideMovement) {
		super.setRectangleDifference(diffx, diffy, diffw, diffh, firstDrag, stickables, undoable, insideMovement);
		if (!insideMovement) {
			updateBoundedContextBorder();
		}
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

	protected abstract FieldProperty addPropertyFromDatabaseColumn(at.mic.dddrt.db.model.TableColumn column);

	protected abstract FieldProperty addProperty(JSONObject jsonObject);

	protected abstract FieldProperty createProperty();

	protected abstract FieldMethod createMethod();

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == propertyAddButton) {
			FieldProperty newProperty = createProperty();
			newProperty.setRemovedListener(this);
			component.getController().executeCommand(new AddFieldElement(newProperty, propertiesPane, this));
		}
		else if (e.getSource() == methodAddButton) {
			FieldMethod newMethod = createMethod();
			newMethod.setRemovedListener(this);
			component.getController().executeCommand(new AddFieldElement(newMethod, methodsPane, this));
		}
		else if (FieldProperty.REMOVED_COMMAND.equals(e.getActionCommand())) {
			removeComponent(e);
		}
	}

	protected void removeComponent(ActionEvent actionEvent) {
		Object source = actionEvent.getSource();
		if (source instanceof java.awt.Component) {
			java.awt.Component deleteComponent = (java.awt.Component) source;

			if (deleteComponent instanceof FieldProperty) {
				component.getController().executeCommand(new RemoveFieldElement(deleteComponent, propertiesPane, this));
			}
			else if (deleteComponent instanceof FieldMethod) {
				component.getController().executeCommand(new RemoveFieldElement(deleteComponent, methodsPane, this));
			}
		}
	}

	@Override
	public void collapseStateChange(boolean collapsed) {
		updateModelFromText();
	}

	public String getName() {
		return fieldName.getText();
	}

	public void initFromDatabase(Table table) {
		fieldName.setText(table.getTableName());
		propertiesPane.removeAll();
		methodsPane.removeAll();
		for (at.mic.dddrt.db.model.TableColumn column : table.getColumns()) {
			FieldProperty property = addPropertyFromDatabaseColumn(column);
			propertiesPane.add(property);
		}
	}

	public String getFullName() {
		StringBuilder stringBuilder = new StringBuilder();
		if (boundedContext != null) {
			stringBuilder.append(boundedContext.getContextName()).append(": ");
		}
		stringBuilder.append(getName());
		return stringBuilder.toString();
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

	public void updateBoundedContext(BoundedContext boundedContext) {
		this.boundedContext = boundedContext;
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

	public boolean isInSameBoundedContext(FieldComposite fieldComposite) {
		if (boundedContext == null || fieldComposite.boundedContext == null) {
			return false;
		}
		else {
			return boundedContext.equals(fieldComposite.boundedContext);
		}
	}

	public void setNameValidity(FieldComposite previous) {
		nameValid = previous == null;
		if (previous != null) {
			fieldName.setBackground(Color.WHITE);
			fieldName.setForeground(Color.RED);
			fieldName.setToolTipText("Duplicated name " + previous.getName());
		}
		else {
			fieldName.setBackground(Color.WHITE);
			fieldName.setForeground(Color.BLACK);
			fieldName.setToolTipText(null);
		}
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		originalString = fieldName.getText();
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		Controller controller = component.getController();
		controller.executeCommand(new TextFieldChange(fieldName, originalString));
	}

	private void validateFieldName() {
		if (boundedContext != null) {
			boundedContext.validateNames();
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		validateFieldName();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		validateFieldName();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		validateFieldName();
	}

	@Override
	public void undoDrag() {
		super.undoDrag();
		for (DDDRelation relation : component.getDrawPanel().getRelationsOfFieldComposite(this)) {
			relation.createRelationLine();
		}
	}

	@Override
	public void redoDrag() {
		super.redoDrag();
		for (DDDRelation relation : component.getDrawPanel().getRelationsOfFieldComposite(this)) {
			relation.createRelationLine();
		}
	}

	@Override
	public ComponentSwing getComponent() {
		return component;
	}

	@Override
	public int compareTo(FieldComposite o) {
		return getFullName().compareTo(o.getFullName());
	}

}
