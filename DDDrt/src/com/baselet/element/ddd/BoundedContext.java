package com.baselet.element.ddd;

import java.awt.Font;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baselet.control.basics.XValues;
import com.baselet.control.basics.geom.Point;
import com.baselet.control.basics.geom.PointDouble;
import com.baselet.control.basics.geom.Rectangle;
import com.baselet.control.enums.Direction;
import com.baselet.control.enums.ElementId;
import com.baselet.control.enums.LineType;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.DrawPanel;
import com.baselet.diagram.MainDrawPanel;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.diagram.draw.helper.ColorOwn;
import com.baselet.element.ComponentSwing;
import com.baselet.element.NewGridElement;
import com.baselet.element.PropertiesGridElement;
import com.baselet.element.TableCellChangeBinding;
import com.baselet.element.TableCellTextFieldBinding;
import com.baselet.element.TablePropertyChangeListener;
import com.baselet.element.UndoInformation;
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

import tk.baumi.main.CompositeType;
import tk.baumi.main.IBoundedContext;
import tk.baumi.main.IFieldComposite;

public class BoundedContext extends PropertiesGridElement implements IBoundedContext, TablePropertyChangeListener {

	private static final String JSON_BOUNDEDCONTEXT_PACKAGES = "packages";
	private static final String JSON_BOUNDEDCONTEXT_NAME = "name";
	private static final String JSON_BOUNDEDCONTEXT_MODULES = "modules";
	private final JTextField contextName;
	private final List<JTextField> packageNames;
	private ComponentSwing component;
	private JSONObject jsonAttributes;
	private final List<TableCellTextFieldBinding> bindings;
	private java.awt.Rectangle[] modulesBounds;

	public enum BORDER_STYLE {
		THICK, NORMAL, NOTHING
	};

	private BORDER_STYLE borderStyle;
	private final Font originalContextNameFont;
	private final Font originalPackageNameFont;
	private final int CONTEXT_FONT_SIZE = 15;
	private final int PACKAGE_FONT_SIZE = 10;
	private int modulesAmount;

	public BoundedContext() {
		super();
		contextName = new JTextField("BoundedContext1");
		contextName.setHorizontalAlignment(SwingConstants.CENTER);
		String originalFontName = contextName.getFont().getFontName();
		originalContextNameFont = new Font(originalFontName, Font.PLAIN, CONTEXT_FONT_SIZE);
		contextName.setFont(originalContextNameFont);
		contextName.setBorder(null);

		modulesAmount = 1;
		packageNames = new ArrayList<JTextField>();
		originalPackageNameFont = new Font(originalFontName, Font.PLAIN, PACKAGE_FONT_SIZE);
		for (int i = 0; i < modulesAmount; i++) {
			packageNames.add(createPackage(originalPackageNameFont, i));
		}

		borderStyle = BORDER_STYLE.NOTHING;
		bindings = new ArrayList<TableCellTextFieldBinding>(10);
	}

	public void updateElementsInside(int diffX, int diffY) {
		organiseBoundedContextElements(60);
	}

	private JTextField createPackage(Font originalPackageNameFont, int i) {
		JTextField packageName = new JTextField("com.example.model.context" + i);
		packageName.setHorizontalAlignment(SwingConstants.CENTER);
		packageName.setFont(originalPackageNameFont);
		packageName.setBorder(null);
		String moduleKey = "Module " + i;
		// TableCellTextFieldBinding.createBinding(getTableModel(), packageName, moduleKey);
		return packageName;
	}

	@Override
	public void init(Rectangle bounds, String panelAttributes, String additionalAttributes, Component component, DrawHandlerInterface handler, String uuid) {
		super.init(bounds, panelAttributes, additionalAttributes, component, handler, uuid);

		this.component = (ComponentSwing) component;
		this.component.add(contextName);
		for (int i = 0; i < packageNames.size(); i++) {
			this.component.add(packageNames.get(i));
		}

		try {
			jsonAttributes = new JSONObject(additionalAttributes);
			contextName.setText(jsonAttributes.getString(JSON_BOUNDEDCONTEXT_NAME));
			int modules = jsonAttributes.getInt(JSON_BOUNDEDCONTEXT_MODULES);
			setModulesAmount(modules, modulesAmount);
			modulesAmount = modules;
			JSONArray modulesArray = jsonAttributes.getJSONArray(JSON_BOUNDEDCONTEXT_PACKAGES);
			for (int i = 0; i < modulesArray.length(); i++) {
				String text = modulesArray.getString(i);
				packageNames.get(i).setText(text);
			}

		} catch (Exception ex) {
			createDefaultJSON();
		}

		addProperty("Context Name", contextName.getText(), false);
		// addProperty("Package Name", packageName.getText(), false);
		addProperty("Modules", "1", false);
		for (int i = 0; i < packageNames.size(); i++) {
			String moduleName = "Module " + i;
			addProperty(moduleName, packageNames.get(i).getText());
			bindings.add(TableCellTextFieldBinding.createBinding(getTableModel(), packageNames.get(i), moduleName));
		}

		TableCellTextFieldBinding.createBinding(getTableModel(), contextName, "Context Name");
		TableCellChangeBinding.createBinding(getTableModel(), "Modules", this);
	}

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
	public ElementId getId() {
		return ElementId.DDDBoundedContext;
	}

