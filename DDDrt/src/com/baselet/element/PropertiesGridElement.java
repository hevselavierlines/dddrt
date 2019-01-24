package com.baselet.element;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

public abstract class PropertiesGridElement extends NewGridElement {

	private final DefaultTableModel tableModel;

	public PropertiesGridElement() {
		super();
		tableModel = new DefaultTableModel(0, 2);
		tableModel.setColumnIdentifiers(new String[] { "Property Name", "Property Value" });
	}

	public List<String> getAllKeys() {
		List<String> ret = new LinkedList<String>();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			ret.add((String) tableModel.getValueAt(i, 0));
		}
		return ret;
	}

	public String getTableProperty(String columnName) {
		String value = null;
		for (int i = 0; i < tableModel.getRowCount() && value == null; i++) {
			if (columnName.equals(tableModel.getValueAt(i, 0))) {
				value = (String) tableModel.getValueAt(i, 1);
			}
		}

		return value;
	}

	public Set<Entry<String, String>> getAllProperties() {
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			map.put((String) tableModel.getValueAt(i, 0),
					(String) tableModel.getValueAt(i, 1));
		}
		return map.entrySet();
	}

	@Override
	public boolean useTableAttributes() {
		return true;
	}

	public void addProperty(String key, String value) {
		tableModel.addRow(new String[] { key, value });
	}

	@Override
	public String getPanelAttributes() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			stringBuilder
					.append(tableModel.getValueAt(i, 0))
					.append("\\=\\")
					.append(tableModel.getValueAt(i, 1))
					.append('\n');
		}

		return stringBuilder.toString();
	}

	@Override
	public void setPanelAttributes(String panelAttributes) {
		if (!panelAttributes.startsWith("!!DNC!!")) {
			for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
				tableModel.removeRow(i);
			}

			this.panelAttributes = new LinkedList<String>();
			this.panelAttributes.add("!!DNC!!");
			for (String line : panelAttributes.split("\n")) {
				String[] keyValue = line.split("\\\\=\\\\");
				if (keyValue.length == 2) {
					addProperty(keyValue[0], keyValue[1]);
				}
			}
		}
	}

	@Override
	public void setPanelAttributesHelper(String panelAttributes) {
		setPanelAttributes(panelAttributes);
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}

}
