package com.baselet.gui.command;

import java.util.List;

import com.baselet.diagram.DiagramHandler;
import com.baselet.element.ElementFactorySwing;
import com.baselet.element.interfaces.GridElement;

public class Duplicate extends Command {

	private List<GridElement> entities;

	@Override
	public void execute(DiagramHandler handler) {
		super.execute(handler);

		entities = ElementFactorySwing.createCopy(handler.getDrawPanel().getSelector().getSelectedElements());

		int offsetX = 30;
		int offsetY = 30;
		// if no element is selected, the whole diagram is copied into the clipboard
		if (!entities.isEmpty()) {
			for (GridElement e : entities) {
				e.setLocationDifference(offsetX, offsetY);
				handler.getDrawPanel().addElement(e);
			}
		}
	}

	@Override
	public void undo(DiagramHandler handler) {
		super.undo(handler);
		if (entities != null && !entities.isEmpty()) {
			for (GridElement e : entities) {
				handler.getDrawPanel().removeElement(e);
			}
		}
	}

	@Override
	public void redo(DiagramHandler handler) {
		if (entities != null && !entities.isEmpty()) {
			for (GridElement e : entities) {
				handler.getDrawPanel().addElement(e);
			}
		}
	}

}
