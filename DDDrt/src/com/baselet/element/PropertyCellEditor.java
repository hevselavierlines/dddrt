package com.baselet.element;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;

import com.baselet.diagram.CurrentDiagram;
import com.baselet.gui.command.TableChangeCommand;

public class PropertyCellEditor extends AbstractCellEditor implements TableCellEditor, DocumentListener, TableModelListener, KeyListener, ItemListener {

	private static final long serialVersionUID = -8162110973029862973L;
	private TableCellEditor editor;

	private JTextField textField;
	private JComboBox<String> comboBox;
	private JCheckBox checkBox;
	private JTextArea textArea;
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
			CurrentDiagram.getInstance().getDiagramHandler().getController().executeCommand(new TableChangeCommand(table, textField, row, column) {});
		}
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		String key = (String) table.getValueAt(row, 0);
		if ("Type".equalsIgnoreCase(key)) {
			comboBox = new JComboBox<String>();
			comboBox.addItem("Entity");
			comboBox.addItem("Value Object");
			comboBox.addItem("Aggregate");
			comboBox.addItemListener(this);
			editor = new DefaultCellEditor(comboBox);
		}
		else if ("Notes".equalsIgnoreCase(key)) {
			textField = new JTextField();
			textField.getDocument().addDocumentListener(this);
			textField.addKeyListener(this);
			editor = new DefaultCellEditor(textField);
		}
		else if ("Primary Key".equals(key)) {
			checkBox = new JCheckBox();
			checkBox.addItemListener(this);
			editor = new DefaultCellEditor(checkBox);
		}
		else {
			textField = new JTextField();
			textField.getDocument().addDocumentListener(this);
			textField.addKeyListener(this);
			editor = new DefaultCellEditor(textField);
		}
		this.table = table;

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
		Object editorValueObj = editor.getCellEditorValue();
		if (editorValueObj instanceof String) {
			String editorValue = (String) editorValueObj;
			if (!value.equals(editorValue)) {
				if (textField != null) {
					textField.setText(value);
				}
			}
		}
		else if (editorValueObj instanceof Boolean) {
			boolean editorValue = (Boolean) editorValueObj;
			if (checkBox != null) {
				checkBox.setSelected(editorValue);
			}
		}
		updating = false;

	}

	@Override
	public void keyTyped(KeyEvent arg0) {}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			cancelCellEditing();
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (!updating) {
			if (arg0.getSource() instanceof JCheckBox) {
				table.getModel().setValueAt(checkBox.isSelected() ? "true" : "false", row, column);
			}
			else if (arg0.getSource() instanceof JComboBox<?>) {
				table.getModel().setValueAt(comboBox.getSelectedItem(), row, column);
			}
		}
	}
}