	private PointDouble p(double x, double y) {
		return new PointDouble(x, y);
	}

	public void organiseElements() {
		for (NewGridElement element : getHandler().getDrawPanel().getBoundedContextChildren(this)) {
			if (element instanceof FieldComposite) {
				organiseElement((FieldComposite) element);
			}
		}
	}

	private String getPackageName(FieldComposite element) {
		java.awt.Rectangle elementRectangle = element.getAwtRectangle();
		elementRectangle.x -= getRectangle().x;
		elementRectangle.y -= getRectangle().y;
		int moduleIndex = -1;
		for (int i = 0; i < modulesBounds.length && moduleIndex < 0; i++) {
			java.awt.Rectangle moduleBound = modulesBounds[i];
			if (moduleBound.contains(elementRectangle)) {
				moduleIndex = i;
			}
		}
		// TODO check modules if overlapping over 3 elements.
		if (moduleIndex < 0) {
			moduleIndex = 0;
		}
		return packageNames.get(moduleIndex).getText();
	}

	public void organiseElement(FieldComposite element) {
		if (modulesBounds != null && modulesAmount > 1) {
			boolean contains = false;
			java.awt.Rectangle elementRectangle = element.getAwtRectangle();
			elementRectangle.x -= getRectangle().x;
			elementRectangle.y -= getRectangle().y;
			for (java.awt.Rectangle moduleBound : modulesBounds) {
				if (moduleBound.contains(elementRectangle)) {
					contains = true;
				}
			}
			if (!contains) {
				List<java.awt.Rectangle> intersections = new ArrayList<java.awt.Rectangle>(3);
				for (java.awt.Rectangle moduleBound : modulesBounds) {
					if (moduleBound.intersects(elementRectangle)) {
						intersections.add(moduleBound);
					}
				}
				if (intersections.size() > 1) {
					java.awt.Rectangle sectionLeft = intersections.get(0);
					int elementDistanceSectionLeft = elementRectangle.x - sectionLeft.x;
					int elementInSectionLeft = sectionLeft.width - elementDistanceSectionLeft;
					int elementRightCorner = elementDistanceSectionLeft + elementRectangle.width;
					int elementInSectionRight = elementRightCorner - sectionLeft.width;
					int gridSize = getGridSize();
					if (elementInSectionLeft > elementInSectionRight) {
						// if (elementInSectionRight % gridSize != 0) {
						// elementInSectionRight += gridSize;
						// }
						element.moveElement(-elementInSectionRight, 0);
					}
					else {
						// if (elementInSectionLeft % gridSize != 0) {
						// elementInSectionLeft += gridSize;
						// }
						element.moveElement(elementInSectionLeft, 0);
					}
					component.getDrawPanel().updateRelations();
				}
				else {
					// System.out.println("NOTHING");
				}
			}
			else {
				// System.out.println("contains");
			}
		}
	}

