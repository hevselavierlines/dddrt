package at.mic.dddrt.db;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import tk.baumi.main.ExportTask;
import tk.baumi.main.IBoundedContext;
import tk.baumi.main.ITextReporter;

public class DatabaseExportDialog extends JDialog implements ActionListener, ITextReporter {
	private static final long serialVersionUID = 722970658932094847L;
	private final JPanel buttonPanel;
	private final JTextArea sqlArea;
	private final JTextArea consoleArea;
	private final JButton buttonStart;
	private final JButton buttonCancel;
	private final JLabel labelDB;
	private final JTextField fieldDB;
	private final JLabel labelUser;
	private final JTextField fieldUser;
	private final JLabel labelPassword;
	private final JPasswordField fieldPassword;
	private List<IBoundedContext> boundedContexts;
	private final JLabel labelFolder;
	private final JTextField fieldFolder;
	private final JPanel panelFolder;
	private final JButton buttonFolder;

	public DatabaseExportDialog(Frame owner) {
		super(owner);

		setLayout(new BorderLayout());

		JPanel panelTop = new JPanel(new BorderLayout());
		JPanel panelInput = new JPanel(new GridBagLayout());
		labelFolder = new JLabel("Classes Folder");
		fieldFolder = new JTextField("C:\\Users\\baumi\\Documents\\testexport");
		buttonFolder = new JButton("...");
		labelDB = new JLabel("DB Connection");
		fieldDB = new JTextField("jdbc:oracle:thin:@localhost:1521:xe");
		labelUser = new JLabel("Username");
		fieldUser = new JTextField("afaci");
		labelPassword = new JLabel("Password");
		fieldPassword = new JPasswordField("afaci");

		panelFolder = new JPanel(new BorderLayout());
		panelFolder.add(fieldFolder, BorderLayout.CENTER);
		panelFolder.add(buttonFolder, BorderLayout.EAST);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 5, 0, 5);
		panelInput.add(labelFolder, gbc);
		gbc.weightx = 1;
		gbc.gridx = 1;
		gbc.gridy = 0;
		panelInput.add(panelFolder, gbc);
		gbc.weightx = 2;
		gbc.gridx = 0;
		gbc.gridy = 1;
		panelInput.add(labelDB, gbc);
		gbc.weightx = 2;
		gbc.gridx = 1;
		gbc.gridy = 1;
		panelInput.add(fieldDB, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		panelInput.add(labelUser, gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		panelInput.add(fieldUser, gbc);
		gbc.gridx = 0;
		gbc.gridy = 3;
		panelInput.add(labelPassword, gbc);
		gbc.gridx = 1;
		gbc.gridy = 3;
		panelInput.add(fieldPassword, gbc);
		panelTop.add(panelInput, BorderLayout.NORTH);
		add(panelTop, BorderLayout.NORTH);

		sqlArea = new JTextArea();
		JScrollPane spSql = new JScrollPane(sqlArea);
		consoleArea = new JTextArea();
		consoleArea.setEditable(false);
		JScrollPane spConsole = new JScrollPane(consoleArea);
		buttonStart = new JButton("Start");
		buttonCancel = new JButton("Cancel");

		JSplitPane centrePanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		centrePanel.setTopComponent(spSql);
		centrePanel.setBottomComponent(spConsole);
		centrePanel.setDividerLocation(500);

		buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(buttonStart);
		buttonPanel.add(buttonCancel);

		buttonStart.addActionListener(this);
		buttonFolder.addActionListener(this);
		buttonCancel.addActionListener(this);

		add(centrePanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		setTitle("Database Export");
		setSize(500, 900);
	}

	public void setSQLText(String sqlText) {
		sqlArea.setText(sqlText);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource().equals(buttonStart)) {
			startScript();
		}
		else if (arg0.getSource().equals(buttonFolder)) {
			JFileChooser fileChooser = new JFileChooser(fieldFolder.getText());
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fileChooser.showOpenDialog(DatabaseExportDialog.this) == JFileChooser.APPROVE_OPTION) {
				fieldFolder.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
		}
	}

	public void startScript() {
		consoleArea.setText("");
		new Thread(new Runnable() {

			@Override
			public void run() {
				setRunningState(true);
				ExportTask.exportBoundedContextsToJava(boundedContexts, new File(fieldFolder.getText()), DatabaseExportDialog.this);
				Connection connection = null;
				try {
					Class.forName("oracle.jdbc.driver.OracleDriver");
					connection = DriverManager.getConnection(fieldDB.getText(), fieldUser.getText(), fieldPassword.getText());
					ScriptRunner scriptRunner = new ScriptRunner(connection, false, false);
					scriptRunner.setLogWriter(DatabaseExportDialog.this);
					scriptRunner.setErrorLogWriter(DatabaseExportDialog.this);
					scriptRunner.runScript(new StringReader(sqlArea.getText()));
				} catch (Exception e) {
					reportTextln(e.getMessage());
				} finally {
					setRunningState(false);
					try {
						if (connection != null) {
							connection.close();
						}
					} catch (SQLException e) {
						reportTextln(e.getMessage());
					}
				}
			}
		}).start();
	}

	public void setBoundedContexts(List<IBoundedContext> boundedContexts) {
		this.boundedContexts = boundedContexts;
	}

	private void setRunningState(final boolean running) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				buttonStart.setEnabled(!running);
			}
		});
	}

	@Override
	public void reportText(final String text) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				consoleArea.append(text);
			}
		});

	}

	@Override
	public void reportTextln(String text) {
		reportText(text + "\n");
	}
}
