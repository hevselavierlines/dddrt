package com.baselet.element.ddd;

import java.awt.Color;

import com.baselet.control.basics.geom.Rectangle;
import com.baselet.control.enums.ElementId;
import com.baselet.element.interfaces.Component;
import com.baselet.element.interfaces.DrawHandlerInterface;

import tk.baumi.main.CompositeType;

public class AggregateComposite extends EntityComposite {
	@Override
	public ElementId getId() {
		return ElementId.DDDAggregate;
	}

	@Override
	public void init(Rectangle bounds, String panelAttributes, String additionalAttributes, Component component, DrawHandlerInterface handler, String uuid) {
		super.init(bounds, panelAttributes, additionalAttributes, component, handler, uuid);

		// this.component.setBackground(Color.pink);
	}

	@Override
	protected String getTitle() {
		return "<<Root Aggregate>>";
	}

	@Override
	protected FieldProperty createProperty() {
		return new EntityProperty();
	}

	@Override
	protected FieldMethod createMethod() {
		return new FieldMethod();
	}

	@Override
	protected Color getBackgroundColor() {
		return new Color(0xd3b47e);
	}

	@Override
	public CompositeType getType() {
		return CompositeType.Aggregate;
	}

}
