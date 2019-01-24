package com.baselet.gui.command;

import javax.swing.JTable;
import javax.swing.JTextField;

import com.baselet.diagram.DiagramHandler;

public class TableChangeCommand extends Command {
	private final JTable table;
	private final JTextField textField;
	private final int row, column;
	private String originalText;
	private String newText;

	public TableChangeCommand(JTable table, JTextField textField, int row, int column) {
		this.table = table;
		this.textField = textField;
		this.row = row;
		this.column = column;
	}

	@Override
	public void execute(DiagramHandler handler) {
		originalText = (String) table.getModel().getValueAt(row, column);
		newText = textField.getText();
		table.getModel().setValueAt(newText, row, column);
	}

	@Override
	public void undo(DiagramHandler handler) {
		table.getModel().setValueAt(originalText, row, column);
	}

	@Override
	public void redo(DiagramHandler handler) {
		table.getModel().setValueAt(newText, row, column);
	}

}
