package com.baselet.element.interfaces;

import com.baselet.control.basics.geom.Rectangle;
import com.baselet.diagram.DrawPanel;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.gui.command.Controller;

public interface Component {
	void setBoundsRect(Rectangle rect);

	Rectangle getBoundsRect();

	void repaintComponent();

	DrawHandler getDrawHandler();

	DrawHandler getMetaDrawHandler();

	void afterModelUpdate();

	void translateForExport();

	void addComponent(java.awt.Component component);

	java.awt.Component[] getAllComponents();

	public DrawPanel getDrawPanel();

	Controller getController();
}
