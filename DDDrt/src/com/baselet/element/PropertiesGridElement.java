package com.baselet.element;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

import com.baselet.control.enums.ElementId;
import com.baselet.element.ddd.AggregateComposite;
import com.baselet.element.ddd.EntityComposite;
import com.baselet.element.ddd.ValueObjectComposite;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.element.facet.Settings;

public class PropertiesGridElement extends NewGridElement {

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

	private int getRowByKey(String key) {
		int keyRow = -1;
		for (int i = 0; i < tableModel.getRowCount() && keyRow < 0; i++) {
			if (key.equals(tableModel.getValueAt(i, 0))) {
				keyRow = i;
			}
		}
		return keyRow;
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
		addProperty(key, value, true);
	}

	public void addProperty(String key, String value, boolean override) {
		if ("Type".equals(key)) {
			if (this instanceof AggregateComposite) {
				value = "Aggregate";
			}
			else if (this instanceof ValueObjectComposite) {
				value = "Value Object";
			}
			else if (this instanceof EntityComposite) {
				value = "Entity";
			}
		}
		int keyRow = getRowByKey(key);
		if (keyRow < 0) {
			tableModel.addRow(new String[] { key, value });
		}
		else {
			if (override) {
				tableModel.setValueAt(value, keyRow, 1);
			}
		}
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
					addProperty(keyValue[0], keyValue[1], true);
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

	@Override
	public ElementId getId() {
		return null;
	}

	@Override
	protected void drawCommonContent(PropertiesParserState state) {}

	@Override
	protected Settings createSettings() {
		return null;
	}

}
