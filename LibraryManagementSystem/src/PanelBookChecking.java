import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.proteanit.sql.DbUtils;


public class PanelBookChecking extends JLayeredPane {
	private static final long serialVersionUID = 1L;
	
	private JTextField textISBN2;
	private JTextField textBranchID;
	private JTextField textCardNo;
	private JTextField textBookID;
	private JTextField textCardNo2;
	private JTextField textBorrower;
	private JTable tableCheckIn;
	
	Connection dbConnection = null; // ......
	
	/** ############# Trigger Event1############# */
	public void ButtonCheckingOutEvent()
	{
		if(textISBN2.getText().length() == 0)
			JOptionPane.showMessageDialog(null, "The ISBN is NULL!");
		else if(textISBN2.getText().length() != 10)
			JOptionPane.showMessageDialog(null, "The length of ISBN is not equal to 10!!!");
		else if(textBranchID.getText().length() == 0)
			JOptionPane.showMessageDialog(null, "The BranchID is NULL!");
		else if(Integer.parseInt(textBranchID.getText()) > 5 || Integer.parseInt(textBranchID.getText()) < 1)
			JOptionPane.showMessageDialog(null, "The BranchID is not in the range '0~5'!!!");
		else if(textCardNo.getText().length() != 8)
			JOptionPane.showMessageDialog(null, "The length of Card No. is not equal to 8!");
		else{
			mysqlOperation.CheckingOutBooks(textISBN2.getText(), textBranchID.getText(), textCardNo.getText());
		    //mysqlConnection.CheckingOutBooks("0515136379", "4", "ID000981");
		}		
	}
	
	/** ############# Trigger Event2############# */
	public void SearchBeforeCheckingInEvent()
	{
		if(textBookID.getText().length()==0 && textCardNo2.getText().length()==0 && textBorrower.getText().length()==0)
			JOptionPane.showMessageDialog(null, "Please input content in 'BookID, Card No., Borrower' Field!");
		else{
			try {
				dbConnection = mysqlOperation.dbConnector(); /** obtain the Connection */
				
				String str[] = textBorrower.getText().split("\\s+");
				String name1 = ""; String name2 = "";
				if(str.length==1) {name1 = str[0]; name2 = "$";} //需要给name2赋一个不相关的值
				else if(str.length>=2) { name1 = str[0]; name2 = str[1];}
				
				/** sqlQuery from input dbConnection */
				ResultSet rs = mysqlOperation.SearchBeforeCheckingIn(dbConnection, textBookID.getText(), textCardNo2.getText(), name1, name2);
				/** Using "rx2xml.jar" to display data into table from ResultSet */
				tableCheckIn.setModel(DbUtils.resultSetToTableModel(rs));
				/** Once rs is closed, it could not be used! */
				rs.close();
				dbConnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
	}
	
	/** ############# Trigger Event3############# */
	public void ButtonCheckingInEvent()
	{
		if(tableCheckIn.getSelectedRow() >= 0) {//如果没有选中行，则返回-1
			//JOptionPane.showMessageDialog(null, tableCheckIn.getValueAt(tableCheckIn.getSelectedRow(), 6));
			
			//获取选中行的最后一列（即位置6）, 注意返回的是null类型，不是空串，但是text文本框获得的是""，不是null
			String Date_in = (String)tableCheckIn.getValueAt(tableCheckIn.getSelectedRow(), 6);
			if(Date_in != null)
				JOptionPane.showMessageDialog(null, "Failed, because ihis book has already been checked in !!!");
			else
			{
				//JOptionPane.showMessageDialog(null, "Date_in is 空串 ");
				int Loan_id = (int)tableCheckIn.getValueAt(tableCheckIn.getSelectedRow(), 0);
				String ISBN = (String)tableCheckIn.getValueAt(tableCheckIn.getSelectedRow(), 1); // 获取制定单元格中的内容
				int BranchID = (int)tableCheckIn.getValueAt(tableCheckIn.getSelectedRow(), 2);
				//JOptionPane.showMessageDialog(null, Integer.toString(Loan_id));
				//JOptionPane.showMessageDialog(null, ISBN);
				//JOptionPane.showMessageDialog(null, Integer.toString(BranchID));
				
				//checkin操作就是在BOOK_LOANS表格中添加date_in，并在BOOK_COPIES的availability中自动加1
				mysqlOperation.CheckingInBooks(Integer.toString(Loan_id), ISBN, Integer.toString(BranchID));
				/**/
			}
		}		
	}
	
	/** ############# Initial Panel ############# */
	public PanelBookChecking() {
		JPanel panelCheckingOut = new JPanel();
		panelCheckingOut.setToolTipText("");
		panelCheckingOut.setBounds(20, 67, 249, 266);
		add(panelCheckingOut);
		panelCheckingOut.setLayout(null);
		
		textISBN2 = new JTextField();
		textISBN2.setBounds(114, 32, 103, 21);
		panelCheckingOut.add(textISBN2);
		textISBN2.setColumns(10);
		
		textBranchID = new JTextField();
		textBranchID.setBounds(114, 82, 103, 21);
		panelCheckingOut.add(textBranchID);
		textBranchID.setColumns(10);
		
		textCardNo = new JTextField();
		textCardNo.setBounds(114, 130, 103, 21);
		panelCheckingOut.add(textCardNo);
		textCardNo.setColumns(10);
		
		JLabel LabelISBN = new JLabel("ISBN");
		LabelISBN.setFont(new Font("微软雅黑", Font.BOLD, 14));
		LabelISBN.setBounds(24, 35, 54, 15);
		panelCheckingOut.add(LabelISBN);
		
		JLabel LableBranchID = new JLabel("Branch ID");
		LableBranchID.setFont(new Font("微软雅黑", Font.BOLD, 14));
		LableBranchID.setBounds(24, 85, 72, 15);
		panelCheckingOut.add(LableBranchID);
		
		JLabel LabelCardNo = new JLabel("Card No.");
		LabelCardNo.setFont(new Font("微软雅黑", Font.BOLD, 14));
		LabelCardNo.setBounds(24, 133, 72, 15);
		panelCheckingOut.add(LabelCardNo);
		
		//=======================Checking Out Button=======================
		JButton ButtonCheckOut = new JButton("Checking Out");
		ButtonCheckOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/** ############# Executing Trigger Event ############# */
				ButtonCheckingOutEvent();
			}
		});
		ButtonCheckOut.setForeground(Color.BLUE);
		ButtonCheckOut.setFont(new Font("微软雅黑", Font.BOLD, 14));
		ButtonCheckOut.setBounds(48, 184, 133, 44);
		panelCheckingOut.add(ButtonCheckOut);
		
