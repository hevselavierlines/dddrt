package com.baselet.gui.pane;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.baselet.control.basics.Converter;
import com.baselet.control.config.DerivedConfig;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.diagram.draw.helper.ColorOwn;
import com.baselet.element.PropertiesGridElement;
import com.baselet.element.PropertyCellEditor;
import com.baselet.element.ddd.FieldComposite;
import com.baselet.element.ddd.FieldProperty;
import com.baselet.element.interfaces.GridElement;
import com.baselet.gui.AutocompletionText;

public class OwnSyntaxPane {

	private static final String SEPARATOR = "    ";

	private final DefaultCompletionProvider provider = new DefaultCompletionProvider() {
		@Override
		protected boolean isValidChar(char ch) {
			return ch != ' '; // every character except space can be part of an autocompletion
		}
	};

	List<AutocompletionText> words = new ArrayList<AutocompletionText>();

	JPanel panel;
	RSyntaxTextArea textArea;
	RTextScrollPane scrollPane;
	private final JScrollPane tableScrollPane;
	private final JTable table;
	private PropertiesGridElement propertiesElement;
	private boolean tableMode;
	private FieldProperty selectedProperty;
	private FieldComposite parentFieldComposite;

	public OwnSyntaxPane() {

		panel = new JPanel(new FlowLayout());
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		textArea = new RSyntaxTextArea() {
			private static final long serialVersionUID = 7431070002967577129L;

			@Override
			public void undoLastAction() {
				CurrentDiagram.getInstance().getDiagramHandler().getController().undo();
			}

			@Override
			public void redoLastAction() {
				CurrentDiagram.getInstance().getDiagramHandler().getController().redo();
			}
		};

		// Setup highlighting
		createHightLightMap();
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
		atmf.putMapping(OwnTokenMaker.ID, OwnTokenMaker.class.getName());
		textArea.setSyntaxEditingStyle(OwnTokenMaker.ID);

		textArea.getSyntaxScheme().getStyle(TokenTypes.RESERVED_WORD).foreground = Converter.convert(ColorOwn.SYNTAX_HIGHLIGHTING);

		// Setup autocompletion
		createAutocompletionCompletionProvider();
		AutoCompletion ac = new AutoCompletion(provider);
		// ac.setShowDescWindow(true);
		ac.install(textArea);

		JLabel propertyLabel = new JLabel(" Properties");
		propertyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		propertyLabel.setFont(DerivedConfig.getPanelHeaderFont());

		panel.add(propertyLabel);

		textArea.setAntiAliasingEnabled(true);
		textArea.setFont(DerivedConfig.getPanelContentFont());
		scrollPane = new RTextScrollPane(textArea, false);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane);

		table = new JTable();
		tableScrollPane = new JScrollPane(table);

		textArea.getDocument().putProperty(PlainDocument.tabSizeAttribute, 3); // Reduce tab size
		setTableMode(false);
	}

	/**
	 * create one per class
	 * @param strings
	 */
	private void createAutocompletionCompletionProvider() {
		provider.clear();
		for (AutocompletionText word : words) {
			provider.addCompletion(new BasicCompletion(provider, word.getText(), word.getInfo()) {
				@Override
				public String toString() {
					if (getShortDescription() == null) {
						return getInputText();
					}
					return getInputText() + SEPARATOR + getShortDescription();
				}
			});
		}

	}

	private void createHightLightMap() {
		TokenMap myWordsToHighlight = new TokenMap();
		for (AutocompletionText word : words) {
			myWordsToHighlight.put(word.getText(), TokenTypes.RESERVED_WORD);
		}
		// use ugly static setter because OwnTokenMaker is unfortunately not instantiated by us
		OwnTokenMaker.setMyWordsToHighlight(myWordsToHighlight);
		// switch syntaxstyle to null and back to OwnTokenMaker to make sure the wordsToHighlight are reset!
		textArea.setSyntaxEditingStyle(null);
		textArea.setSyntaxEditingStyle(OwnTokenMaker.ID);
	}

	public String getText() {
		if (tableMode) {
			return propertiesElement.getPanelAttributes();
		}
		else {
			return textArea.getText();
		}
	}

	public JPanel getPanel() {
		return panel;
	}

	public void invalidate() {
		panel.invalidate();
	}

	public JTextComponent getTextComponent() {
		return textArea;
	}

	public void switchToProperty(FieldProperty fieldProperty) {
		parentFieldComposite = fieldProperty.getParentFieldComposite();
		propertiesElement = fieldProperty.getProperties();
		configurePropertiesElement();
	}

	public void deselectProperty() {
		parentFieldComposite = null;
		propertiesElement = null;
	}

	public void switchToElement(GridElement e) {
		words = e.getAutocompletionList();
		if (parentFieldComposite == null || e != parentFieldComposite) {
			if (parentFieldComposite != null) {
				parentFieldComposite.deselectAll();
			}
			if (e instanceof PropertiesGridElement) {
				propertiesElement = (PropertiesGridElement) e;
				configurePropertiesElement();
			}
			else {
				propertiesElement = null;
				configureTextElement(e.getPanelAttributes());
			}
		}
	}

	public void switchToNonElement(String text) {
		if (parentFieldComposite != null) {
			parentFieldComposite.deselectAll();
		}
		words = new ArrayList<AutocompletionText>();
		propertiesElement = null;
		configureTextElement(text);
	}

	private void configurePropertiesElement() {
		setTableMode(true);
		table.setModel(propertiesElement.getTableModel());
		table.setRowHeight(30);
		table.getColumnModel().getColumn(0).setCellEditor(null);
		table.getColumnModel().getColumn(1).setCellEditor(new PropertyCellEditor());

		table.invalidate();
	}

	private void configureTextElement(String text) {
		setTableMode(false);
		if (!textArea.getText().equals(text)) {
			textArea.setText(text); // Always set text even if they are equal to trigger correct syntax highlighting (if words to highlight have changed but text not)
		}
		textArea.setCaretPosition(0);
		createHightLightMap();
		createAutocompletionCompletionProvider();
	}

	public boolean isTableMode() {
		return tableMode;
	}

	public void setTableMode(boolean tableMode) {
		if (this.tableMode != tableMode) {
			if (tableMode) {
				panel.remove(scrollPane);
				panel.add(tableScrollPane);
				panel.revalidate();
				panel.repaint();
			}
			else {
				panel.remove(tableScrollPane);
				panel.add(scrollPane);
				panel.revalidate();
				panel.repaint();
			}
		}
		this.tableMode = tableMode;
	}

}
