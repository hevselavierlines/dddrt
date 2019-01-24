package com.baselet.element;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;

public class PropertyCellEditor extends AbstractCellEditor implements TableCellEditor, DocumentListener, TableModelListener {

	private static final long serialVersionUID = -8162110973029862973L;
	private TableCellEditor editor;

	private JTextField textField;
	private JTable table;
	private int row, column;
	private boolean updating;

	@Override
	public Object getCellEditorValue() {
		if (editor != null) {
			return editor.getCellEditorValue();
		}

		return null;
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		updateCell();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		updateCell();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		updateCell();
	}

	public void updateCell() {
		if (!updating) {
			table.getModel().setValueAt(textField.getText(), row, column);
		}
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		// String key = (String) table.getValueAt(row, 0);
		textField = new JTextField();
		this.table = table;
		textField.getDocument().addDocumentListener(this);
		editor = new DefaultCellEditor(textField);
		this.row = row;
		this.column = column;
		table.getModel().addTableModelListener(this);
		// if ("DATABASE NAME".equalsIgnoreCase(key)) {
		// editor = new DefaultCellEditor(new JComboBox<String>());
		// }
		// else {
		// editor = new DefaultCellEditor(new JTextField());
		// }

		return editor.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		updating = true;
		String value = table.getModel().getValueAt(row, column).toString();
		String editorValue = (String) editor.getCellEditorValue();
		if (!value.equals(editorValue)) {
			if (textField != null) {
				textField.setText(value);
			}
		}
		updating = false;
	}
}
