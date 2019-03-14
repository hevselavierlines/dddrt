package at.mic.dddrt.db;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class DatabaseExportDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 722970658932094847L;
	private final JPanel centrePanel;
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

	public DatabaseExportDialog(Frame owner) {
		super(owner);

		setLayout(new BorderLayout());

		JPanel panelTop = new JPanel(new BorderLayout());
		JPanel panelInput = new JPanel(new GridBagLayout());
		labelDB = new JLabel("DB Connection");
		fieldDB = new JTextField("jdbc:oracle:thin:@localhost:1521:xe");
		labelUser = new JLabel("Username");
		fieldUser = new JTextField("afaci");
		labelPassword = new JLabel("Password");
		fieldPassword = new JPasswordField("afaci");
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
		add(panelTop, BorderLayout.NORTH);

		sqlArea = new JTextArea();
		JScrollPane spSql = new JScrollPane(sqlArea);
		consoleArea = new JTextArea();
		JScrollPane spConsole = new JScrollPane(consoleArea);
		buttonStart = new JButton("Start");
		buttonCancel = new JButton("Cancel");

		centrePanel = new JPanel(new BorderLayout());

		centrePanel.add(spSql, BorderLayout.CENTER);
		centrePanel.add(spConsole, BorderLayout.SOUTH);

		buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(buttonStart);
		buttonPanel.add(buttonCancel);

		buttonStart.addActionListener(this);

		add(centrePanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		setTitle("Database Export");
		setSize(450, 700);
	}

	public void setSQLText(String sqlText) {
		sqlArea.setText(sqlText);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource().equals(buttonStart)) {
			startScript();
		}
	}

	public void startScript() {

		Connection connection = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection(fieldDB.getText(), fieldUser.getText(), fieldPassword.getText());
			ScriptRunner sr = new ScriptRunner(connection, false, false);
			sr.runScript(new StringReader(sqlArea.getText()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