		JPanel panelCheckingIn = new JPanel();
		panelCheckingIn.setBounds(312, 20, 646, 462);
		add(panelCheckingIn);
		panelCheckingIn.setLayout(null);
		
		JLabel LabelBookID = new JLabel("Book ISBN");
		LabelBookID.setFont(new Font("微软雅黑", Font.BOLD, 14));
		LabelBookID.setHorizontalAlignment(SwingConstants.LEFT);
		LabelBookID.setBounds(24, 28, 75, 15);
		panelCheckingIn.add(LabelBookID);
		
		JLabel LabelCardNo2 = new JLabel("Card No.");
		LabelCardNo2.setFont(new Font("微软雅黑", Font.BOLD, 14));
		LabelCardNo2.setHorizontalAlignment(SwingConstants.LEFT);
		LabelCardNo2.setBounds(24, 60, 65, 15);
		panelCheckingIn.add(LabelCardNo2);
		
		JLabel LabelBorrower = new JLabel("Borrower");
		LabelBorrower.setFont(new Font("微软雅黑", Font.BOLD, 14));
		LabelBorrower.setHorizontalAlignment(SwingConstants.LEFT);
		LabelBorrower.setBounds(24, 95, 80, 15);
		panelCheckingIn.add(LabelBorrower);
		
		textBookID = new JTextField();
		textBookID.setBounds(116, 26, 116, 21);
		panelCheckingIn.add(textBookID);
		textBookID.setColumns(10);
		
		textCardNo2 = new JTextField();
		textCardNo2.setBounds(116, 58, 116, 21);
		panelCheckingIn.add(textCardNo2);
		textCardNo2.setColumns(10);
		
		textBorrower = new JTextField();
		textBorrower.setBounds(116, 93, 116, 21);
		panelCheckingIn.add(textBorrower);
		textBorrower.setColumns(10);
		
		JScrollPane scrollPaneCheckIn = new JScrollPane();
		scrollPaneCheckIn.setBounds(10, 133, 626, 319);
		panelCheckingIn.add(scrollPaneCheckIn);
		
		tableCheckIn = new JTable();
		scrollPaneCheckIn.setViewportView(tableCheckIn);
		
		//=======================Search Before Checking In=======================
		JButton ButtonSearch = new JButton("Search");
		ButtonSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/** ############# Executing Trigger Event ############# */
				SearchBeforeCheckingInEvent();
			}
		});
		ButtonSearch.setFont(new Font("微软雅黑", Font.BOLD, 14));
		ButtonSearch.setBounds(264, 41, 93, 53);
		panelCheckingIn.add(ButtonSearch);	
		
		//=======================Checking In Button=======================
		JButton ButtonCheckingIn = new JButton("Checking In");
		ButtonCheckingIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/** ############# Executing Trigger Event ############# */
				ButtonCheckingInEvent();
			}
		});
		ButtonCheckingIn.setForeground(Color.BLUE);
		ButtonCheckingIn.setFont(new Font("微软雅黑", Font.BOLD, 14));
		ButtonCheckingIn.setBounds(458, 38, 123, 59);
		panelCheckingIn.add(ButtonCheckingIn);
	}
}
