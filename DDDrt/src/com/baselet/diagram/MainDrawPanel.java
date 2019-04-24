package com.baselet.diagram;

import com.baselet.control.basics.geom.Rectangle;
import com.baselet.control.enums.ElementId;
import com.baselet.element.ElementFactorySwing;
import com.baselet.element.ddd.BoundedContext;
import com.baselet.element.ddd.handler.BoundedContextsHandler;
import com.baselet.element.interfaces.GridElement;

public class MainDrawPanel extends DrawPanel {

	private final BoundedContextsHandler boundedContexts;

	public MainDrawPanel(DiagramHandler handler) {
		super(handler);

		boundedContexts = new BoundedContextsHandler(this);
	}

	public void updateBoundedContext(BoundedContext boundedContext) {
		boundedContexts.update(boundedContext);
	}

	@Override
	public void initEmpty() {
		BoundedContext mainBoundedContext = (BoundedContext) ElementFactorySwing.create(ElementId.DDDBoundedContext, new Rectangle(0, 0, 400, 800), "Context Name\\=\\Core\r\n" +
																																					"Modules\\=\\1\r\n" +
																																					"Module 0\\=\\model.core",
				"{\r\n" +
																																												" \"name\": \"Core\",\r\n" +
																																												" \"packages\": [\"model.core\"],\r\n" +
																																												" \"modules\": 1\r\n" +
																																												"}",
				handler);
		addElement(mainBoundedContext);

	}

	@Override
	public void addElement(GridElement gridElement) {
		super.addElement(gridElement);
		if (gridElement instanceof BoundedContext) {
			boundedContexts.addBoundedContext((BoundedContext) gridElement);
		}
	}

	@Override
	public void removeElement(GridElement gridElement) {
		super.removeElement(gridElement);
		if (gridElement instanceof BoundedContext) {
			boundedContexts.removeBoundedContext((BoundedContext) gridElement);
		}
	}

}
