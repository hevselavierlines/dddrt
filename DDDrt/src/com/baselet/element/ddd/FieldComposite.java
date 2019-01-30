package com.baselet.element.ddd;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
import com.baselet.diagram.draw.DrawHandler.Layer;
import com.baselet.element.ComponentSwing;
import com.baselet.element.FieldTypeChange;
import com.baselet.element.ICollapseListener;
import com.baselet.element.PropertiesGridElement;
import com.baselet.element.TableCellTextFieldBinding;
import com.baselet.element.TableCellTypeChange;
import com.baselet.element.facet.Facet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.element.facet.Settings;
import com.baselet.element.facet.common.SeparatorLineFacet;
import com.baselet.element.interfaces.Component;
import com.baselet.element.interfaces.DrawHandlerInterface;
import com.baselet.element.relation.DDDRelation;
import com.baselet.element.settings.SettingsManualresizeCenter;
import com.baselet.element.sticking.StickableMap;
import com.baselet.gui.CurrentGui;
import com.baselet.gui.command.AddFieldElement;
import com.baselet.gui.command.Controller;
import com.baselet.gui.command.FieldCompositeTypeChangeCommand;
import com.baselet.gui.command.RemoveFieldElement;
import com.baselet.gui.command.TextFieldChange;
import com.baselet.gui.pane.OwnSyntaxPane;

import at.mic.dddrt.db.model.Table;

public abstract class FieldComposite extends PropertiesGridElement implements ActionListener, ICollapseListener, FocusListener, DocumentListener, Comparable<FieldComposite>, FieldTypeChange {

	private static final int ADD_BUTTON_HEIGHT = 25;
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
	protected ComponentSwing component;
	protected BoundedContext boundedContext;
	private boolean nameValid;
	private String originalString;
	private final Font compositeFont;
	private FieldProperty selection;
	private TableCellTypeChange tableCellTypeChange;
	protected static Image addButtomImage;

	public FieldComposite() {
		compositeFont = new Font(FieldComposite.FONT_NAME, Font.PLAIN, 15);

		fieldName = new JTextField();
		fieldName.setHorizontalAlignment(SwingConstants.CENTER);
		fieldName.setBorder(null);
		fieldName.setBackground(new Color(0, 0, 0, 0));
		fieldName.setOpaque(false);
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
		propertyAddButton = new MetalButton("");
		methodAddButton = new MetalButton("");
		try {
			if (addButtomImage == null) {
				addButtomImage = ImageIO.read(new File("img/add_button.png"));
			}
			Image img = addButtomImage.getScaledInstance(ADD_BUTTON_HEIGHT, ADD_BUTTON_HEIGHT, Image.SCALE_FAST);
			propertyAddButton.setIcon(new ImageIcon(img));
			propertyAddButton.setBorderPainted(false);
			propertyAddButton.setFocusPainted(false);
			propertyAddButton.setContentAreaFilled(false);

			methodAddButton.setIcon(new ImageIcon(img));
			methodAddButton.setBorderPainted(false);
			methodAddButton.setFocusPainted(false);
			methodAddButton.setContentAreaFilled(false);
		} catch (Exception ex) {
			propertyAddButton.setText("+");
			propertyAddButton.setFont(compositeFont);

			methodAddButton.setText("+");
			methodAddButton.setFont(compositeFont);
		}

		methodAddButton.addActionListener(this);
		propertyAddButton.addActionListener(this);

		nameValid = true;
	}

	public void selectProperty(FieldProperty fieldProperty) {
		OwnSyntaxPane pane = CurrentGui.getInstance().getGui().getPropertyPane();
		deselectAll();
		selection = fieldProperty;
		fieldProperty.setSelection(true);
		fieldProperty.repaint();
		pane.switchToProperty(fieldProperty);
	}

	public void deselectAll() {
		OwnSyntaxPane pane = CurrentGui.getInstance().getGui().getPropertyPane();
		if (selection != null) {
			selection.setSelection(false);
			pane.deselectProperty();
			selection.repaint();
			selection = null;
		}
	}

	public FieldProperty getPropertyByPosition(Point position) {
		position.x -= getRectangle().x;
		position.y -= getRectangle().y;
		deselectAll();
		for (java.awt.Component property : propertiesPane.getComponents()) {
			if (property instanceof FieldProperty) {
				FieldProperty fieldProperty = (FieldProperty) property;
				java.awt.Rectangle bounds = fieldProperty.getBounds();
				bounds.y += propertiesPane.getBounds().y;
				bounds.x += propertiesPane.getBounds().x;
				if (bounds.contains(new java.awt.Point(position.x, position.y))) {
					selectProperty(fieldProperty);
				}
			}
		}
		return null;
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
		component.setDrawBackground(true);
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
		this.component.setBackground(getBackgroundColor());

		addProperty("Type", "Entity", false);
		addProperty("Class Name", getName(), false);
		addProperty("Database Name", getName(), false);
		addProperty("Notes", getName(), false);

		TableCellTextFieldBinding.createBinding(getTableModel(), fieldName, "Class Name");
		tableCellTypeChange = new TableCellTypeChange(getTableModel(), "Type", this);
	}

