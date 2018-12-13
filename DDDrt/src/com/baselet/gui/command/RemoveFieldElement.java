package com.baselet.gui.command;

import java.awt.Component;

import com.baselet.diagram.DiagramHandler;
import com.baselet.element.ddd.CollapsiblePanel;
import com.baselet.element.ddd.FieldComposite;

public class RemoveFieldElement extends Command {

	private final java.awt.Component component;
	private final CollapsiblePanel collapsiblePane;
	private final FieldComposite fieldComposite;

	public RemoveFieldElement(Component component, CollapsiblePanel collapsiblePane, FieldComposite fieldComposite) {
		super();
		this.component = component;
		this.collapsiblePane = collapsiblePane;
		this.fieldComposite = fieldComposite;
	}

	@Override
	public void execute(DiagramHandler handler) {
		collapsiblePane.remove(component);
		collapsiblePane.updateBorderTitle();
		fieldComposite.updateModelFromText();
	}

	@Override
	public void undo(DiagramHandler handler) {
		collapsiblePane.add(component);
		collapsiblePane.updateBorderTitle();
		fieldComposite.updateModelFromText();
	}

}
