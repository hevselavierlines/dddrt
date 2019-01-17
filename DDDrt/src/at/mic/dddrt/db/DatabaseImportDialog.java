package at.mic.dddrt.db;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
	private final JButton resolveRelationsButton;
	private final DefaultListModel<JCheckBox> tablesModel;
	private DatabaseImportManager databaseManager;
	private final JLabel errorLabel;

	public DatabaseImportDialog(Frame owner) {
		super(owner);

		JPanel panelTop = new JPanel(new BorderLayout());
		JPanel panelInput = new JPanel(new GridLayout(3, 2));
		labelDB = new JLabel("DB Connection");
		fieldDB = new JTextField("jdbc:oracle:thin:@localhost:1521:xe");
		labelUser = new JLabel("Username");
		fieldUser = new JTextField("afaci");
		labelPassword = new JLabel("Password");
		fieldPassword = new JTextField("afaci");
		connectButton = new JButton("Load Database Tables");
		connectButton.addActionListener(this);

		panelInput.add(labelDB);
		panelInput.add(fieldDB);
		panelInput.add(labelUser);
		panelInput.add(fieldUser);
		panelInput.add(labelPassword);
		panelInput.add(fieldPassword);
		panelTop.add(panelInput, BorderLayout.NORTH);
		panelTop.add(connectButton, BorderLayout.SOUTH);

		setLayout(new BorderLayout());
		this.add(panelTop, BorderLayout.NORTH);

		tablesModel = new DefaultListModel<JCheckBox>();
		JCheckBoxList checkBoxList = new JCheckBoxList(tablesModel);
		JScrollPane scrollListModel = new JScrollPane(checkBoxList);

		this.add(scrollListModel, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		importButton = new JButton("Import");
		importButton.addActionListener(this);
		bottomPanel.add(importButton, BorderLayout.CENTER);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		bottomPanel.add(cancelButton, BorderLayout.EAST);

		resolveRelationsButton = new JButton("Relation Check");
		resolveRelationsButton.addActionListener(this);
		bottomPanel.add(resolveRelationsButton, BorderLayout.WEST);

		errorLabel = new JLabel();
		bottomPanel.add(errorLabel, BorderLayout.NORTH);

		this.add(bottomPanel, BorderLayout.SOUTH);
		addWindowListener(this);
		setSize(480, 640);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == connectButton) {
			try {
				connectButton.setEnabled(false);
				new Thread() {
					@Override
					public void run() {
						try {
							databaseManager = new DatabaseImportManager(fieldDB.getText(), fieldUser.getText(), fieldPassword.getText());
							final List<Table> tables = databaseManager.loadTables();
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									connectButton.setEnabled(true);
									tablesModel.clear();
									for (Table table : tables) {
										JCheckBox tableCheckBox = new JCheckBox(table.getTableName());
										tableCheckBox.setSelected(true);
										tablesModel.addElement(tableCheckBox);
									}
								}
							});

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
		else if (arg0.getSource() == cancelButton) {
			closeDatabaseConnection();
			setVisible(false);
			dispose();
		}
		else if (arg0.getSource() == importButton) {
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
							try {
								databaseManager.loadColumns(table);
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						final List<ColumnRelation> allRelations = new LinkedList<ColumnRelation>();
						for (final Table table : selectedTables) {
							try {
								allRelations.addAll(databaseManager.loadRelations(table, selectedTables));
								System.out.println(table);
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						final List<FieldComposite> elements = new LinkedList<FieldComposite>();
						for (final Table table : selectedTables) {
							Rectangle size = new Rectangle(0, 0, 400, 100);
							FieldComposite fieldComposite = (FieldComposite) ElementFactorySwing.create(ElementId.DDDEntity, size, "", null, CurrentDiagram.getInstance().getDiagramHandler(), null);
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
