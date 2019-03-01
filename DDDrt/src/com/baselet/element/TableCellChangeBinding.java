package com.baselet.element;

import java.util.LinkedList;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class TableCellChangeBinding implements TableModelListener {
	private final DefaultTableModel tableModel;
	private int rowNum;
	private String originalString;
	private final TablePropertyChangeListener changeListener;
	private final String rowKey;
	private static LinkedList<TableCellChangeBinding> bindings;

	public static void createBinding(DefaultTableModel tableModel, String rowKey, TablePropertyChangeListener changeListener) {
		if (bindings == null) {
			bindings = new LinkedList<TableCellChangeBinding>();
		}
		bindings.add(new TableCellChangeBinding(tableModel, rowKey, changeListener));
	}

	public static void clearBindings() {
		bindings.clear();
	}

	public TableCellChangeBinding(DefaultTableModel tableModel, String rowKey, TablePropertyChangeListener changeListener) {
		super();
		this.tableModel = tableModel;
		this.tableModel.addTableModelListener(this);

		this.rowKey = rowKey;
		rowNum = -1;

		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String key = tableModel.getValueAt(i, 0).toString();
			if (rowKey.equals(key)) {
				rowNum = i;
				originalString = tableModel.getValueAt(rowNum, 1).toString();
			}
		}
		this.changeListener = changeListener;
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (rowNum >= 0) {
			final String tableValue = tableModel.getValueAt(rowNum, 1).toString();
			if (!tableValue.equals(originalString)) {
				if (changeListener != null) {
					changeListener.propertyChange(rowKey, tableValue);
				}
				originalString = tableValue;
			}
		}
	}
}
