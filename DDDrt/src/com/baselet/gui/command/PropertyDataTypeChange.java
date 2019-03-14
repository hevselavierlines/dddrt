package com.baselet.gui.command;

import javax.swing.JComboBox;

import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.DrawPanel;
import com.baselet.element.ddd.FieldComposite;
import com.baselet.element.ddd.FieldProperty;
import com.baselet.element.relation.DDDRelation;

public class PropertyDataTypeChange extends Command {
	private final FieldProperty property;
	private final FieldComposite fieldComposite;
	private final DrawPanel drawPanel;
	private final DDDRelation relation;
	private DDDRelation newRelation;
	private final JComboBox<?> selector;
	private final Object oldValue;
	private Object newValue;
	private final boolean collection;

	public PropertyDataTypeChange(FieldProperty property, FieldComposite fieldComposite, DrawPanel drawPanel, DDDRelation relation, JComboBox<?> selector, Object oldValue, boolean collection) {
		this.property = property;
		this.drawPanel = drawPanel;
		this.relation = relation;
		this.fieldComposite = fieldComposite;
		this.selector = selector;
		this.oldValue = oldValue;
		this.collection = collection;
	}

	@Override
	public void execute(DiagramHandler handler) {
		if (fieldComposite != null) {
			if (relation != null) {
				drawPanel.removeRelation(relation);
			}
			newRelation = DDDRelation.createRelation(property, fieldComposite, collection);
			property.setRelation(newRelation);
			drawPanel.addRelation(newRelation);
		}
		else {
			if (relation != null) {
				drawPanel.removeRelation(relation);
				property.setRelation(null);
			}
		}
		drawPanel.repaint();
		newValue = selector.getSelectedItem();
	}

	@Override
	public void undo(DiagramHandler handler) {
		selector.setSelectedItem(oldValue);
		if (newRelation != null && relation == null) {
			drawPanel.removeRelation(newRelation);
		}
		else if (newRelation == null && relation != null) {
			drawPanel.addRelation(relation);
		}
		else if (newRelation != null && relation != null) {
			drawPanel.removeRelation(newRelation);
			drawPanel.addRelation(relation);
		}
		drawPanel.repaint();
	}

	@Override
	public void redo(DiagramHandler handler) {
		selector.setSelectedItem(newValue);
		if (newRelation != null && relation == null) {
			drawPanel.addRelation(newRelation);
		}
		else if (newRelation == null && relation != null) {
			drawPanel.removeRelation(relation);
		}
		else if (newRelation != null && relation != null) {
			drawPanel.removeRelation(relation);
			drawPanel.addRelation(newRelation);
		}
		drawPanel.repaint();
	}

}
