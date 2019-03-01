package com.baselet.element;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

public class TableCellTextFieldBinding implements DocumentListener, TableModelListener {
	private final DefaultTableModel tableModel;
	private final JTextComponent textField;
	private int rowNum;
	private boolean changeOccurred;

	public static TableCellTextFieldBinding createBinding(DefaultTableModel tableModel, JTextComponent textField, String rowKey) {
		return new TableCellTextFieldBinding(tableModel, textField, rowKey);
	}

	private TableCellTextFieldBinding(DefaultTableModel tableModel, JTextComponent textField, String rowKey) {
		super();
		this.tableModel = tableModel;
		this.textField = textField;

		this.textField.getDocument().addDocumentListener(this);
		this.tableModel.addTableModelListener(this);

		rowNum = -1;

		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String key = tableModel.getValueAt(i, 0).toString();
			if (rowKey.equals(key)) {
				rowNum = i;
			}
		}
		textFieldChanges();
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		textFieldChanges();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		textFieldChanges();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		textFieldChanges();
	}

	public void textFieldChanges() {
		if (!changeOccurred) {
			changeOccurred = true;
			if (rowNum >= 0 && rowNum <= tableModel.getRowCount()) {
				String tableValue = tableModel.getValueAt(rowNum, 1).toString();
				String textValue = textField.getText();
				if (!tableValue.equals(textValue)) {
					tableModel.setValueAt(textValue, rowNum, 1);
				}
			}
			changeOccurred = false;
		}
	}

	@Override
	public void tableChanged(TableModelEvent arg0) {
		if (!changeOccurred) {
			changeOccurred = true;
			if (rowNum >= 0 && rowNum < tableModel.getRowCount()) {
				final String tableValue = tableModel.getValueAt(rowNum, 1).toString();
				String textValue = textField.getText();
				if (!textValue.equals(tableValue)) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							textField.setText(tableValue);
						}
					});
				}
			}
			changeOccurred = false;
		}
	}

	public boolean isTextField(JTextField textField) {
		return this.textField == textField;
	}

	public void dispose() {
		tableModel.removeTableModelListener(this);
		textField.getDocument().removeDocumentListener(this);
	}
}
