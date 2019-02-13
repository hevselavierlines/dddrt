package com.baselet.design.metal;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class VisibilityComboBox extends MetalComboBox<VisibilityItem> {
	private static final long serialVersionUID = 1385934107040563411L;

	public VisibilityComboBox() {
		super();

		addItem(new VisibilityItem("-", "private"));
		addItem(new VisibilityItem("~", "package"));
		addItem(new VisibilityItem("#", "protected"));
		addItem(new VisibilityItem("+", "public"));
		setRenderer(new ItemRenderer());
		setPopupMinimumSize(100, 0);
	}

	public void setSelection(String selectionString) {
		int selectedIndex = -1;
		for (int i = 0; i < getItemCount() && selectedIndex < 0; i++) {
			VisibilityItem item = getItemAt(i);
			if (item.getId().equals(selectionString)) {
				selectedIndex = i;
			}
		}

		if (selectedIndex >= 0) {
			setSelectedIndex(selectedIndex);
		}
	}
}

class ItemRenderer extends BasicComboBoxRenderer {
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
		if (value != null) {
			VisibilityItem item = (VisibilityItem) value;
			setText(item.getId() + " " + item.getDescription());
		}
		if (index == -1) {
			VisibilityItem item = (VisibilityItem) value;
			if (item != null) {
				setText(item.getId());
			}
		}
		return this;
	}
}

class VisibilityItem {

	private final String id;
	private final String description;

	public VisibilityItem(String id, String description) {
		this.id = id;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return id;
	}
}
