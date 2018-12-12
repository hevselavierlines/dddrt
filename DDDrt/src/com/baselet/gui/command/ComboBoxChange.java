package com.baselet.gui.command;

import javax.swing.JComboBox;

import com.baselet.diagram.DiagramHandler;

public class ComboBoxChange extends Command {
	private final Object originalSelection;
	private final Object newSelection;
	private final JComboBox<?> comboBox;

	public ComboBoxChange(JComboBox<?> comboBox, Object originalSelection) {
		this.comboBox = comboBox;
		newSelection = comboBox.getSelectedItem();
		this.originalSelection = originalSelection;
	}

	@Override
	public void execute(DiagramHandler handler) {}

	@Override
	public void undo(DiagramHandler handler) {
		comboBox.setSelectedItem(originalSelection);
	}

	@Override
	public void redo(DiagramHandler handler) {
		comboBox.setSelectedItem(newSelection);
	}

}
