package com.baselet.gui.command;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.baselet.control.constants.Constants;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.DrawPanel;
import com.baselet.element.NewGridElement;
import com.baselet.element.interfaces.GridElement;
import com.baselet.element.relation.DDDRelation;

public class RemoveElement extends Command {

	protected final List<GridElement> _entities;
	private Point origin;
	private final boolean _zoom;
	private final List<GridElement> connectedEntities;

	public RemoveElement(GridElement e) {
		this(e, true);
	}

	public RemoveElement(GridElement e, boolean zoom) {
		_entities = new ArrayList<GridElement>();
		connectedEntities = new ArrayList<GridElement>();
		_entities.add(e);
		_zoom = zoom;
	}

	public RemoveElement(List<GridElement> v) {
		this(v, true);
	}

	public RemoveElement(List<GridElement> v, boolean zoom) {
		_entities = new ArrayList<GridElement>();
		_entities.addAll(v);
		connectedEntities = new ArrayList<GridElement>();
		_zoom = zoom;
	}

	@Override
	public void execute(DiagramHandler handler) {
		super.execute(handler);
		if (_entities.size() == 0) {
			return;
		}

		connectedEntities.clear();
		DrawPanel p = handler.getDrawPanel();
		for (GridElement e : _entities) {
			handler.getDrawPanel().removeElement(e);
			for (DDDRelation relatedRelation : p.getHelper(DDDRelation.class)) {
				if (e.equals(relatedRelation.getEndComposite()) ||
					e.equals(relatedRelation.getStartComposite())) {
					connectedEntities.add(relatedRelation);
					p.removeElement(relatedRelation);
				}
			}
		}

		origin = handler.getDrawPanel().getOriginAtDefaultZoom();
		if (_zoom) {
			DiagramHandler.zoomEntities(handler.getGridSize(), Constants.DEFAULTGRIDSIZE, _entities);
		}

		p.updatePanelAndScrollbars();
		p.repaint();
		p.getSelector().deselectAll();
	}

	@Override
	public void undo(DiagramHandler handler) {
		super.undo(handler);

		if (_zoom) {
			DiagramHandler.zoomEntities(Constants.DEFAULTGRIDSIZE, handler.getGridSize(), _entities);
		}

		int offsetX = origin.x - handler.getDrawPanel().getOriginAtDefaultZoom().x;
		int offsetY = origin.y - handler.getDrawPanel().getOriginAtDefaultZoom().y;

		offsetX = offsetX * handler.getGridSize() / Constants.DEFAULTGRIDSIZE;
		offsetY = offsetY * handler.getGridSize() / Constants.DEFAULTGRIDSIZE;

		for (GridElement e : _entities) {
			new AddElement(e,
					handler.realignToGrid(e.getRectangle().x + offsetX),
					handler.realignToGrid(e.getRectangle().y + offsetY), _zoom).execute(handler);
		}

		for (GridElement e : connectedEntities) {
			NewGridElement checkForTwo = handler.getDrawPanel().getElementById(e.getUUID());
			if (checkForTwo == null) {
				new AddElement(e,
						handler.realignToGrid(e.getRectangle().x + offsetX),
						handler.realignToGrid(e.getRectangle().y + offsetY), _zoom).execute(handler);
			}
		}

		handler.getDrawPanel().updatePanelAndScrollbars();
		handler.getDrawPanel().repaint();
	}
}
