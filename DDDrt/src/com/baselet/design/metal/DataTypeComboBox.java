package com.baselet.design.metal;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import com.baselet.element.ddd.FieldComposite;

public class DataTypeComboBox extends MetalComboBox<DataTypeItem> {
	public DataTypeComboBox() {
		super();

		setRenderer(new DataTypeItemRenderer());
		setPopupMinimumSize(250, 250);
	}

	public void setSelection(String selectionString) {
		int selectedIndex = -1;
		for (int i = 0; i < getItemCount() && selectedIndex < 0; i++) {
			DataTypeItem item = getItemAt(i);
			if (item.getId().equals(selectionString)) {
				selectedIndex = i;
			}
		}

		if (selectedIndex >= 0) {
			setSelectedIndex(selectedIndex);
		}
	}

	public void addItem(String item, FieldComposite ec, boolean collection) {
		super.addItem(new DataTypeItem(item, ec, collection));
	}

	public void addItem(String item, FieldComposite ec) {
		addItem(item, ec, false);
	}

	public void addItem(String item) {
		addItem(item, null, false);
	}

	public void addItem(String item, boolean collection) {
		addItem(item, null, collection);
	}

	public FieldComposite getSelection() {
		DataTypeItem item = getItemAt(getSelectedIndex());
		if (item != null) {
			return item.getDescription();
		}
		else {
			return null;
		}
	}
}

class DataTypeItemRenderer extends BasicComboBoxRenderer {
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
		if (value != null) {
			DataTypeItem item = (DataTypeItem) value;

			setText(item.toString());
		}
		if (index == -1) {
			DataTypeItem item = (DataTypeItem) value;
			if (item != null) {
				setText(item.toString());
			}
		}
		return this;
	}
}
