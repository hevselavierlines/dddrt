package com.baselet.element;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class TableCellTextFieldBinding implements DocumentListener, TableModelListener {
	private final DefaultTableModel tableModel;
	private final JTextField textField;
	private int rowNum;

	public TableCellTextFieldBinding(DefaultTableModel tableModel, JTextField textField, String rowKey) {
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
		if (rowNum >= 0) {
			String tableValue = tableModel.getValueAt(rowNum, 1).toString();
			String textValue = textField.getText();
			if (!tableValue.equals(textValue)) {
				tableModel.setValueAt(textValue, rowNum, 1);
			}
		}
	}

	@Override
	public void tableChanged(TableModelEvent arg0) {
		if (rowNum >= 0) {
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
	}

}
