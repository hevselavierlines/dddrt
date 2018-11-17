package com.baselet.design.metal;

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;

public class VisibilityComboBox extends JComboBox<Item> {
	private static final long serialVersionUID = 1385934107040563411L;

	public VisibilityComboBox() {
		super();

		addItem(new Item("-", "private"));
		addItem(new Item("~", "package"));
		addItem(new Item("#", "protected"));
		addItem(new Item("+", "public"));
		setRenderer(new ItemRenderer());
		setUI(new StyledComboBoxUI());
	}

	public void setSelection(String selectionString) {
		int selectedIndex = -1;
		for (int i = 0; i < getItemCount() && selectedIndex < 0; i++) {
			Item item = getItemAt(i);
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
			Item item = (Item) value;
			setText(item.getId() + " " + item.getDescription());
			java.awt.Rectangle rect = this.getBounds();
			rect.width = 300;
			this.setBounds(rect);
		}
		if (index == -1) {
			Item item = (Item) value;
			if (item != null) {
				setText(item.getId());
			}
		}
		return this;
	}
}

class Item {

	private final String id;
	private final String description;

	public Item(String id, String description) {
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

class StyledComboBoxUI extends MetalComboBoxUI {
	@Override
	protected ComboPopup createPopup() {
		BasicComboPopup popup = new BasicComboPopup(comboBox) {
			@Override
			protected Rectangle computePopupBounds(int px, int py, int pw, int ph) {
				return super.computePopupBounds(
						px, py, Math.max(200, pw), ph);
			}
		};
		popup.getAccessibleContext().setAccessibleParent(comboBox);
		return popup;
	}
}
