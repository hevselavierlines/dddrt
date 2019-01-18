package at.mic.dddrt.db;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.baselet.control.basics.geom.Rectangle;
import com.baselet.control.enums.ElementId;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.diagram.DiagramHandler;
import com.baselet.element.ElementFactorySwing;
import com.baselet.element.ddd.FieldComposite;
import com.baselet.gui.command.Controller;
import com.baselet.gui.command.DatabaseImport;

import at.mic.dddrt.db.model.ColumnRelation;
import at.mic.dddrt.db.model.Table;

public class DatabaseImportDialog extends JDialog implements ActionListener, WindowListener {
	private static final long serialVersionUID = 2814177914721604109L;
	private final JLabel labelDB;
	private final JTextField fieldDB;
	private final JLabel labelUser;
	private final JTextField fieldUser;
	private final JLabel labelPassword;
	private final JTextField fieldPassword;
	private final JButton connectButton;
	private final JButton importButton;
	private final JButton cancelButton;
	private final JButton selectAllButton, deselectAllButton, resolveRelationsButton;
	private final DefaultListModel<JCheckBox> tablesModel;
	private DatabaseImportManager databaseManager;
	private final JLabel errorLabel;
	private final JCheckBoxList checkBoxList;

	public DatabaseImportDialog(Frame owner) {
		super(owner);

		JPanel panelTop = new JPanel(new BorderLayout());
		JPanel panelInput = new JPanel(new GridBagLayout());
		labelDB = new JLabel("DB Connection");
		fieldDB = new JTextField("jdbc:oracle:thin:@localhost:1521:xe");
		labelUser = new JLabel("Username");
		fieldUser = new JTextField("afaci");
		labelPassword = new JLabel("Password");
		fieldPassword = new JPasswordField("afaci");
		connectButton = new JButton("Load Database Tables");
		connectButton.addActionListener(this);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 5, 0, 5);

