package com.baselet.element.ddd.handler;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.baselet.control.basics.geom.Rectangle;
import com.baselet.diagram.MainDrawPanel;
import com.baselet.element.ddd.BoundedContext;

public class BoundedContextsHandler {
	private final List<BoundedContext> contexts;
	private final MainDrawPanel drawPanel;

	public BoundedContextsHandler(MainDrawPanel drawPanel) {
		contexts = new LinkedList<BoundedContext>();
		this.drawPanel = drawPanel;
	}

	public void addBoundedContext(BoundedContext boundedContext) {
		contexts.add(boundedContext);
		arrangeBoundedContexts();
	}

	public void removeBoundedContext(BoundedContext boundedContext) {
		int id = getIndex(boundedContext);
		if (id >= 0) {
			contexts.remove(id);
		}
	}

	private int getIndex(BoundedContext boundedContext) {
		int id = -1;
		for (int i = 0; i < contexts.size() && id < 0; i++) {
			if (contexts.get(i) == boundedContext) {
				id = i;
			}
		}
		return id;
	}

	public void arrangeBoundedContexts() {
		Collections.sort(contexts, new Comparator<BoundedContext>() {

			@Override
			public int compare(BoundedContext o1, BoundedContext o2) {
				return o1.getRectangle().x - o2.getRectangle().x;
			}
		});
		int widthOffset = 0;
		for (int i = 0; i < contexts.size(); i++) {
			BoundedContext bc = contexts.get(i);
			int width = bc.getRectangle().width;
			int height = bc.getRectangle().height;
			int diffX = widthOffset - bc.getComponent().getBoundsRect().x;
			int diffY = 0 - bc.getComponent().getBoundsRect().y;
			bc.getComponent().setBoundsRect(new Rectangle(widthOffset, 0, width, height));
			widthOffset += width;
			bc.updateModelFromText();
			bc.updateElementsInside(diffX, diffY);
		}
		drawPanel.repaint();
	}

	public void update(BoundedContext boundedContext) {
		Rectangle diffRectangle = boundedContext.getDiffRectangle();
		if (diffRectangle != null) {
			if (diffRectangle.x > 0 && diffRectangle.width < 0 || diffRectangle.x < 0 && diffRectangle.width > 0) {
				int index = getIndex(boundedContext);
				if (index > 0) {
					BoundedContext leftIndex = contexts.get(index - 1);
					leftIndex.setRectangleDifference(0, 0, diffRectangle.x, 0, true, null, false, false);
				}
			}
		}
		arrangeBoundedContexts();
	}
}
