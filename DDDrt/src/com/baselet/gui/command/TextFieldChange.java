package com.baselet.gui.command;

import javax.swing.JTextField;

import com.baselet.diagram.DiagramHandler;

public class TextFieldChange extends Command {
	private final JTextField textField;
	private final String startText;
	private final String endText;

	public TextFieldChange(JTextField textField, String startText) {
		this.textField = textField;
		this.startText = startText;
		endText = textField.getText();
	}

	@Override
	public void undo(DiagramHandler handler) {
		textField.setText(startText);
	}

	@Override
	public void redo(DiagramHandler handler) {
		textField.setText(endText);
	}

	@Override
	public void execute(DiagramHandler handler) {}
}