	protected abstract Color getBackgroundColor();

	public void updateTypeOnTable() {
		String type = "Entity";
		if (this instanceof AggregateComposite) {
			type = "Aggregate";
		}
		else if (this instanceof EntityComposite) {
			type = "Entity";
		}
		else if (this instanceof ValueObjectComposite) {
			type = "Value Object";
		}
		tableCellTypeChange.preventUpdate(type);
		addProperty("Type", type);
		tableCellTypeChange.stopPreventUpdate();
	}

	protected abstract void createDefaultJSON();

	public int measureHeight() {
		int startHeight = 0;
		int addHeight = 0;
		// properties
		addHeight = propertiesPane.getComponentCount() * FieldProperty.DEFAULT_HEIGHT + propertiesPane.getTitleHeight() + 5;
		if (propertiesPane.isCollapsed()) {
			addHeight = propertiesPane.getTitleHeight();
		}
		startHeight += addHeight + 35;
		addHeight = methodsPane.getComponentCount() * FieldMethod.DEFAULT_HEIGHT + methodsPane.getTitleHeight() + 5;
		if (methodsPane.isCollapsed()) {
			addHeight = methodsPane.getTitleHeight();
		}
		// methods
		startHeight += addHeight + 35;
		addHeight = methodsPane.getComponentCount() * FieldMethod.DEFAULT_HEIGHT + methodsPane.getTitleHeight() + 5;
		if (methodsPane.isCollapsed()) {
			addHeight = methodsPane.getTitleHeight();
		}
		return startHeight + addHeight + FieldMethod.DEFAULT_HEIGHT;
	}

	@Override
	public String getPanelAttributes() {
		return super.getPanelAttributes();
	}

	@Override
	public List<String> getPanelAttributesAsList() {
		return super.getPanelAttributesAsList();
	}

