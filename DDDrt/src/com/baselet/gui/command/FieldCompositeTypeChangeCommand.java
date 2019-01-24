package com.baselet.gui.command;

import com.baselet.control.enums.ElementId;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.DrawPanel;
import com.baselet.element.ElementFactorySwing;
import com.baselet.element.ddd.FieldComposite;

public class FieldCompositeTypeChangeCommand extends Command {
	private final FieldComposite originalFieldComposite;
	private FieldComposite newFieldComposite;
	private final ElementId elementId;

	public FieldCompositeTypeChangeCommand(FieldComposite fieldComposite, ElementId newId) {
		originalFieldComposite = fieldComposite;
		elementId = newId;
	}

	@Override
	public void execute(DiagramHandler handler) {
		DrawPanel drawPanel = handler.getDrawPanel();
		newFieldComposite = (FieldComposite) ElementFactorySwing
				.create(
						elementId,
						originalFieldComposite.getRectangle(),
						originalFieldComposite.getPanelAttributes(),
						originalFieldComposite.getAdditionalAttributes(),
						handler, originalFieldComposite.getUUID());
		drawPanel.addElement(newFieldComposite);
		drawPanel.removeElement(originalFieldComposite);
		drawPanel.getSelector().deselectAll();
		drawPanel.getSelector().select(newFieldComposite);
	}

	@Override
	public void undo(DiagramHandler handler) {
		DrawPanel drawPanel = handler.getDrawPanel();
		drawPanel.removeElement(newFieldComposite);
		drawPanel.addElement(originalFieldComposite);
		drawPanel.getSelector().deselectAll();
		drawPanel.getSelector().select(originalFieldComposite);
	}

	@Override
	public void redo(DiagramHandler handler) {
		DrawPanel drawPanel = handler.getDrawPanel();
		drawPanel.removeElement(originalFieldComposite);
		drawPanel.addElement(newFieldComposite);
		drawPanel.getSelector().deselectAll();
		drawPanel.getSelector().select(newFieldComposite);
	}

}