	@Override
	protected void drawCommonContent(PropertiesParserState state) {
		DrawHandler drawer = state.getDrawer();
		int w = getRealSize().width;
		int h = getRealSize().height;
		LineType originalLineType = drawer.getLineType();
		drawer.setLineType(LineType.DASHED);
		drawer.drawRectangleRound(0, 0, w, h, 25);
		drawer.setLineType(originalLineType);

		state.setStickingPolygonGenerator(stickingPolygonGenerator);

		Rectangle boundsRect = getRectangle();
		Rectangle boundsRealRectangle = getRealRectangle();
		Font newContextFont = originalContextNameFont.deriveFont(Font.BOLD, (int) (CONTEXT_FONT_SIZE * getZoom()));
		Font newPackageFont = originalPackageNameFont.deriveFont(Font.PLAIN, (int) (PACKAGE_FONT_SIZE * getZoom()));
		contextName.setFont(newContextFont);
		contextName.setBounds(0, (int) (10 * getZoom()), boundsRect.width, (int) (20 * getZoom()));

		int moduleWidth = w / modulesAmount;
		int moduleStartY = 28;
		int i = 0;
		JTextField packageName = packageNames.get(i);
		int moduleX = 0;
		modulesBounds = new java.awt.Rectangle[packageNames.size()];
		// modulesBounds[0] = new java.awt.Rectangle((int) (moduleX * getZoom()), (int) (28 * getZoom()), (int) (moduleWidth * getZoom()), (int) (h - (28 + 15) * getZoom()));
		// packageName.setBounds((int) (moduleX * getZoom()), (int) (28 * getZoom()), (int) (moduleWidth * getZoom()), (int) (15 * getZoom()));
		// packageName.setFont(newPackageFont);
		for (i = 0; i < packageNames.size(); i++) {
			moduleX = moduleWidth * i;
			if (i > 0) {
				drawer.drawLine(moduleX, moduleStartY, moduleX, h);
			}
			packageName = packageNames.get(i);
			packageName.setFont(newPackageFont);
			modulesBounds[i] = new java.awt.Rectangle((int) (moduleX * getZoom()), 0, (int) (moduleWidth * getZoom()), (int) (h * getZoom()));

			packageName.setBounds((int) (moduleX * getZoom()), (int) (28 * getZoom()), (int) (moduleWidth * getZoom()), (int) (15 * getZoom()));
		}
		drawer.drawLine(0, moduleStartY - 1, w, moduleStartY - 1);
		// packageName.setBounds(0, (int) (28 * getZoom()), boundsRect.width, (int) (15 * getZoom()));

		double lineWidth = drawer.getLineWidth();
		ColorOwn lineColor = drawer.getForegroundColor();

		if (borderStyle == BORDER_STYLE.NORMAL) {
			drawer.setLineWidth(2.0f);
			drawer.setForegroundColor(ColorOwn.BLUE);
			drawer.drawRectangleRound(1, 1, boundsRealRectangle.width - 2, boundsRealRectangle.height - 2, 25);
		}
		else if (borderStyle == BORDER_STYLE.THICK) {
			drawer.setLineWidth(5.0f);
			drawer.setForegroundColor(ColorOwn.BLUE);
			drawer.drawRectangleRound(3, 3, boundsRealRectangle.width - 6, boundsRealRectangle.height - 6, 25);
		}
		drawer.setLineWidth(lineWidth);
		drawer.setForegroundColor(lineColor);
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
	public void drag(Collection<Direction> resizeDirection, int diffX, int diffY, Point mousePosBeforeDrag, boolean isShiftKeyDown, boolean firstDrag, StickableMap stickables, boolean undoable, boolean insideElement) {
		super.drag(resizeDirection, diffX, diffY, mousePosBeforeDrag, isShiftKeyDown, firstDrag, stickables, undoable, insideElement);
		// if (resizeDirection == null || resizeDirection.isEmpty()) {
		// DrawPanel drawPanel = getHandler().getDrawPanel();
		// for (FieldComposite fieldComposite : drawPanel.getBoundedContextChildren(this)) {
		// fieldComposite.drag(resizeDirection, diffX, diffY, mousePosBeforeDrag, isShiftKeyDown, firstDrag, stickables, undoable);
		// }
		// }
	}

	protected void createDefaultJSON() {
		jsonAttributes = new JSONObject();
		jsonAttributes.put(JSON_BOUNDEDCONTEXT_NAME, "boundedContext");
		JSONArray modulesArray = new JSONArray();
		for (JTextField module : packageNames) {
			modulesArray.put(module.getText());
		}
		jsonAttributes.put(JSON_BOUNDEDCONTEXT_PACKAGES, modulesArray);
		jsonAttributes.put(JSON_BOUNDEDCONTEXT_MODULES, 1);
	}

	@Override
	public String getAdditionalAttributes() {
		jsonAttributes.put(JSON_BOUNDEDCONTEXT_NAME, contextName.getText());
		JSONArray modulesArray = new JSONArray();
		for (JTextField module : packageNames) {
			modulesArray.put(module.getText());
		}
		jsonAttributes.put(JSON_BOUNDEDCONTEXT_PACKAGES, modulesArray);
		jsonAttributes.put(JSON_BOUNDEDCONTEXT_MODULES, modulesAmount);
		return jsonAttributes.toString(1);
	}

	@Override
	public void dragEnd() {
		checkFieldCompositesInsideBoundedContext();

		// validateNames();
		organiseElements();
		borderStyle = BORDER_STYLE.NOTHING;
		updateModelFromText();

		updateParentPanel();
	}

	public Rectangle getDiffRectangle() {
		UndoInformation undoInfo = undoStack.get(true);
		Rectangle diffRectangle = null;
		if (undoInfo != null) {
			diffRectangle = undoInfo.getDiffRectangle(getGridSize(), true);
			diffRectangle = diffRectangle.copyInverted();
		}
		return diffRectangle;
	}

	private void updateParentPanel() {
		if (getHandler().getDrawPanel() != null) {
			DrawPanel dp = getHandler().getDrawPanel();
			if (dp instanceof MainDrawPanel) {
				((MainDrawPanel) dp).updateBoundedContext(this);
			}
		}
	}

	protected void checkFieldCompositesInsideBoundedContext() {
		CurrentDiagram diagram = CurrentDiagram.getInstance();
		if (diagram != null) {
			DiagramHandler handler = diagram.getDiagramHandler();
			if (handler != null) {
				DrawPanel drawPanel = handler.getDrawPanel();
				for (FieldComposite fieldComposite : drawPanel.getHelperAndSub(FieldComposite.class)) {
					Rectangle rect = fieldComposite.getRectangle();
					if (getRectangle().contains(rect)) {
						fieldComposite.updateBoundedContext(this);
					}
				}

			}
		}
	}

	@Override
	public String getContextName() {
		return contextName.getText();
	}

	@Override
	public String getPackageName() {
		return packageNames.get(0).getText();
	}

	@Override
	public String getPackageName(IFieldComposite fieldComposite) {
		return getPackageName((FieldComposite) fieldComposite);
	}

	public void setBorderThick() {
		borderStyle = BORDER_STYLE.THICK;
		updateModelFromText();
	}

	public void setBorderNormal() {
		borderStyle = BORDER_STYLE.NORMAL;
		updateModelFromText();
	}

	public void setBorderNothing() {
		borderStyle = BORDER_STYLE.NOTHING;
		updateModelFromText();
	}

	@Override
	public List<IFieldComposite> getContainingComposites() {
		List<NewGridElement> elements = component.getDrawPanel().getBoundedContextChildren(this);
		List<IFieldComposite> fieldComposites = new ArrayList<IFieldComposite>(elements.size());
		for (NewGridElement element : elements) {
			if (element instanceof IFieldComposite) {
				fieldComposites.add((IFieldComposite) element);
			}
		}
		return fieldComposites;
	}

	public void setModulesAmount(int modulesAmount, int originalAmount) {
		if (modulesAmount > originalAmount) {
			for (int i = originalAmount; i < modulesAmount; i++) {
				JTextField newTextField = createPackage(originalPackageNameFont, i);
				String moduleName = "Module " + i;
				addProperty(moduleName, "", false);
				bindings.add(TableCellTextFieldBinding.createBinding(getTableModel(), newTextField, moduleName));
				component.add(newTextField);
				packageNames.add(newTextField);
			}
		}
		else if (modulesAmount < originalAmount) {
			for (int i = originalAmount - 1; i >= modulesAmount; i--) {
				JTextField removeTextField = packageNames.get(i);
				removeProperty("Module " + i);
				packageNames.remove(removeTextField);
				component.remove(removeTextField);
				for (int j = bindings.size() - 1; j >= 0; j--) {
					if (bindings.get(j).isTextField(removeTextField)) {
						bindings.get(j).dispose();
						bindings.remove(j);
					}
				}
			}
		}
	}

	@Override
	public void propertyChange(String propertyName, String newText) {
		try {
			int modules = Integer.parseInt(newText);
			if (modules >= 1 && modules <= 10) {
				if (modulesAmount != modules) {
					int originalAmount = modulesAmount;
					modulesAmount = modules;
					setModulesAmount(modules, originalAmount);
				}
			}
			updateModelFromText();
		} catch (NumberFormatException ex) {
			// swallow exception as the user might type in an empty or a wrong number.
		}
	}

	public int organiseBoundedContextElements(int startY) {
		List<NewGridElement> elements = getHandler().getDrawPanel().getBoundedContextChildren(this);
		Collections.sort(elements, new Comparator<NewGridElement>() {

			@Override
			public int compare(NewGridElement o1, NewGridElement o2) {
				int ret = 0;
				if (o1 instanceof IFieldComposite && o2 instanceof IFieldComposite) {
					IFieldComposite fieldComposite1 = (IFieldComposite) o1;
					IFieldComposite fieldComposite2 = (IFieldComposite) o2;
					if (fieldComposite1.getType() == CompositeType.Service && fieldComposite2.getType() != CompositeType.Service) {
						ret = -1;
					}
					else if (fieldComposite1.getType() != CompositeType.Service && fieldComposite2.getType() == CompositeType.Service) {
						ret = 1;
					}
				}
				if (ret == 0) {
					ret = o1.getRectangle().y - o2.getRectangle().y;
				}
				return ret;
			}
		});
		for (NewGridElement element : elements) {
			int width = getRectangle().width;
			Rectangle rect = element.getRectangle();
			rect.x = getRectangle().x + 10;
			rect.y = getRectangle().y + startY;
			startY += rect.height + 10;
			rect.width = width - 20;
			element.setRectangle(rect);
			element.dragEnd();
			element.updateModelFromText();
		}
		return startY;
	}
}
