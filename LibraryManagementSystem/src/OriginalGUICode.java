
public class OriginalGUICode {

//	private AbstractListModel<String> ALM;
//	private JTable BookSearchTable;
//	private JTextField textISBN;
//	private JTextField textTitle;
//	private JTextField textAuthor;
//	private JLabel lableResultNum;

//	private JLayeredPane panel_1;
//	private JLayeredPane panel_2;
//	private JLayeredPane panel_3;
//	private JLayeredPane panel_4;
	
//	private JTextField textISBN2;
//	private JTextField textBranchID;
//	private JTextField textCardNo;
//	private JTextField textBookID;
//	private JTextField textCardNo2;
//	private JTextField textBorrower;
//	private JTable tableCheckIn;
	
//	private JTextField textSSN;
//	private JTextField textFname;
//	private JTextField textLname;
//	private JTextField textStreet;
//	private JTextField textCity;
//	private JTextField textState;
//	private JTextField textPhone;
	
//	private JTable tableFines;
//	private JTextField textCardNumber;
//	private JCheckBox CheckBoxFilterOutPaid;
//	private JLabel labelFinesNum;
	
	public OriginalGUICode() {
//		//tabbedPane.add("Book Search", panel_1);
//		//tabbedPane.add("Checking OUT / IN Books", panel_2);
//		//tabbedPane.add("Borrower Management", panel_3);
//		//tabbedPane.add("Fines", panel_4);
		
		//================================panel_1=====================================
//		panel_1 = new JLayeredPane();
//		tabbedPane.addTab("Book Search", panel_1);
//		panel_1.setLayout(null);
//		
//		JScrollPane scrollPane = new JScrollPane();
//		scrollPane.setBounds(10, 94, 969, 388);
//		panel_1.add(scrollPane);
//		
//		textISBN = new JTextField();
//		textISBN.setBounds(118, 41, 111, 21);
//		panel_1.add(textISBN);
//		textISBN.setColumns(10);
//		
//		textTitle = new JTextField();
//		textTitle.setBounds(120, 8, 439, 21);
//		panel_1.add(textTitle);
//		textTitle.setColumns(10);
//		
//		textAuthor = new JTextField();
//		textAuthor.setBounds(351, 41, 208, 21);
//		panel_1.add(textAuthor);
//		textAuthor.setColumns(10);
//		
//		JLabel Label_BookTitle = new JLabel("Book Title");
//		Label_BookTitle.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		Label_BookTitle.setBounds(36, 10, 81, 15);
//		panel_1.add(Label_BookTitle);
//		
//		JLabel Label_ISBN = new JLabel("ISBN");
//		Label_ISBN.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		Label_ISBN.setBounds(36, 43, 54, 15);
//		panel_1.add(Label_ISBN);
//		
//		JLabel Label_Author = new JLabel("Author");
//		Label_Author.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		Label_Author.setBounds(287, 43, 54, 15);
//		panel_1.add(Label_Author);
//		
//		BookSearchTable = new JTable();
//		scrollPane.setViewportView(BookSearchTable); //scrollPane中加载table
//		
//		JButton ButtonBookSearch = new JButton("Book Search");
//		ButtonBookSearch.setForeground(Color.BLUE);
//		ButtonBookSearch.setFont(new Font("微软雅黑", Font.BOLD, 13));
//		ButtonBookSearch.setBounds(702, 15, 125, 43);
//		panel_1.add(ButtonBookSearch);
//		
//		lableResultNum = new JLabel(""); //刚开始为空Search Result Number:
//		lableResultNum.setBounds(785, 68, 194, 15);
//		panel_1.add(lableResultNum);
//		
//		ButtonBookSearch.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				if(textISBN.getText().length()==0 && textTitle.getText().length()==0
//						&& textAuthor.getText().length()==0)
//					JOptionPane.showMessageDialog(null, "Please input content in 'ISBN, Book Title, Author' Field!");
//				else{
//					try {
//						dbConnection = mysqlOperation.dbConnector(); /** obtain the Connection */
//						/** sqlQuery from input dbConnection */
//						ResultSet rs = mysqlOperation.BookQuery(dbConnection, textISBN.getText(), textTitle.getText(), textAuthor.getText());
//						
//						/** Display "Search Result Number" */
//						int rowcount = 0;
//						if (rs.last()) {
//						  rowcount = rs.getRow();
//						  rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
//						}
//						else rowcount = 0;
//						lableResultNum.setText("Search Result Number: " + rowcount);
//						
//						/** Using "rx2xml.jar" to display data into table from ResultSet */
//						BookSearchTable.setModel(DbUtils.resultSetToTableModel(rs));
//						/** Once rs is closed, it could not be used! */
//						rs.close();
//						dbConnection.close();
//					} catch (SQLException e) {
//						//e.printStackTrace();
//						JOptionPane.showMessageDialog(null, e.getMessage());
//					}
//				}
//				//String str = textTitle.getText();
//				//JOptionPane.showMessageDialog(null, str);
//			}
//		});
		//================================panel_2=====================================
//		panel_2 = new JLayeredPane();
//		tabbedPane.addTab("Checking OUT / IN Books", panel_2);
//		panel_2.setLayout(null);
//		
//		JPanel panelCheckingOut = new JPanel();
//		panelCheckingOut.setToolTipText("");
//		panelCheckingOut.setBounds(20, 67, 249, 266);
//		panel_2.add(panelCheckingOut);
//		panelCheckingOut.setLayout(null);
//		
//		textISBN2 = new JTextField();
//		textISBN2.setBounds(114, 32, 103, 21);
//		panelCheckingOut.add(textISBN2);
//		textISBN2.setColumns(10);
//		
//		textBranchID = new JTextField();
//		textBranchID.setBounds(114, 82, 103, 21);
//		panelCheckingOut.add(textBranchID);
//		textBranchID.setColumns(10);
//		
//		textCardNo = new JTextField();
//		textCardNo.setBounds(114, 130, 103, 21);
//		panelCheckingOut.add(textCardNo);
//		textCardNo.setColumns(10);
//		
//		JLabel LabelISBN = new JLabel("ISBN");
//		LabelISBN.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		LabelISBN.setBounds(24, 35, 54, 15);
//		panelCheckingOut.add(LabelISBN);
//		
//		JLabel LableBranchID = new JLabel("Branch ID");
//		LableBranchID.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		LableBranchID.setBounds(24, 85, 72, 15);
//		panelCheckingOut.add(LableBranchID);
//		
//		JLabel LabelCardNo = new JLabel("Card No.");
//		LabelCardNo.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		LabelCardNo.setBounds(24, 133, 72, 15);
//		panelCheckingOut.add(LabelCardNo);
//		
//		//=======================Checking Out Button=======================
//		JButton ButtonCheckOut = new JButton("Checking Out");
//		ButtonCheckOut.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				if(textISBN2.getText().length() == 0)
//					JOptionPane.showMessageDialog(null, "The ISBN is NULL!");
//				else if(textISBN2.getText().length() != 10)
//					JOptionPane.showMessageDialog(null, "The length of ISBN is not equal to 10!!!");
//				else if(textBranchID.getText().length() == 0)
//					JOptionPane.showMessageDialog(null, "The BranchID is NULL!");
//				else if(Integer.parseInt(textBranchID.getText()) > 5 || Integer.parseInt(textBranchID.getText()) < 1)
//					JOptionPane.showMessageDialog(null, "The BranchID is not in the range '0~5'!!!");
//				else if(textCardNo.getText().length() != 8)
//					JOptionPane.showMessageDialog(null, "The length of Card No. is not equal to 8!");
//				else{
//					mysqlOperation.CheckingOutBooks(textISBN2.getText(), textBranchID.getText(), textCardNo.getText());
//				    //mysqlConnection.CheckingOutBooks("0515136379", "4", "ID000981");
//				}
//			}
//		});
//		ButtonCheckOut.setForeground(Color.BLUE);
//		ButtonCheckOut.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		ButtonCheckOut.setBounds(48, 184, 133, 44);
//		panelCheckingOut.add(ButtonCheckOut);
//		
//		JPanel panelCheckingIn = new JPanel();
//		panelCheckingIn.setBounds(312, 20, 646, 462);
//		panel_2.add(panelCheckingIn);
//		panelCheckingIn.setLayout(null);
//		
//		JLabel LabelBookID = new JLabel("Book ISBN");
//		LabelBookID.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		LabelBookID.setHorizontalAlignment(SwingConstants.LEFT);
//		LabelBookID.setBounds(24, 28, 75, 15);
//		panelCheckingIn.add(LabelBookID);
//		
//		JLabel LabelCardNo2 = new JLabel("Card No.");
//		LabelCardNo2.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		LabelCardNo2.setHorizontalAlignment(SwingConstants.LEFT);
//		LabelCardNo2.setBounds(24, 60, 65, 15);
//		panelCheckingIn.add(LabelCardNo2);
//		
//		JLabel LabelBorrower = new JLabel("Borrower");
//		LabelBorrower.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		LabelBorrower.setHorizontalAlignment(SwingConstants.LEFT);
//		LabelBorrower.setBounds(24, 95, 80, 15);
//		panelCheckingIn.add(LabelBorrower);
//		
//		textBookID = new JTextField();
//		textBookID.setBounds(116, 26, 116, 21);
//		panelCheckingIn.add(textBookID);
//		textBookID.setColumns(10);
//		
//		textCardNo2 = new JTextField();
//		textCardNo2.setBounds(116, 58, 116, 21);
//		panelCheckingIn.add(textCardNo2);
//		textCardNo2.setColumns(10);
//		
//		textBorrower = new JTextField();
//		textBorrower.setBounds(116, 93, 116, 21);
//		panelCheckingIn.add(textBorrower);
//		textBorrower.setColumns(10);
//		
//		JScrollPane scrollPaneCheckIn = new JScrollPane();
//		scrollPaneCheckIn.setBounds(10, 133, 626, 319);
//		panelCheckingIn.add(scrollPaneCheckIn);
//		
//		tableCheckIn = new JTable();
//		scrollPaneCheckIn.setViewportView(tableCheckIn);
//		
//		//=======================Search Before Checking In=======================
//		JButton ButtonSearch = new JButton("Search");
//		ButtonSearch.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				if(textBookID.getText().length()==0 && textCardNo2.getText().length()==0 && textBorrower.getText().length()==0)
//					JOptionPane.showMessageDialog(null, "Please input content in 'BookID, Card No., Borrower' Field!");
//				else{
//					try {
//						dbConnection = mysqlOperation.dbConnector(); /** obtain the Connection */
//						
//						String str[] = textBorrower.getText().split("\\s+");
//						String name1 = ""; String name2 = "";
//						if(str.length==1) {name1 = str[0]; name2 = "$";} //需要给name2赋一个不相关的值
//						else if(str.length>=2) { name1 = str[0]; name2 = str[1];}
//						
//						/** sqlQuery from input dbConnection */
//						ResultSet rs = mysqlOperation.SearchBeforeCheckingIn(dbConnection, textBookID.getText(), textCardNo2.getText(), name1, name2);
//						/** Using "rx2xml.jar" to display data into table from ResultSet */
//						tableCheckIn.setModel(DbUtils.resultSetToTableModel(rs));
//						/** Once rs is closed, it could not be used! */
//						rs.close();
//						dbConnection.close();
//					} catch (SQLException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//		ButtonSearch.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		ButtonSearch.setBounds(264, 41, 93, 53);
//		panelCheckingIn.add(ButtonSearch);	
//		
//		//=======================Checking In Button=======================
//		JButton ButtonCheckingIn = new JButton("Checking In");
//		ButtonCheckingIn.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				if(tableCheckIn.getSelectedRow() >= 0) {//如果没有选中行，则返回-1
//					//JOptionPane.showMessageDialog(null, tableCheckIn.getValueAt(tableCheckIn.getSelectedRow(), 6));
//					
//					//获取选中行的最后一列（即位置6）, 注意返回的是null类型，不是空串，但是text文本框获得的是""，不是null
//					String Date_in = (String)tableCheckIn.getValueAt(tableCheckIn.getSelectedRow(), 6);
//					if(Date_in != null)
//						JOptionPane.showMessageDialog(null, "Failed, because ihis book has already been checked in !!!");
//					else
//					{
//						//JOptionPane.showMessageDialog(null, "Date_in is 空串 ");
//						int Loan_id = (int)tableCheckIn.getValueAt(tableCheckIn.getSelectedRow(), 0);
//						String ISBN = (String)tableCheckIn.getValueAt(tableCheckIn.getSelectedRow(), 1); // 获取制定单元格中的内容
//						int BranchID = (int)tableCheckIn.getValueAt(tableCheckIn.getSelectedRow(), 2);
//						//JOptionPane.showMessageDialog(null, Integer.toString(Loan_id));
//						//JOptionPane.showMessageDialog(null, ISBN);
//						//JOptionPane.showMessageDialog(null, Integer.toString(BranchID));
//						
//						//checkin操作就是在BOOK_LOANS表格中添加date_in，并在BOOK_COPIES的availability中自动加1
//						mysqlOperation.CheckingInBooks(Integer.toString(Loan_id), ISBN, Integer.toString(BranchID));
//						/**/
//					}
//				}
//			}
//		});
//		ButtonCheckingIn.setForeground(Color.BLUE);
//		ButtonCheckingIn.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		ButtonCheckingIn.setBounds(458, 38, 123, 59);
//		panelCheckingIn.add(ButtonCheckingIn);
		
		//================================panel_3=====================================
//		panel_3 = new JLayeredPane();
//		tabbedPane.addTab("Borrower Management", null, panel_3, null);
		
//		JPanel panel = new JPanel();
//		panel.setLayout(null);
//		panel.setBounds(256, 49, 386, 400);
//		panel_3.add(panel);
//		
//		JLabel labelFname = new JLabel("Fname");
//		labelFname.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		labelFname.setBounds(40, 75, 54, 15);
//		panel.add(labelFname);
//		
//		JLabel labelLname = new JLabel("Lname");
//		labelLname.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		labelLname.setBounds(40, 116, 54, 15);
//		panel.add(labelLname);
//		
//		JLabel labelCity = new JLabel("City");
//		labelCity.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		labelCity.setBounds(40, 197, 54, 15);
//		panel.add(labelCity);
//		
//		JLabel labelState = new JLabel("State");
//		labelState.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		labelState.setBounds(40, 236, 54, 15);
//		panel.add(labelState);
//		
//		JLabel labelStreet = new JLabel("Street");
//		labelStreet.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		labelStreet.setBounds(40, 154, 54, 15);
//		panel.add(labelStreet);
//		
//		JLabel labelSSN = new JLabel("SSN");
//		labelSSN.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		labelSSN.setBounds(40, 28, 77, 15);
//		panel.add(labelSSN);
//		
//		textSSN = new JTextField();
//		textSSN.setColumns(10);
//		textSSN.setBounds(127, 25, 136, 21);
//		panel.add(textSSN);
//		
//		textFname = new JTextField();
//		textFname.setColumns(10);
//		textFname.setBounds(127, 72, 136, 21);
//		panel.add(textFname);
//		
//		textLname = new JTextField();
//		textLname.setColumns(10);
//		textLname.setBounds(127, 113, 136, 21);
//		panel.add(textLname);
//		
//		textStreet = new JTextField();
//		textStreet.setColumns(10);
//		textStreet.setBounds(127, 154, 228, 21);
//		panel.add(textStreet);
//		
//		textCity = new JTextField();
//		textCity.setColumns(10);
//		textCity.setBounds(127, 195, 136, 21);
//		panel.add(textCity);
//		
//		textState = new JTextField();
//		textState.setColumns(10);
//		textState.setBounds(127, 234, 136, 21);
//		panel.add(textState);
//		
//		JLabel labelPhone = new JLabel("Phone");
//		labelPhone.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		labelPhone.setBounds(40, 280, 54, 15);
//		panel.add(labelPhone);
//		
//		textPhone = new JTextField();
//		textPhone.setColumns(10);
//		textPhone.setBounds(127, 278, 136, 21);
//		panel.add(textPhone);
//		
//		//=======================Create New Account=======================
//		JButton buttonCreateAccount = new JButton("Create Account");
//		buttonCreateAccount.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				String SSN = textSSN.getText();
//				String Fname = textFname.getText();
//				String Lname = textLname.getText();
//				String Street = textStreet.getText();
//				String City = textCity.getText();
//				String State = textState.getText();
//				String Phone = textPhone.getText();
//				String[] infoList = {SSN, Fname, Lname, Street, City, State, Phone};
//				
//				//======Create New Account======
//				mysqlOperation.CreateNewAccount(infoList);
//			}
//		});
//		buttonCreateAccount.setForeground(Color.BLUE);
//		buttonCreateAccount.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		buttonCreateAccount.setBounds(110, 324, 175, 52);
//		panel.add(buttonCreateAccount);
//		
//		JLabel lblNewLabel = new JLabel("(Optional)");
//		lblNewLabel.setBounds(276, 281, 67, 15);
//		panel.add(lblNewLabel);
		
		//================================panel_4=====================================
//		panel_4 = new JLayeredPane();
//		tabbedPane.addTab("Fines", null, panel_4, null);
		
//		JScrollPane scrollPaneFine = new JScrollPane();
//		scrollPaneFine.setBounds(142, 139, 713, 318);
//		panel_4.add(scrollPaneFine);
//		
//		tableFines = new JTable();
//		scrollPaneFine.setViewportView(tableFines);
//		
//		labelFinesNum = new JLabel("");
//		labelFinesNum.setBounds(682, 467, 173, 15);
//		panel_4.add(labelFinesNum);
//		
//		//=======================Refresh Fines=======================
//		JButton ButtonRefreshFines = new JButton("Update/Refresh FINES");
//		ButtonRefreshFines.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				// >>>>>> card_no中包含747的选项含有两次违规记录，可能会被用来测试
//				
//				String CardNum = textCardNumber.getText().trim();
//				if(CardNum.length()>0 && CardNum.length()!=8)
//					JOptionPane.showMessageDialog(null, "The length of Card No. is not equal to 8 !!!");
//				
//				boolean FilterOutPaid = CheckBoxFilterOutPaid.isSelected(); //没选中返回false，选中返回true
//
//				int cnt = mysqlOperation.FinesTableUpdatedDisplay(tableFines, CardNum, FilterOutPaid);
//				labelFinesNum.setText("Search Result Number: " + cnt);
//			}
//		});
//		ButtonRefreshFines.setForeground(Color.BLUE);
//		ButtonRefreshFines.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		ButtonRefreshFines.setBounds(300, 74, 200, 45);
//		panel_4.add(ButtonRefreshFines);
//		
//		//=======================Enter Payment=======================
//		JButton ButtonEnterPayment = new JButton("Update FINES Record");
//		ButtonEnterPayment.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				
//				//BigDecimal EnterPayment = new BigDecimal("0.00");
//				String Card_no = (String)tableFines.getValueAt(tableFines.getSelectedRow(), 0);
//				int paid = (int)tableFines.getValueAt(tableFines.getSelectedRow(), 2);
//				
//				//=========很关键，tableFines默认返回是double，但是修改后返回是String，所以使用Object类型=========
//				Object ob = (Object)tableFines.getValueAt(tableFines.getSelectedRow(), 1);
//				double EnterPayment = Double.parseDouble(ob.toString());
//				//JOptionPane.showMessageDialog(null, EnterPayment); return;
//				
//				if(paid == 0)
//					JOptionPane.showMessageDialog(null, "Because paid=FALSE, Fail to Update!");	
//				else
//					mysqlOperation.UpdateFINESRecord(Card_no, EnterPayment);/**/		
//			}
//		});
//		ButtonEnterPayment.setForeground(Color.BLUE);
//		ButtonEnterPayment.setFont(new Font("微软雅黑", Font.BOLD, 13));
//		ButtonEnterPayment.setBounds(666, 74, 189, 45);
//		panel_4.add(ButtonEnterPayment);
//		
//		//=======================Enter Paid=======================
//		JButton ButtonPaid = new JButton("Enter Paid");
//		ButtonPaid.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				
//				int paid = (int)tableFines.getValueAt(tableFines.getSelectedRow(), 2);
//				String Card_no = (String)tableFines.getValueAt(tableFines.getSelectedRow(), 0);
//				
//				if(paid == 0)
//					mysqlOperation.AlterPaid(Card_no);
//				else
//					JOptionPane.showMessageDialog(null, "This book has been paid!");
//			}
//		});
//		ButtonPaid.setForeground(Color.BLUE);
//		ButtonPaid.setFont(new Font("微软雅黑", Font.BOLD, 14));
//		ButtonPaid.setBounds(528, 60, 118, 62);
//		panel_4.add(ButtonPaid);
//		
//		textCardNumber = new JTextField();
//		textCardNumber.setBounds(142, 60, 118, 23);
//		panel_4.add(textCardNumber);
//		textCardNumber.setColumns(10);
//		
//		JLabel LabelCardNumber = new JLabel("Card No. :");
//		LabelCardNumber.setFont(new Font("微软雅黑", Font.BOLD, 13));
//		LabelCardNumber.setBounds(142, 40, 91, 15);
//		panel_4.add(LabelCardNumber);
//		
//		CheckBoxFilterOutPaid = new JCheckBox("Filter Out Paid Fines");
//		CheckBoxFilterOutPaid.setForeground(new Color(0, 128, 0));
//		CheckBoxFilterOutPaid.setFont(new Font("微软雅黑", Font.BOLD, 11));
//		CheckBoxFilterOutPaid.setBounds(142, 96, 139, 23);
//		panel_4.add(CheckBoxFilterOutPaid);
		
		
		//=======================tabbedPane重要初始配置=======================
//		tabbedPane.remove(panel_1);
//		tabbedPane.remove(panel_2);
//		tabbedPane.remove(panel_3);
//		tabbedPane.remove(panel_4);
	}
}
