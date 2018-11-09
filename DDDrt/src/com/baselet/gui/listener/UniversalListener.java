package com.baselet.gui.listener;

import java.awt.Polygon;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.baselet.control.basics.Converter;
import com.baselet.control.basics.geom.Point;
import com.baselet.control.basics.geom.Rectangle;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.DrawPanel;
import com.baselet.diagram.SelectorFrame;
import com.baselet.diagram.SelectorOld;
import com.baselet.element.interfaces.GridElement;
import com.baselet.gui.CurrentGui;
import com.baselet.gui.command.Controller;

public abstract class UniversalListener extends ComponentAdapter implements MouseListener, MouseMotionListener {

	protected DiagramHandler handler;
	protected DrawPanel diagram;
	public SelectorOld selector;
	protected Controller controller;

	private int _xOffset, _yOffset;
	private boolean disableElementMovement = true; // is true after mouseReleased until the next mousePressed AND if the lasso is active
	private int old_x_eff, old_y_eff;
	private int new_x_eff, new_y_eff;
	private java.awt.Point startPoint;

	protected UniversalListener(DiagramHandler handler) {
		this.handler = handler;
		diagram = handler.getDrawPanel();
		selector = diagram.getSelector();
		controller = handler.getController();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent me) {}

	@Override
	public void mousePressed(MouseEvent me) {
		disableElementMovement = false;
		CurrentGui.getInstance().getGui().requestFocus(); // to avoid beeing stuck in the propertyPanel
		Point off = getOffset(me);
		_xOffset = off.x;
		_yOffset = off.y;
		startPoint = new java.awt.Point(off.x, off.y);

		// everytime a mouse is pressed within a listener the gui gets the current diagram!
		CurrentDiagram.getInstance().setCurrentDiagramHandler(handler);

		if (CurrentDiagram.getInstance().getDiagramHandler() != null) {
			int factor = CurrentDiagram.getInstance().getDiagramHandler().getGridSize();
			CurrentGui.getInstance().getGui().setValueOfZoomDisplay(factor);
		}
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		disableElementMovement = true;
		if (selector.isSelectorFrameActive()) {
			SelectorFrame selframe = selector.getSelectorFrame();
			diagram.remove(selframe);
			selector.deselectAll();
			selector.multiSelect(Converter.convert(selframe.getBounds()));
			selector.setSelectorFrameActive(false);
			diagram.repaint();
		}

		diagram.updatePanelAndScrollbars();
		DrawPanel drawPanel = CurrentGui.getInstance().getGui().getCurrentDiagram();
		drawPanel.setSelectionPoint(null);
		drawPanel.repaint();

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent me) {

	}

	@Override
	public void mouseDragged(MouseEvent me) {
		// Get new mouse coordinates
		if (selector.isSelectorFrameActive()) {
			selector.getSelectorFrame().resizeTo(getOffset(me).getX(), getOffset(me).getY());
			disableElementMovement = true;
			return;
		}
		else if (disableElementMovement()) {
			return;
		}

		Point off = getOffset(me);
		int xNewOffset = off.x;
		int yNewOffset = off.y;
		int gridSize = CurrentDiagram.getInstance().getDiagramHandler().getGridSize();

		new_x_eff = gridSize * ((xNewOffset - gridSize / 2) / gridSize);
		new_y_eff = gridSize * ((yNewOffset - gridSize / 2) / gridSize);
		old_x_eff = gridSize * ((_xOffset - gridSize / 2) / gridSize);
		old_y_eff = gridSize * ((_yOffset - gridSize / 2) / gridSize);

		_xOffset = xNewOffset;
		_yOffset = yNewOffset;
	}

	// only call after mouseDragged
	protected final boolean disableElementMovement() {
		return disableElementMovement;
	}

	// only call after mouseDragged
	protected final Point getOldCoordinate() {
		return new Point(old_x_eff, old_y_eff);
	}

	// only call after mouseDragged
	protected final Point getOldCoordinateNotRounded() {
		return new Point(_xOffset, _yOffset);
	}

	// only call after mouseDragged
	protected final Point getNewCoordinate() {
		return new Point(new_x_eff, new_y_eff);
	}

	protected abstract Point getOffset(MouseEvent me);

	protected void selectDiagramElements() {
		if (disableElementMovement()) {
			return;
		}

		Point newp = getNewCoordinate();
		Point oldp = getOldCoordinate();

		int diffx = newp.x - oldp.x;
		int diffy = newp.y - oldp.y;
		Polygon polygon = new Polygon();
		polygon.addPoint(startPoint.x, startPoint.y);
		polygon.addPoint(newp.x, startPoint.y);
		polygon.addPoint(newp.x, newp.y);
		polygon.addPoint(startPoint.x, newp.y);
		polygon.addPoint(startPoint.x, startPoint.y);

		java.awt.Rectangle boundRect = polygon.getBoundingBox();
		Rectangle boundingRectangle = new Rectangle(boundRect.x, boundRect.y, boundRect.width, boundRect.height);

		diagram.setSelectionPoint(polygon);
		diagram.repaint();
		diagram.getSelector().deselectAll();
		for (GridElement e : diagram.getGridElements()) {
			if (e.isInRange(boundingRectangle)) {
				diagram.getSelector().select(e);
			}
		}

		// drawPanel.repaint();
		// if (diffx != 0 || diffy != 0) {
		// Vector<Command> moveCommands = new Vector<Command>();
		// for (GridElement e : diagram.getGridElements()) {
		// moveCommands.add(new Move(Collections.<Direction> emptySet(), e, diffx, diffy, oldp, false, false, true, StickableMap.EMPTY_MAP));
		// }
		//
		// controller.executeCommand(new Macro(moveCommands));
		// }
	}
}
