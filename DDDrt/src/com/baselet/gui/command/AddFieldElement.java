package com.baselet.gui.command;

import javax.swing.JLayeredPane;

import com.baselet.diagram.DiagramHandler;
import com.baselet.element.ddd.CollapsiblePanel;
import com.baselet.element.ddd.FieldComposite;

public class AddFieldElement extends Command {

	private final JLayeredPane fieldProperty;
	private final CollapsiblePanel propertiesPane;
	private final FieldComposite fieldComposite;

	public AddFieldElement(JLayeredPane fieldProperty, CollapsiblePanel propertiesPane, FieldComposite fieldComposite) {
		super();
		this.fieldProperty = fieldProperty;
		this.propertiesPane = propertiesPane;
		this.fieldComposite = fieldComposite;
	}

	@Override
	public void execute(DiagramHandler handler) {
		propertiesPane.add(fieldProperty);
		fieldComposite.updateModelFromText();
	}

	@Override
	public void undo(DiagramHandler handler) {
		propertiesPane.remove(fieldProperty);
		fieldComposite.updateModelFromText();
	}

	@Override
	public void redo(DiagramHandler handler) {
		propertiesPane.add(fieldProperty);
		fieldComposite.updateModelFromText();
	}

}