		panelInput.add(labelDB, gbc);
		gbc.weightx = 2;
		gbc.gridx = 1;
		gbc.gridy = 0;
		panelInput.add(fieldDB, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		panelInput.add(labelUser, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		panelInput.add(fieldUser, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		panelInput.add(labelPassword, gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		panelInput.add(fieldPassword, gbc);
		panelTop.add(panelInput, BorderLayout.NORTH);
		panelTop.add(connectButton, BorderLayout.SOUTH);

		setLayout(new BorderLayout());
		this.add(panelTop, BorderLayout.NORTH);

		JPanel middleList = new JPanel(new BorderLayout());

		tablesModel = new DefaultListModel<JCheckBox>();
		checkBoxList = new JCheckBoxList(tablesModel);
		JScrollPane scrollListModel = new JScrollPane(checkBoxList);
		middleList.add(scrollListModel, BorderLayout.CENTER);

		JPanel selectionButtons = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		resolveRelationsButton = new JButton("Relation Check");
		resolveRelationsButton.addActionListener(this);
		gbc.gridx = 0;
		gbc.gridy = 0;
		selectionButtons.add(resolveRelationsButton, gbc);

		selectAllButton = new JButton("Select All");
		selectAllButton.addActionListener(this);
		gbc.gridx = 1;
		gbc.gridy = 0;
		selectionButtons.add(selectAllButton, gbc);

		deselectAllButton = new JButton("Deselect All");
		deselectAllButton.addActionListener(this);
		gbc.gridx = 2;
		gbc.gridy = 0;
		selectionButtons.add(deselectAllButton, gbc);

		middleList.add(selectionButtons, BorderLayout.SOUTH);

		this.add(middleList, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		importButton = new JButton("Import");
		importButton.addActionListener(this);
		bottomPanel.add(importButton, BorderLayout.CENTER);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		bottomPanel.add(cancelButton, BorderLayout.EAST);

		errorLabel = new JLabel();
		bottomPanel.add(errorLabel, BorderLayout.SOUTH);

		this.add(bottomPanel, BorderLayout.SOUTH);
		addWindowListener(this);
		setSize(450, 700);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		errorLabel.setText("");
		if (arg0.getSource() == connectButton) {
			connect();
		}
		else if (arg0.getSource() == cancelButton) {
			closeDatabaseConnection();
			setVisible(false);
			dispose();
		}
		else if (arg0.getSource() == resolveRelationsButton) {
			resolveRelations();
		}
		else if (arg0.getSource() == importButton) {
			doImport();
		}
		else if (arg0.getSource() == selectAllButton) {
			for (int i = 0; i < tablesModel.size(); i++) {
				JCheckBox checkbox = tablesModel.getElementAt(i);
				checkbox.setSelected(true);
			}
			checkBoxList.updateUI();
		}
		else if (arg0.getSource() == deselectAllButton) {
			for (int i = 0; i < tablesModel.size(); i++) {
				JCheckBox checkbox = tablesModel.getElementAt(i);
				checkbox.setSelected(false);
			}
			checkBoxList.updateUI();
		}
	}

	private void connect() {
		try {
			connectButton.setEnabled(false);
			new Thread() {
				@Override
				public void run() {
					try {
						databaseManager = new DatabaseImportManager(fieldDB.getText(), fieldUser.getText(), fieldPassword.getText());
						loadTables();

					} catch (final Exception ex) {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								connectButton.setEnabled(true);
								errorLabel.setText(ex.getMessage());
							}
						});
					}
				}
			}.start();

		} catch (Exception e) {
			errorLabel.setText(e.getMessage());
		}
	}

	private void loadTables() throws SQLException {
		final List<Table> tables = databaseManager.loadTables();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				connectButton.setEnabled(true);
				tablesModel.clear();
				for (Table table : tables) {
					JCheckBox tableCheckBox = new JCheckBox(table.getTableName());
					tableCheckBox.setSelected(false);
					tablesModel.addElement(tableCheckBox);
				}
			}
		});
	}

	private void resolveRelations() {
		resolveRelationsButton.setEnabled(false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					final Set<String> tablesToAdd = new TreeSet<String>();
					final List<Table> selectedTables = new LinkedList<Table>();
					for (int i = 0; i < tablesModel.size(); i++) {
						JCheckBox checkbox = tablesModel.getElementAt(i);
						if (checkbox.isSelected()) {
							selectedTables.add(new Table(checkbox.getText()));
						}
					}
					for (final Table table : selectedTables) {
						List<ColumnRelation> relations = databaseManager.loadRelations(table);
						for (ColumnRelation relation : relations) {
							String refTable = relation.getReferencingTable();
							boolean referenceExist = false;
							for (Table refTables : selectedTables) {
								if (refTables.getTableName().equals(refTable)) {
									referenceExist = true;
								}
							}
							if (!referenceExist) {
								tablesToAdd.add(refTable);
							}
						}
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							for (int i = 0; i < tablesModel.size(); i++) {
								JCheckBox checkbox = tablesModel.getElementAt(i);
								if (tablesToAdd.contains(checkbox.getText())) {
									checkbox.setSelected(true);
								}
							}
							checkBoxList.updateUI();
							resolveRelationsButton.setEnabled(true);
						}
					});
				} catch (final Exception ex) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							errorLabel.setText(ex.getMessage());
							resolveRelationsButton.setEnabled(true);
						}
					});
				}
			}
		}).start();
	}

	private void doImport() {
		importButton.setEnabled(false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					final List<Table> selectedTables = new LinkedList<Table>();
					for (int i = 0; i < tablesModel.size(); i++) {
						JCheckBox checkbox = tablesModel.getElementAt(i);
						if (checkbox.isSelected()) {
							selectedTables.add(new Table(checkbox.getText()));
						}
					}
					for (final Table table : selectedTables) {
						databaseManager.loadColumns(table);
					}
					final List<ColumnRelation> allRelations = new LinkedList<ColumnRelation>();
					for (final Table table : selectedTables) {
						allRelations.addAll(databaseManager.loadRelations(table));
					}
					final List<FieldComposite> elements = new LinkedList<FieldComposite>();
					for (final Table table : selectedTables) {
						Rectangle size = new Rectangle(0, 0, 350, 100);
						ElementId elementId = ElementId.DDDValueObject;
						if (table.hasPrimaryKey()) {
							elementId = ElementId.DDDEntity;
						}
						FieldComposite fieldComposite = (FieldComposite) ElementFactorySwing.create(
								elementId,
								size,
								"",
								null,
								CurrentDiagram.getInstance().getDiagramHandler(),
								null);
						fieldComposite.initFromDatabase(table);
						int totalHeight = fieldComposite.measureHeight();
						size.height = totalHeight;
						fieldComposite.setRectangle(size);
						elements.add(fieldComposite);
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							DiagramHandler handler = CurrentDiagram.getInstance().getDiagramHandler();
							Controller controller = handler.getController();
							controller.executeCommand(new DatabaseImport(elements, allRelations));
							closeDatabaseConnection();
							setVisible(false);
							dispose();
						}
					});
				} catch (final Exception ex) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							errorLabel.setText(ex.getMessage());
							importButton.setEnabled(true);
						}
					});
				}
			}
		}).start();
	}

	@Override
	public String getTitle() {
		return "Database Import";
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	private void closeDatabaseConnection() {
		if (databaseManager != null) {
			databaseManager.close();
		}
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		closeDatabaseConnection();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {

	}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

}
