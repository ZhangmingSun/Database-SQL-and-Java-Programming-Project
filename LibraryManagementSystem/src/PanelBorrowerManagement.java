import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class PanelBorrowerManagement extends JLayeredPane {
	private static final long serialVersionUID = 1L;
	
	private JTextField textSSN;
	private JTextField textFname;
	private JTextField textLname;
	private JTextField textStreet;
	private JTextField textCity;
	private JTextField textState;
	private JTextField textPhone;
	
	public PanelBorrowerManagement() {
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(256, 49, 386, 400);
		add(panel);
		
		JLabel labelFname = new JLabel("Fname");
		labelFname.setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 14));
		labelFname.setBounds(40, 75, 54, 15);
		panel.add(labelFname);
		
		JLabel labelLname = new JLabel("Lname");
		labelLname.setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 14));
		labelLname.setBounds(40, 116, 54, 15);
		panel.add(labelLname);
		
		JLabel labelCity = new JLabel("City");
		labelCity.setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 14));
		labelCity.setBounds(40, 197, 54, 15);
		panel.add(labelCity);
		
		JLabel labelState = new JLabel("State");
		labelState.setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 14));
		labelState.setBounds(40, 236, 54, 15);
		panel.add(labelState);
		
		JLabel labelStreet = new JLabel("Street");
		labelStreet.setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 14));
		labelStreet.setBounds(40, 154, 54, 15);
		panel.add(labelStreet);
		
		JLabel labelSSN = new JLabel("SSN");
		labelSSN.setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 14));
		labelSSN.setBounds(40, 28, 77, 15);
		panel.add(labelSSN);
		
		textSSN = new JTextField();
		textSSN.setColumns(10);
		textSSN.setBounds(127, 25, 136, 21);
		panel.add(textSSN);
		
		textFname = new JTextField();
		textFname.setColumns(10);
		textFname.setBounds(127, 72, 136, 21);
		panel.add(textFname);
		
		textLname = new JTextField();
		textLname.setColumns(10);
		textLname.setBounds(127, 113, 136, 21);
		panel.add(textLname);
		
		textStreet = new JTextField();
		textStreet.setColumns(10);
		textStreet.setBounds(127, 154, 228, 21);
		panel.add(textStreet);
		
		textCity = new JTextField();
		textCity.setColumns(10);
		textCity.setBounds(127, 195, 136, 21);
		panel.add(textCity);
		
		textState = new JTextField();
		textState.setColumns(10);
		textState.setBounds(127, 234, 136, 21);
		panel.add(textState);
		
		JLabel labelPhone = new JLabel("Phone");
		labelPhone.setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 14));
		labelPhone.setBounds(40, 280, 54, 15);
		panel.add(labelPhone);
		
		textPhone = new JTextField();
		textPhone.setColumns(10);
		textPhone.setBounds(127, 278, 136, 21);
		panel.add(textPhone);
		
		//=======================Create New Account=======================
		JButton buttonCreateAccount = new JButton("Create Account");
		buttonCreateAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String SSN = textSSN.getText();
				String Fname = textFname.getText();
				String Lname = textLname.getText();
				String Street = textStreet.getText();
				String City = textCity.getText();
				String State = textState.getText();
				String Phone = textPhone.getText();
				String[] infoList = {SSN, Fname, Lname, Street, City, State, Phone};
				
				//======Create New Account======
				mysqlOperation.CreateNewAccount(infoList);
			}
		});
		buttonCreateAccount.setForeground(Color.BLUE);
		buttonCreateAccount.setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 14));
		buttonCreateAccount.setBounds(110, 324, 175, 52);
		panel.add(buttonCreateAccount);
		
		JLabel lblNewLabel = new JLabel("(Optional)");
		lblNewLabel.setBounds(276, 281, 67, 15);
		panel.add(lblNewLabel);

	}

}
