package com.baselet.element;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.baselet.element.ddd.FieldProperty;

public class TableCellPrimaryKeyBinding implements TableModelListener {
	private final DefaultTableModel tableModel;
	private int rowNum;
	private final FieldProperty fieldProperty;
	private String originalValue;

	public TableCellPrimaryKeyBinding(DefaultTableModel tableModel, FieldProperty fieldProperty, String rowKey) {
		super();
		this.tableModel = tableModel;
		this.tableModel.addTableModelListener(this);

		this.fieldProperty = fieldProperty;
		rowNum = -1;

		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String key = tableModel.getValueAt(i, 0).toString();
			if (rowKey.equals(key)) {
				rowNum = i;
			}
		}

		if (rowNum >= 0) {
			originalValue = (String) tableModel.getValueAt(rowNum, 1);
		}
	}

	@Override
	public void tableChanged(TableModelEvent arg0) {
		Object tableValue = tableModel.getValueAt(rowNum, 1);
		String textValue = null;
		if (tableValue instanceof String) {
			textValue = (String) tableValue;
		}
		else if (tableValue instanceof Boolean) {
			Boolean booleanValue = (Boolean) tableValue;
			textValue = booleanValue.toString();
		}

		if (textValue != null && originalValue != null && !originalValue.equals(textValue)) {
			originalValue = textValue;
			boolean primaryKey = textValue.equals("true");
			fieldProperty.setIdProperty(primaryKey);
		}
	}
}
