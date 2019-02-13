package com.baselet.design.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxButton;

import sun.swing.StringUIClientPropertyKey;

public class DDDComboBoxUI extends BasicComboBoxUI {
	protected int minWidth;
	protected int minHeight;

	@Override
	protected ComboPopup createPopup() {
		BasicComboPopup popup = new BasicComboPopup(comboBox) {
			private static final long serialVersionUID = 4226010583898566235L;

			@Override
			protected Rectangle computePopupBounds(int px, int py, int pw, int ph) {
				return super.computePopupBounds(
						px, py, Math.max(minWidth, pw), Math.max(minHeight, ph));
			}
		};
		popup.getAccessibleContext().setAccessibleParent(comboBox);
		return popup;
	}

	public void setPopupMinimumSize(int width, int height) {
		minWidth = width;
		minHeight = height;
	}

	public static ComponentUI createUI(JComponent c) {
		return new DDDComboBoxUI();
	}

	/**
	 * If necessary paints the currently selected item.
	 *
	 * @param g Graphics to paint to
	 * @param bounds Region to paint current value to
	 * @param hasFocus whether or not the JComboBox has focus
	 * @throws NullPointerException if any of the arguments are null.
	 * @since 1.5
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void paintCurrentValue(Graphics g, Rectangle bounds,
			boolean hasFocus) {
		@SuppressWarnings("rawtypes")
		ListCellRenderer renderer = comboBox.getRenderer();
		if (comboBox.getSelectedIndex() != -1) {
			Component comp;
			if (hasFocus && !isPopupVisible(comboBox)) {
				comp = renderer.getListCellRendererComponent(listBox,
						comboBox.getSelectedItem(), -1, true, false);
			}
			else {
				comp = renderer.getListCellRendererComponent(listBox,
						comboBox.getSelectedItem(), -1, false, false);
				Color bg = UIManager.getColor("ComboBox.disabledForeground");
				comp.setBackground(bg);
			}
			comp.setFont(comboBox.getFont());
			if (hasFocus && !isPopupVisible(comboBox)) {
				comp.setForeground(comboBox.getForeground());
				comp.setBackground(comboBox.getBackground());
			}
			else if (comboBox.isEnabled()) {
				comp.setForeground(comboBox.getForeground());
				comp.setBackground(comboBox.getBackground());
			}
			else {
				Color fg = UIManager.getColor("ComboBox.disabledForeground");
				comp.setForeground(fg);
				Color bg = UIManager.getColor("ComboBox.disabledBackground");
				comp.setBackground(bg);
			}
			currentValuePane.paintComponent(g, comp, comboBox, bounds.x, bounds.y,
					bounds.width, bounds.height);
		}
	}

	/**
	 * Returns the baseline.
	 *
	 * @throws NullPointerException {@inheritDoc}
	 * @throws IllegalArgumentException {@inheritDoc}
	 * @see javax.swing.JComponent#getBaseline(int, int)
	 * @since 1.6
	 */
	@Override
	public int getBaseline(JComponent c, int width, int height) {
		int baseline;
		if (true && height >= 4) {
			height -= 4;
			baseline = super.getBaseline(c, width, height);
			if (baseline >= 0) {
				baseline += 2;
			}
		}
		else {
			baseline = super.getBaseline(c, width, height);
		}
		return baseline;
	}

	@Override
	protected ComboBoxEditor createEditor() {
		return new DDDComboBoxEditor();
	}

	@Override
	protected JButton createArrowButton() {
		boolean iconOnly = true;
		final JButton button = new MetalComboBoxButton(comboBox,
				new DDDComboBoxIcon(),
				iconOnly,
				currentValuePane,
				listBox);
		button.setMargin(new Insets(0, 1, 1, 3));
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setContentAreaFilled(false);

		// Disabled rollover effect.
		button.putClientProperty(new StringUIClientPropertyKey("NoButtonRollover"),
				Boolean.TRUE);

		updateButtonForOcean(button);
		return button;
	}

	/**
	 * Resets the necessary state on the ComboBoxButton for ocean.
	 */
	private void updateButtonForOcean(JButton button) {
		button.setFocusPainted(comboBox.isEditable());
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new MetalComboBoxLayoutManager();
	}

	/**
	 * This class should be treated as a &quot;protected&quot; inner class.
	 * Instantiate it only within subclasses of {@code MetalComboBoxUI}.
	 */
	public class MetalComboBoxLayoutManager extends BasicComboBoxUI.ComboBoxLayoutManager {
		@Override
		public void layoutContainer(Container parent) {
			layoutComboBox();
		}
	}

	public void layoutComboBox() {
		if (arrowButton != null) {
			Insets insets = comboBox.getInsets();
			int buttonWidth = arrowButton.getMinimumSize().width;
			arrowButton.setBounds(comboBox.getWidth() - insets.right - buttonWidth,
					insets.top, buttonWidth,
					comboBox.getHeight() - insets.top - insets.bottom);

		}

		if (editor != null && true) {
			Rectangle cvb = rectangleForCurrentValue();
			editor.setBounds(cvb);
		}
	}

	/**
	 * As of Java 2 platform v1.4 this method is no
	 * longer used.
	 *
	 * @deprecated As of Java 2 platform v1.4.
	 */
	@Deprecated
	protected void removeListeners() {
		if (propertyChangeListener != null) {
			comboBox.removePropertyChangeListener(propertyChangeListener);
		}
	}

	// These two methods were overloaded and made public. This was probably a
	// mistake in the implementation. The functionality that they used to
	// provide is no longer necessary and should be removed. However,
	// removing them will create an uncompatible API change.

	@Override
	public void configureEditor() {
		super.configureEditor();
	}

	@Override
	public void unconfigureEditor() {
		super.unconfigureEditor();
	}

	@Override
	public Dimension getMinimumSize(JComponent c) {
		if (!isMinimumSizeDirty) {
			return new Dimension(cachedMinimumSize);
		}

		Dimension size = null;

		if (!comboBox.isEditable() &&
			arrowButton != null) {
			Insets buttonInsets = arrowButton.getInsets();
			Insets insets = comboBox.getInsets();

			size = getDisplaySize();
			size.width += insets.left + insets.right;
			size.width += buttonInsets.right;
			size.width += arrowButton.getMinimumSize().width;
			size.height += insets.top + insets.bottom;
			size.height += buttonInsets.top + buttonInsets.bottom;
		}
		else if (comboBox.isEditable() &&
					arrowButton != null &&
					editor != null) {
			size = super.getMinimumSize(c);
			Insets margin = arrowButton.getMargin();
			size.height += margin.top + margin.bottom;
			size.width += margin.left + margin.right;
		}
		else {
			size = super.getMinimumSize(c);
		}

		cachedMinimumSize.setSize(size.width, size.height);
		isMinimumSizeDirty = false;

		return new Dimension(cachedMinimumSize);
	}
}
