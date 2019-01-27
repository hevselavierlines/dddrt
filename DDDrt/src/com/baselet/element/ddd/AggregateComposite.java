package com.baselet.element.ddd;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baselet.control.enums.ElementId;
import com.baselet.element.NewGridElement;

public class AggregateComposite extends EntityComposite {
	@Override
	public ElementId getId() {
		return ElementId.DDDAggregate;
	}

	public boolean isRootAggregates() {
		if (boundedContext != null) {
			List<AggregateComposite> aggregates = new LinkedList<AggregateComposite>();
			for (NewGridElement fieldComp : getComponent().getDrawPanel().getBoundedContextChildren(boundedContext)) {
				if (fieldComp instanceof AggregateComposite) {
					aggregates.add((AggregateComposite) fieldComp);
				}
			}
			if (aggregates.size() == 1) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	@Override
	protected String getTitle() {
		if (isRootAggregates()) {
			return "<<Root Aggregate>>";
		}
		else {
			return "<<Aggregate>>";
		}
	}

	@Override
	protected void createDefaultJSON() {
		jsonAttributes = new JSONObject();
		jProperties = new JSONArray();
		JSONObject property = new JSONObject();
		property.put("visibility", "-");
		property.put("type", "UUID");
		property.put("name", "unqueId");
		property.put("idproperty", true);
		jProperties.put(property);

		jMethods = new JSONArray();
		JSONObject method = new JSONObject();
		method.put("visibility", "+");
		method.put("type", "int");
		method.put("name", "testMethod");
		method.put("parameters", "(Object inputParam)");
		jMethods.put(method);

		JSONObject entities = new JSONObject();
		entities.put("name", "newAggregate");
		entities.put("properties", jProperties);
		entities.put("methods", jMethods);
		jsonAttributes.put("entities", entities);
	}

	@Override
	protected FieldProperty createProperty() {
		return new EntityProperty();
	}

	@Override
	protected FieldMethod createMethod() {
		return new FieldMethod();
	}
}
