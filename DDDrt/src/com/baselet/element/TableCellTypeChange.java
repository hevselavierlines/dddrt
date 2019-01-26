package com.baselet.element;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class TableCellTypeChange implements TableModelListener {

	private final DefaultTableModel tableModel;
	private final String rowKey;
	private final FieldTypeChange typeChange;
	private String originalType;
	private int rowNum;
	private boolean changing;

	public void preventUpdate(String originalType) {
		changing = true;
		this.originalType = originalType;
	}

	public void stopPreventUpdate() {
		changing = false;
	}

	public TableCellTypeChange(DefaultTableModel tableModel, String rowKey, FieldTypeChange typeChange) {
		super();

		this.tableModel = tableModel;

		rowNum = -1;

		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String key = tableModel.getValueAt(i, 0).toString();
			if (rowKey.equals(key)) {
				rowNum = i;
			}
		}
		if (rowNum >= 0) {
			this.tableModel.addTableModelListener(this);
			originalType = (String) this.tableModel.getValueAt(rowNum, 1);
		}

		this.rowKey = rowKey;
		this.typeChange = typeChange;
	}

	@Override
	public void tableChanged(TableModelEvent arg0) {
		if (!changing) {
			String newType = (String) tableModel.getValueAt(rowNum, 1);
			if (typeChange != null && newType != null && !newType.equals(originalType)) {
				changing = true;
				originalType = newType;
				typeChange.typeChanged(newType);
				changing = false;
			}
		}
	}

}
