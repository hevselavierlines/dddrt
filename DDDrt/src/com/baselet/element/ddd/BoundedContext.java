package com.baselet.element.ddd;

import java.awt.Font;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.SwingConstants;

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
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.diagram.draw.helper.ColorOwn;
import com.baselet.element.ComponentSwing;
import com.baselet.element.NewGridElement;
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

import tk.baumi.main.IBoundedContext;
import tk.baumi.main.IFieldComposite;

public class BoundedContext extends PropertiesGridElement implements IBoundedContext {

	private static final String JSON_BOUNDEDCONTEXT_PACKAGE = "package";
	private static final String JSON_BOUNDEDCONTEXT_NAME = "name";
	private final JTextField contextName;
	private final JTextField packageName;
	private ComponentSwing component;
	private JSONObject jsonAttributes;
	private static final int CORNER = 12;
	private final List<TableCellTextFieldBinding> bindings;

	public enum BORDER_STYLE {
		THICK, NORMAL, NOTHING
	};

	private BORDER_STYLE borderStyle;
	private final Font originalContextNameFont;
	private final Font originalPackageNameFont;
	private final int CONTEXT_FONT_SIZE = 15;
	private final int PACKAGE_FONT_SIZE = 10;

	public BoundedContext() {
		super();
		contextName = new JTextField("BoundedContext1");
		contextName.setHorizontalAlignment(SwingConstants.CENTER);
		String originalFontName = contextName.getFont().getFontName();
		originalContextNameFont = new Font(originalFontName, Font.PLAIN, CONTEXT_FONT_SIZE);
		contextName.setFont(originalContextNameFont);
		contextName.setBorder(null);

		packageName = new JTextField("com.example.model.context1");
		packageName.setHorizontalAlignment(SwingConstants.CENTER);
		originalPackageNameFont = new Font(originalFontName, Font.PLAIN, PACKAGE_FONT_SIZE);
		packageName.setFont(originalPackageNameFont);
		packageName.setBorder(null);
		borderStyle = BORDER_STYLE.NOTHING;

		bindings = new LinkedList<TableCellTextFieldBinding>();
	}

	@Override
	public void init(Rectangle bounds, String panelAttributes, String additionalAttributes, Component component, DrawHandlerInterface handler, String uuid) {
		super.init(bounds, panelAttributes, additionalAttributes, component, handler, uuid);

		this.component = (ComponentSwing) component;
		this.component.add(contextName);
		this.component.add(packageName);

		try {
			jsonAttributes = new JSONObject(additionalAttributes);
			contextName.setText(jsonAttributes.getString(JSON_BOUNDEDCONTEXT_NAME));
			packageName.setText(jsonAttributes.getString(JSON_BOUNDEDCONTEXT_PACKAGE));
		} catch (Exception ex) {
			createDefaultJSON();
		}

		TableCellTextFieldBinding.createBinding(getTableModel(), contextName, "Context Name");
		TableCellTextFieldBinding.createBinding(getTableModel(), packageName, "Package Name");
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
		packageName.setFont(newPackageFont);
		packageName.setBounds(0, (int) (28 * getZoom()), boundsRect.width, (int) (15 * getZoom()));

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
		jsonAttributes.put(JSON_BOUNDEDCONTEXT_PACKAGE, "com.example.model.context1");
	}

	@Override
	public String getAdditionalAttributes() {
		jsonAttributes.put(JSON_BOUNDEDCONTEXT_NAME, contextName.getText());
		jsonAttributes.put(JSON_BOUNDEDCONTEXT_PACKAGE, packageName.getText());
		return jsonAttributes.toString(1);
	}

	@Override
	public void dragEnd() {
		checkFieldCompositesInsideBoundedContext();

		validateNames();
		borderStyle = BORDER_STYLE.NOTHING;
		updateModelFromText();
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

	public boolean validateNames() {
		DrawPanel drawPanel = CurrentDiagram.getInstance().getDiagramHandler().getDrawPanel();
		HashMap<String, FieldComposite> boundedContextNames = new HashMap<String, FieldComposite>();
		for (NewGridElement gridElement : drawPanel.getBoundedContextChildren(this)) {
			if (gridElement instanceof FieldComposite) {
				FieldComposite fieldComposite = (FieldComposite) gridElement;
				FieldComposite previous = boundedContextNames.put(fieldComposite.getName(), fieldComposite);
				fieldComposite.setNameValidity(previous);
			}
		}
		return true;
	}

	@Override
	public String getContextName() {
		return contextName.getText();
	}

	@Override
	public String getPackageName() {
		return packageName.getText();
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

}
