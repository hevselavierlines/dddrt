package com.baselet.gui.command;

import java.util.List;

import com.baselet.control.enums.ElementId;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.DrawPanel;
import com.baselet.element.ElementFactorySwing;
import com.baselet.element.ddd.FieldComposite;
import com.baselet.element.relation.DDDRelation;

public class FieldCompositeTypeChangeCommand extends Command {
	private final FieldComposite originalFieldComposite;
	private FieldComposite newFieldComposite;
	private final ElementId newElementType;

	public FieldCompositeTypeChangeCommand(FieldComposite fieldComposite, ElementId newId) {
		originalFieldComposite = fieldComposite;
		newElementType = newId;
	}

	@Override
	public void execute(DiagramHandler handler) {
		changeType(handler, originalFieldComposite, newElementType);
	}

	private void changeType(DiagramHandler handler, FieldComposite originalFieldComposite, ElementId newElementType) {
		DrawPanel drawPanel = handler.getDrawPanel();
		newFieldComposite = (FieldComposite) ElementFactorySwing
				.create(
						newElementType,
						originalFieldComposite.getRectangle(),
						originalFieldComposite.getPanelAttributes(),
						originalFieldComposite.getAdditionalAttributes(),
						handler, originalFieldComposite.getUUID());
		List<DDDRelation> relations = drawPanel.getRelationsOfFieldComposite(originalFieldComposite);
		for (DDDRelation relation : relations) {
			relation.changeFieldComposite(originalFieldComposite, newFieldComposite);
		}
		drawPanel.addElement(newFieldComposite);
		drawPanel.removeElement(originalFieldComposite);
		drawPanel.getSelector().deselectAll();
		drawPanel.getSelector().select(newFieldComposite);
		newFieldComposite.updateBoundedContext(originalFieldComposite.getBoundedContext());
	}

	@Override
	public void undo(DiagramHandler handler) {
		DrawPanel drawPanel = handler.getDrawPanel();
		drawPanel.removeElement(newFieldComposite);
		drawPanel.addElement(originalFieldComposite);
		originalFieldComposite.updateTypeOnTable();

		drawPanel.getSelector().deselectAll();
		drawPanel.getSelector().select(originalFieldComposite);

		List<DDDRelation> relations = drawPanel.getRelationsOfFieldComposite(newFieldComposite);
		for (DDDRelation relation : relations) {
			relation.changeFieldComposite(newFieldComposite, originalFieldComposite);
		}
	}

	@Override
	public void redo(DiagramHandler handler) {
		DrawPanel drawPanel = handler.getDrawPanel();
		drawPanel.addElement(newFieldComposite);
		drawPanel.removeElement(originalFieldComposite);

		drawPanel.getSelector().deselectAll();
		drawPanel.getSelector().select(newFieldComposite);

		List<DDDRelation> relations = drawPanel.getRelationsOfFieldComposite(originalFieldComposite);
		for (DDDRelation relation : relations) {
			relation.changeFieldComposite(originalFieldComposite, newFieldComposite);
		}
	}

}