	@Override
	protected void drawCommonContent(PropertiesParserState state) {
		double zoomLevel = getZoom();
		int elementWidth = getRectangle().width;
		int elementHeight = getRectangle().height;
		int realWidth = getRealRectangle().width;
		int realHeight = getRealRectangle().height;
		DrawHandler drawer = state.getDrawer();
		drawer.setLayer(Layer.Foreground);
		double originalFontSize = drawer.getFontSize();
		drawer.setFontSize(10.0);
		int offsetY = 0;
		offsetY += (int) (zoomLevel * 15);
		drawer.print(getTitle(), new PointDouble(realWidth / 2, 15), AlignHorizontal.CENTER);

		drawer.setFontSize(20.0);
		Font currentFont = new Font(compositeFont.getFontName(), compositeFont.getStyle(), (int) (compositeFont.getSize() * zoomLevel));
		fieldName.setFont(currentFont);
		fieldName.setBounds(10, offsetY, elementWidth - 20, (int) (30 * zoomLevel));
		offsetY += (int) (30 * zoomLevel);
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

		int startHeight = offsetY;
		int addHeight = 0;

		propertiesPane.setVisible(true);

		// properties
		propertiesPane.setZoomLevel(zoomLevel);
		addHeight = propertiesPane.getTitleHeight();
		// addHeight = (int) (propertiesPane.getComponentCount() * FieldProperty.getProperty + propertiesPane.getTitleHeight() + 5);
		for (java.awt.Component component : propertiesPane.getComponents()) {
			if (component instanceof FieldProperty) {
				FieldProperty fieldProperty = (FieldProperty) component;
				fieldProperty.setZoomLevel(zoomLevel);
				addHeight += fieldProperty.getFieldHeight();
			}
		}
		// if (propertiesPane.isCollapsed()) {
		// addHeight += propertiesPane.getTitleHeight();
		// }
		propertiesPane.setBounds(0, startHeight, elementWidth, addHeight);

		double originalLineWidth = drawer.getLineWidth();
		propertyAddButton.setVisible(true);
		int addButtonHeight = (int) (zoomLevel * ADD_BUTTON_HEIGHT);
		Image img = addButtomImage.getScaledInstance(addButtonHeight, addButtonHeight, Image.SCALE_FAST);
		propertyAddButton.setIcon(new ImageIcon(img));
		propertyAddButton.setBounds(10, startHeight + addHeight, elementWidth - 20, addButtonHeight);

		// methods
		startHeight += addHeight + addButtonHeight;
		addHeight = methodsPane.getTitleHeight();
		methodsPane.setVisible(true);
		methodsPane.setZoomLevel(zoomLevel);
		for (java.awt.Component component : methodsPane.getComponents()) {
			if (component instanceof FieldMethod) {
				FieldMethod fieldMethod = (FieldMethod) component;
				fieldMethod.setZoomLevel(zoomLevel);
				addHeight += fieldMethod.getFieldHeight();
			}
		}
		// addHeight = methodsPane.getComponentCount() * FieldMethod.HEIGHT + methodsPane.getTitleHeight() + 5;
		// if (methodsPane.isCollapsed()) {
		// addHeight = methodsPane.getTitleHeight();
		// }
		methodsPane.setBounds(0, startHeight, elementWidth, addHeight);
		// for (java.awt.Component comp : component.getAllComponents()) {
		// if (comp instanceof FieldMethod) {
		// comp.setBounds(2, startHeight + addHeight, elementWidth - 4, FieldMethod.HEIGHT);
		// addHeight += FieldMethod.HEIGHT;
		// }
		// }
		methodAddButton.setVisible(true);
		startHeight += addHeight;
		methodAddButton.setIcon(new ImageIcon(img));
		methodAddButton.setBounds(10, startHeight, elementWidth - 20, addButtonHeight);

		startHeight += addButtonHeight + 10;

		drawer.setLineWidth(originalLineWidth);
		drawer.setLineType(LineType.SOLID);
		drawer.setFontSize(originalFontSize);

		totalHeight = startHeight;

		updateCompositeHeight();

		drawer.setLayer(Layer.Background);
		// drawer.setBackgroundColor(ColorOwn.CYAN);
		drawer.drawRectangle(0, 0, realWidth, getRealRectangle().height);
		drawer.setLayer(Layer.Foreground);

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
				if (boundedContext.getRectangle().intersects(getRectangle())) {
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
					if (boundedContext.getRectangle().intersects(rect)) {
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
		return new SettingsManualresizeCenter() {

			@Override
			protected List<Facet> createFacets() {
				return listOf(super.createFacets(), SeparatorLineFacet.INSTANCE);
			}
		};
	}

	protected abstract FieldProperty addPropertyFromDatabaseColumn(at.mic.dddrt.db.model.TableColumn column);

	protected abstract FieldProperty addProperty(JSONObject jsonObject);

	protected abstract FieldProperty createProperty();

	protected abstract FieldMethod createMethod();

	@Override
	public void actionPerformed(ActionEvent e) {
		deselectAll();
		if (e.getSource() == propertyAddButton) {
			FieldProperty newProperty = createProperty();
			newProperty.setRemovedListener(this);
			component.getController().executeCommand(new AddFieldElement(newProperty, propertiesPane, this));
			selectProperty(newProperty);
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
		fieldName.setText(table.getTableNameAsCamelCase());
		propertiesPane.removeAll();
		methodsPane.removeAll();
		addProperty("Type", "Entity");
		addProperty("Class Name", table.getTableNameAsCamelCase());
		addProperty("Database Name", table.getTableName());
		addProperty("Notes", table.generateNotes());
		for (at.mic.dddrt.db.model.TableColumn column : table.getColumns()) {
			FieldProperty property = addPropertyFromDatabaseColumn(column);
			property.setRemovedListener(this);
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

	public java.awt.Point getAbsolutePosition(boolean bottom) {
		java.awt.Point p = new java.awt.Point();

		// getAbsolutePositionRecursively(component, p);
		p.x = getRealRectangle().x;
		p.y = getRealRectangle().y;
		p.x += getRealRectangle().getWidth() / 2;
		if (bottom) {
			p.y += zoom(totalHeight);
			// p.y += getRealRectangle().getHeight();// - 1.0f;
		}
		return p;
	}

	private void getAbsolutePositionRecursively(java.awt.Component currentComponent, java.awt.Point point) {
		if (currentComponent != null && !(currentComponent instanceof DrawPanel)) {
			point.x += currentComponent.getBounds().x;
			point.y += currentComponent.getBounds().y;
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

	public List<FieldProperty> getPropertiesWithRelation() {
		List<FieldProperty> properties = new LinkedList<FieldProperty>();
		for (java.awt.Component property : propertiesPane.getComponents()) {
			if (property instanceof FieldProperty) {
				FieldProperty fieldProperty = (FieldProperty) property;
				if (fieldProperty.getRelation() != null) {
					properties.add(fieldProperty);
				}
			}
		}
		return properties;
	}

	public void updateBoundedContext(BoundedContext boundedContext) {
		this.boundedContext = boundedContext;
	}

	public boolean isInBoundedContext(BoundedContext boundedContext) {
		return boundedContext != null && this.boundedContext != null &&
				this.boundedContext == boundedContext;
	}

	public BoundedContext getBoundedContext() {
		return boundedContext;
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

	@Override
	public void typeChanged(String newType) {
		ElementId newId = null;
		if ("Entity".equals(newType)) {
			newId = ElementId.DDDEntity;
		}
		else if ("Value Object".equalsIgnoreCase(newType)) {
			newId = ElementId.DDDValueObject;
		}
		else if ("Aggregate".equalsIgnoreCase(newType)) {
			newId = ElementId.DDDAggregate;
		}
		if (newId != null) {
			DiagramHandler diagramHandler = CurrentDiagram.getInstance().getDiagramHandler();
			diagramHandler.getController().executeCommand(
					new FieldCompositeTypeChangeCommand(this, newId));
		}
	}
}
