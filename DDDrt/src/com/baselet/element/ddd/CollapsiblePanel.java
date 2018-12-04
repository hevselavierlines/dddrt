package com.baselet.element.ddd;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import javax.swing.border.TitledBorder;

import com.baselet.element.ICollapseListener;

public class CollapsiblePanel extends JLayeredPane {

	private static final long serialVersionUID = 6477443706241182651L;
	private boolean collapsed;
	private String title;
	TitledBorder border;
	private final List<ICollapseListener> collapseListeners;

	public CollapsiblePanel(String title) {
		this.title = title;
		border = BorderFactory.createTitledBorder(title);
		setBorder(border);
		addMouseListener(mouseListener);
		collapseListeners = new LinkedList<ICollapseListener>();
		collapsed = false;
	}

	MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			toggleVisibility();
		}
	};

	ComponentListener contentComponentListener = new ComponentAdapter() {
		@Override
		public void componentShown(ComponentEvent e) {
			updateBorderTitle();
		}

		@Override
		public void componentHidden(ComponentEvent e) {
			updateBorderTitle();
		}
	};

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		firePropertyChange("title", this.title, this.title = title);
	}

	@Override
	public Component add(Component comp) {
		comp.addComponentListener(contentComponentListener);
		Component r = super.add(comp);
		updateBorderTitle();
		return r;
	}

	@Override
	public Component add(String name, Component comp) {
		comp.addComponentListener(contentComponentListener);
		Component r = super.add(name, comp);
		updateBorderTitle();
		return r;
	}

	@Override
	public Component add(Component comp, int index) {
		comp.addComponentListener(contentComponentListener);
		Component r = super.add(comp, index);
		updateBorderTitle();
		return r;
	}

	@Override
	public void add(Component comp, Object constraints) {
		comp.addComponentListener(contentComponentListener);
		super.add(comp, constraints);
		updateBorderTitle();
	}

	@Override
	public void add(Component comp, Object constraints, int index) {
		comp.addComponentListener(contentComponentListener);
		super.add(comp, constraints, index);
		updateBorderTitle();
	}

	@Override
	public void remove(int index) {
		Component comp = getComponent(index);
		comp.removeComponentListener(contentComponentListener);
		super.remove(index);
	}

	@Override
	public void remove(Component comp) {
		comp.removeComponentListener(contentComponentListener);
		super.remove(comp);
	}

	@Override
	public void removeAll() {
		for (Component c : getComponents()) {
			c.removeComponentListener(contentComponentListener);
		}
		super.removeAll();
	}

	protected void toggleVisibility() {
		toggleVisibility(isInvisible());
	}

	protected void toggleVisibility(boolean visible) {
		for (Component c : getComponents()) {
			c.setVisible(visible);
		}
		collapsed = !visible;
		callCollapseListeners();
		updateBorderTitle();
	}

	public void updateBorderTitle() {
		String arrow = "";
		if (getComponentCount() > 0) {
			arrow = isInvisible() ? "▽" : "△";
		}
		border.setTitle(arrow + " " + title);
		repaint();
	}

	protected final boolean isInvisible() {
		return collapsed;
	}

	public int getTitleHeight() {
		return 30;
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public void addCollapseListener(ICollapseListener collapseListener) {
		collapseListeners.add(collapseListener);
	}

	public void callCollapseListeners() {
		for (ICollapseListener listener : collapseListeners) {
			listener.collapseStateChange(collapsed);
		}
	}

}