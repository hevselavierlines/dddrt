package com.baselet.design.metal;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;

/**
 * The default editor for Metal editable combo boxes
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans&trade;
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @author Steve Wilson
 */
public class DDDComboBoxEditor extends BasicComboBoxEditor {

	public DDDComboBoxEditor() {
		super();
		// editor.removeFocusListener(this);
		editor = new JTextField("", 9) {
			private static final long serialVersionUID = -2539419942043923354L;

			// workaround for 4530952
			@Override
			public void setText(String s) {
				if (getText().equals(s)) {
					return;
				}
				super.setText(s);
			}
		};

		editor.setBorder(null);
		editor.setBackground(new Color(0, 0, 0, 0));
		editor.setOpaque(false);
	}
}
