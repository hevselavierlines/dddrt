package com.baselet.element.ddd;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLayeredPane;

import com.baselet.design.metal.ElementPanelBorder;
import com.baselet.element.ICollapseListener;

public class CollapsiblePanel extends JLayeredPane {

	private static final long serialVersionUID = 6477443706241182651L;
	private boolean collapsed;
	private final String title;
	private ElementPanelBorder border;
	private final List<ICollapseListener> collapseListeners;
	private Font fontComposite;
	public static final int DEFAULT_FONT_SIZE = 15;
	protected int DEFAULT_TITLE_HEIGHT = 20;
	protected int DEFAULT_BOTTOM_HEIGHT = 4;

	public CollapsiblePanel(String title) {
		this.title = title;
		border = new ElementPanelBorder(DEFAULT_TITLE_HEIGHT, 5);
		setBorder(border);
		addMouseListener(mouseListener);
		collapseListeners = new LinkedList<ICollapseListener>();
		collapsed = true;
		GridLayout gridLayout = new GridLayout(0, 1);
		gridLayout.setHgap(0);
		gridLayout.setVgap(0);
		setLayout(gridLayout);
		fontComposite = new Font(FieldComposite.FONT_NAME, Font.PLAIN, DEFAULT_FONT_SIZE);
		updateBorderTitle();
	}

	public void setTitleFont(Font font) {
		fontComposite = font;
		border.setTitleFont(font);
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
	private double currentZoomLevel = 1.0;

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

	public void zoomCollapse() {
		toggleVisibility(false);
	}

	protected void toggleVisibility(boolean visible) {
		for (Component c : getComponents()) {
			c.setVisible(visible);
		}
		collapsed = !visible;
		updateBorderTitle();
		callCollapseListeners();
	}

	public void updateBorderTitle() {
		String arrow = isInvisible() ? "\u25BC" : "\u25B2";
		border.setTitle(arrow + " " + title);
		repaint();
	}

	protected final boolean isInvisible() {
		return collapsed;
	}

	public int getTitleHeight() {
		return (int) (currentZoomLevel * DEFAULT_TITLE_HEIGHT);
	}

	public int getBottomHeight() {
		return (int) (currentZoomLevel * DEFAULT_BOTTOM_HEIGHT);
	}

	public int getFullHeight() {
		return getTitleHeight() + getBottomHeight();
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

	public void setZoomLevel(double zoomLevel) {
		currentZoomLevel = zoomLevel;
		int newFontSize = (int) (currentZoomLevel * DEFAULT_FONT_SIZE);
		fontComposite = fontComposite.deriveFont(Font.PLAIN, newFontSize);
		border = new ElementPanelBorder(border, getTitleHeight(), getBottomHeight());
		border.setTitleFont(fontComposite);
		setBorder(border);
	}
}