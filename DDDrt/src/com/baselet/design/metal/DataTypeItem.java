package com.baselet.design.metal;

import com.baselet.element.ddd.FieldComposite;

public class DataTypeItem {

	private final String typeName;
	private final FieldComposite typeField;
	private final boolean collection;

	public DataTypeItem(String id, FieldComposite typeField, boolean collection) {
		typeName = id;
		this.typeField = typeField;
		this.collection = collection;
	}

	public DataTypeItem(String id, FieldComposite typeField) {
		this(id, typeField, false);
	}

	public String getId() {
		return typeName;
	}

	public FieldComposite getDescription() {
		return typeField;
	}

	@Override
	public String toString() {
		if (collection) {
			return "List<" + typeName + ">";
		}
		else {
			return typeName;
		}
	}

	public boolean isCollection() {
		return collection;
	}
}