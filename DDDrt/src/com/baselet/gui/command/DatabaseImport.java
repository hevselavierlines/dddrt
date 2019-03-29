package com.baselet.gui.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.DrawPanel;
import com.baselet.element.ddd.FieldComposite;
import com.baselet.element.ddd.FieldProperty;
import com.baselet.element.interfaces.GridElement;
import com.baselet.element.relation.DDDRelation;
import com.baselet.generator.sorting.AlphabetLayout;
import com.baselet.generator.sorting.SortableElement;

import at.mic.dddrt.db.model.ColumnRelation;
import at.mic.dddrt.db.model.TableColumn;

public class DatabaseImport extends Command {
	private final List<FieldComposite> elements;
	private final List<DDDRelation> relations;
	private final List<ColumnRelation> allRelations;

	public DatabaseImport(List<FieldComposite> elements, List<ColumnRelation> allRelations) {
		this.elements = elements;
		this.allRelations = allRelations;
		relations = new LinkedList<DDDRelation>();
	}

	@Override
	public void execute(DiagramHandler handler) {
		List<SortableElement> sortableElements = new ArrayList<SortableElement>(elements.size());
		for (FieldComposite element : elements) {
			sortableElements.add(new SortableElement(element, element.getTableProperty("Database Name")));
		}
		new AlphabetLayout().layout(sortableElements);
		addElementsToDiagram(elements, handler);

		for (ColumnRelation relation : allRelations) {
			FieldComposite startTable = null, endTable = null;
			for (SortableElement sortElem : sortableElements) {
				if (relation.getOriginalTable().equals(sortElem.getName())) {
					startTable = (FieldComposite) sortElem.getElement();
				}
				if (relation.getReferencingTable().equals(sortElem.getName())) {
					endTable = (FieldComposite) sortElem.getElement();
				}
			}
			if (startTable != null && endTable != null) {
				FieldProperty startProperty = startTable.getPropertyByName(TableColumn.convertToCamelCase(relation.getOriginalColumn()));
				if (startProperty != null) {
					DDDRelation dddRelation = DDDRelation.createRelation(startProperty, endTable, false);
					startProperty.setRelation(dddRelation);
					addRelationToDiagram(dddRelation, handler);
					startProperty.setPropertyType(endTable.getName());
				}
			}
		}
	}

	private void addElementsToDiagram(List<FieldComposite> elements, DiagramHandler handler) {
		for (GridElement e : elements) {
			new AddElement(e,
					handler.realignToGrid(e.getRectangle().x),
					handler.realignToGrid(e.getRectangle().y), false).execute(handler);
		}
	}

	private void addRelationToDiagram(DDDRelation relation, DiagramHandler handler) {
		// new PropertyDataTypeChange(relation.getS, fieldComposite, drawPanel, relation, selector, oldValue)
		relations.add(relation);
		handler.getDrawPanel().addRelation(relation);
	}

	@Override
	public void undo(DiagramHandler handler) {
		DrawPanel drawPanel = handler.getDrawPanel();
		for (FieldComposite fieldComposite : elements) {
			drawPanel.removeElement(fieldComposite);
		}
		for (DDDRelation relation : relations) {
			drawPanel.removeRelation(relation);
		}
		drawPanel.repaint();
	}

	@Override
	public void redo(DiagramHandler handler) {
		DrawPanel drawPanel = handler.getDrawPanel();
		for (FieldComposite fieldComposite : elements) {
			drawPanel.addElement(fieldComposite);
		}
		for (DDDRelation relation : relations) {
			drawPanel.addRelation(relation);
		}
		drawPanel.repaint();
	}

}
