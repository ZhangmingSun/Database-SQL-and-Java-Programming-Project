import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JLayeredPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

import java.awt.Font;
import java.sql.Connection;

public class mainGUI {
	private JFrame frame;

	private JTabbedPane tabbedPane;
	private JLayeredPane PanePassword;
	private PanelBookSearch panelBookSearch;
	private PanelBookChecking panelBookChecking;
	private PanelBorrowerManagement panelBorrowerManagement;
	private PanelFines panelFines;
	
	Connection dbConnection = null; // ......
	private JTextField textPassword;
	
	/** Launch the application. */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainGUI window = new mainGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/** ############# Trigger Event############# */
	public void panelPasswordEvent()
	{
		String passWord = textPassword.getText();
		//String passWord = "szm123";
		boolean isConnect;
		isConnect = mysqlOperation.MySQLConnectTest(passWord);
		if(isConnect == true) {
			JOptionPane.showMessageDialog(null, "Connect MySQL Succesfully!!!");
			mysqlOperation.password = passWord; //≈‰÷√√‹¬Î
			
			tabbedPane.add("Book Search", panelBookSearch);
			tabbedPane.add("Checking OUT / IN Books", panelBookChecking);
			tabbedPane.add("Borrower Management", panelBorrowerManagement);
			tabbedPane.add("Fines", panelFines);
			
			/** tabbedPaneµƒadd(),remove(), insert()µ»∑Ω∑®∫‹÷ÿ“™£°±ÿ–Î¡ÈªÓ”¶”√*/
			tabbedPane.remove(PanePassword);
		}		
	}
	
	/** Create the application. */
	public mainGUI() { initialize(); }

	/** Initialize the contents of the frame. */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1010, 559);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 994, 521);
		frame.getContentPane().add(tabbedPane);
		
		//====================Create different panels===================
		//panelTest = new PanelPassword();
		panelBookSearch = new PanelBookSearch();
		panelBookChecking = new PanelBookChecking();
		panelBorrowerManagement = new PanelBorrowerManagement();
		panelFines = new PanelFines();
		
		//================================panel of Password=====================================
		PanePassword = new JLayeredPane();
		tabbedPane.addTab("MySQL Password", null, PanePassword, null);
		PanePassword.setLayout(null);
		
		JPanel PasswordPanel = new JPanel();
		PasswordPanel.setBounds(284, 94, 440, 298);
		PanePassword.add(PasswordPanel);
		PasswordPanel.setLayout(null);
		
		JLabel LabelMySQLPasswrod = new JLabel("MySQL Passwrod:");
		LabelMySQLPasswrod.setFont(new Font("Œ¢»Ì—≈∫⁄", Font.BOLD, 20));
		LabelMySQLPasswrod.setBounds(128, 49, 194, 36);
		PasswordPanel.add(LabelMySQLPasswrod);
		
		textPassword = new JTextField();
		textPassword.setBounds(104, 95, 256, 45);
		PasswordPanel.add(textPassword);
		textPassword.setColumns(10);
		
		JButton ButtonPassword = new JButton("MySQL Connect Test");
		ButtonPassword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/** ############# Executing Trigger Event ############# */
				panelPasswordEvent();
			}
		});
		ButtonPassword.setFont(new Font("Œ¢»Ì—≈∫⁄", Font.BOLD, 16));
		ButtonPassword.setBounds(104, 191, 256, 55);
		PasswordPanel.add(ButtonPassword);
		
		JLabel Label_Hint = new JLabel("If no password, press button directly. ");
		Label_Hint.setFont(new Font("Œ¢»Ì—≈∫⁄", Font.PLAIN, 14));
		Label_Hint.setBounds(104, 150, 256, 15);
		PasswordPanel.add(Label_Hint);
	}
}
