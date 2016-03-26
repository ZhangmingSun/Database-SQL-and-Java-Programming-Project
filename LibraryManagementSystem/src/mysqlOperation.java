import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import net.proteanit.sql.DbUtils;

public class mysqlOperation {
	//static Connection conn = null;
	//public static ResultSet rs = null;
	public static String password = ""; //��������ʼ��ʱ�ɽ������룡����
	
	public static Connection dbConnector(){
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", password);
			//JOptionPane.showMessageDialog(null, "Connection Succesful!!!");
			return conn;
		} 
		catch(SQLException ex) {
			//System.out.println("Error in connection: " + ex.getMessage());
			JOptionPane.showMessageDialog(null, ex.getMessage());
			return null;
		}
	}
	
	public static boolean MySQLConnectTest(String inputPassword){
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", inputPassword);
			//JOptionPane.showMessageDialog(null, "Connection Succesful!!!");
			return true;
		}
		catch(SQLException ex) {
			//System.out.println("Error in connection: " + ex.getMessage());
			//JOptionPane.showMessageDialog(null, ex.getMessage());
			JOptionPane.showMessageDialog(null, "The input MySQL Password is wrong!\nThe Error Hint: \n"+ex.getMessage());
			return false;
		}
	}
	
	public static ResultSet BookQuery(Connection conn, String ISBN, String title, String Authro){
		ResultSet rs = null;
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("use library;");
			
			//BookQuery = "select Isbn, Title, Authro from BOOKS LIMIT 50;";
			//BookQuery = "select Isbn, Title, Authro from BOOKS WHERE Isbn LIKE '%0195%' AND Title LIKE '%%' AND Authro LIKE '%%';";
			/** 
			 * ע�⣺ÿ�е�ĩβ�ӿո�
			 * ��3�����Ӵ�ƥ�����������
			 * ��4����4��table��JOIN������
			 * ��5�б�ʾֻ��ʾ��copies��Branch
			 * ��6����GROUP��ISBN��������Ҳ�����á�ORDER BY B1.Isbn���Խ����������GROUP���ٶȱ�ORDER�죡����
			 */
			String BookQuery = "SELECT B1.Isbn, Title, Author_name, L.Branch_id, L.Branch_name, no_of_copies, availability " +
					"FROM BOOK AS B1, BOOK_AUTHORS AS B2, BOOK_COPIES AS B3, LIBRARY_BRANCH AS L " +
					"WHERE B1.Isbn LIKE '%"+ISBN+"%' AND Title LIKE '%"+title+"%' AND Author_name LIKE '%"+Authro+"%' " +
					"AND B1.Isbn=B2.Isbn AND B1.Isbn=B3.Book_id AND B3.Branch_id=L.Branch_id " +
					"AND no_of_copies>0 " +
					"GROUP BY B1.Isbn, L.Branch_id ;";
					//"ORDER BY B1.Isbn;";
					
			rs = stmt.executeQuery(BookQuery);
			return rs;
		} 
		catch(SQLException ex) {
			//System.out.println("Error in connection: " + ex.getMessage());
			JOptionPane.showMessageDialog(null, ex.getMessage());
			return null;
		}
	}

	public static ResultSet SearchBeforeCheckingIn(Connection conn, String BookID, String CardNo, String name1, String name2)
	{
		ResultSet rs = null;
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("use library;");
			/** 
			 * ע��1��BOOK_LOANS.* ��ʾֻ��ʾһ�ű��ȫ������
			 * ע��2�������name1��name2֪ʶԭ����name��һ���֣���߼���ֻ����Fname��Lname
			 */
			String CheckingInQuery = "SELECT BOOK_LOANS.* FROM BOOK_LOANS, BORROWER " +
					"WHERE Isbn LIKE '%"+BookID+"%' AND BOOK_LOANS.Card_no LIKE '%"+CardNo+"%' " +
					"AND BOOK_LOANS.Card_no=BORROWER.Card_no " +
					"AND (Fname LIKE '%"+name1+"%' or Fname LIKE '%"+name2+"%' or Lname LIKE '%"+name1+"%' or Lname LIKE '%"+name2+"%'); ";
					
			rs = stmt.executeQuery(CheckingInQuery);
			return rs;
		} 
		catch(SQLException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage());
			return null;
		}
	}
	
	/** checkin����������BOOK_LOANS��������date_in������BOOK_COPIES��availability���Զ���1 */
	// test input: 0515136379  4  ID000981
	public static void CheckingInBooks(String Loan_id, String ISBN, String BranchID)
	{
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", password);
			Statement stmt = conn.createStatement();
			stmt.execute("use library;");
			
			/*=============��ñ���ʱ�� "yyyy-MM-dd" =============*/
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
	        Date d = new Date();
	        String Date_in = sdf.format(d);
	        //JOptionPane.showMessageDialog(null, Date_in);
	        
	        //================Update Date_in Operation================
			String UpdateDateIn = " UPDATE BOOK_LOANS SET  Date_in = '"+Date_in+"' WHERE  Loan_id = "+Loan_id+";";
			stmt.executeUpdate(UpdateDateIn);
			JOptionPane.showMessageDialog(null, "Checking In this book successfully!!!");
			
			//================Update Operation: add number '1' in "availability" in TABLE BOOK_COPIES================
			String Update = " UPDATE BOOK_COPIES SET availability = availability + 1 WHERE Book_id='"+ISBN+"' AND Branch_id="+BranchID+"; ";
			stmt.executeUpdate(Update);

			conn.close();
		} 
		catch(SQLException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage());
		}
	}
	
	// test input: 0515136379  4  ID000981
	public static void CheckingOutBooks(String ISBN, String BranchID, String CardNo)
	{
		ResultSet rs = null;
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", password);
			Statement stmt = conn.createStatement();
			stmt.execute("use library;");
			
			//===================================Check 1===================================
			/** ע�⣺"Date_in=NULL", ��ʾ������黹δ�黹��
			 *  @return: ����BORROWER��δ�黹�鱾������ */
			String MaxBooksCheck = "SELECT COUNT(*) AS count FROM BOOK_LOANS WHERE Card_no='"+CardNo+"' AND Date_in IS NULL; ";
			rs = stmt.executeQuery(MaxBooksCheck); rs.next();
			int cnt1 = Integer.parseInt(rs.getString("count"));
			//JOptionPane.showMessageDialog(null, "Test: δ�黹����Ϊ"+cnt1);
			if(cnt1>=3){
				JOptionPane.showMessageDialog(null, "Each BORROWER is permitted a maximum of 3 BOOK_LOANS!");
				rs.close(); conn.close();
				return;
			}
			
			//===================================Check 2===================================
			/** 1. �ӽ�������У� ��ȡ��֧ΪBranchID����BookIDΪISBN������
			 * 2. ע�����ͬʱ�������µ�3��������Isbn=ISBN��Branch_id=BranchID��Date_in=NULL��ͬʱBranch_id������
			 * ��"Date_in IS NULL" ���� "isnull(Date_in)" ���ж�Date_in�Ƿ���NLL�������� Date_in=NULL*/
			String AvaiableBooksCheck = "SELECT COUNT(*) AS count FROM BOOK_LOANS WHERE Isbn='"+ISBN+"' AND Branch_id="+BranchID+" AND Date_in IS NULL; ";
			rs = stmt.executeQuery(AvaiableBooksCheck); rs.next();
			int cnt2 = Integer.parseInt(rs.getString("count"));
			//JOptionPane.showMessageDialog(null, "Number of Books have been borrowed: "+cnt2);
			
			/** Obtain the Number_of_copies in TABLE BOOK_COPIES*/
			String Number_of_copies = "SELECT No_of_copies AS num FROM BOOK_COPIES where Book_id='"+ISBN+"' AND Branch_id="+BranchID+"; ";
			rs = stmt.executeQuery(Number_of_copies); rs.next();
			int cnt3 = Integer.parseInt(rs.getString("num"));
			//JOptionPane.showMessageDialog(null, "Number_of_opies: "+cnt3);
			
			// ����������ȴ洢���� �������ȣ��򱨴�
			if(cnt2 >= cnt3){
				JOptionPane.showMessageDialog(null, "There are no more book copies available at this library_branch!");
				rs.close(); conn.close();
				return;
			}
			
			// DELETE FROM BOOK_LOANS WHERE Isbn='0515136379' AND Branch_id=4; //���г������
			// SELECT * FROM BOOK_LOANS WHERE Isbn='0515136379' AND Branch_id=4;
			// ���ݣ� 409	0515136379	4	ID000981	2016-01-10	2016-01-23	2016-01-24
			// INSERT INTO BOOK_LOANS(Loan_id, Isbn, Branch_id, Card_no, Date_out, Due_date, Date_in) VALUES (409, '0515136379', 4, 'ID000981', '2016-01-10', '2016-01-23', '2016-01-24');
			// INSERT INTO BOOK_LOANS(Isbn, Branch_id, Card_no, Date_out, Due_date, Date_in) VALUES ('0515136379', 4, 'ID000981', '2016-03-18', '2016-04-01', '2016-01-24');
			
			/*===================================Insert Operation===================================*/
			/*=============ʱ�䷽��1������� 2016-1-9������ 2016-01-09���ָ�ʽ=============
			Calendar now = Calendar.getInstance();
			String Date_out = now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DAY_OF_MONTH);
			//JOptionPane.showMessageDialog(null, Date_out);
			
			now.set(Calendar.DATE,now.get(Calendar.DATE)+14); //���ôӵ�ǰʱ�俪ʼ�ĵ�14��
			String Due_date = now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DAY_OF_MONTH);
			//JOptionPane.showMessageDialog(null, Due_date);
	        */
			/*=============ʱ�䷽��2����ȷ=============*/
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
	        Date d = new Date();  
	        String Date_out = sdf.format(d);
	        //JOptionPane.showMessageDialog(null, Date_out);
	        
	        Calendar now = Calendar.getInstance();
	        now.set(Calendar.DATE,now.get(Calendar.DATE)+14); //���ôӵ�ǰʱ�俪ʼ�ĵ�14��
	        String Due_date = sdf.format(now.getTime());
	        //JOptionPane.showMessageDialog(null, Due_date);

			String Insert = " INSERT INTO BOOK_LOANS(Isbn, Branch_id, Card_no, Date_out, Due_date, Date_in) " +
					"VALUES ('"+ISBN+"', "+BranchID+", '"+CardNo+"', '"+Date_out+"', '"+Due_date+"', NULL);";
			stmt.executeUpdate(Insert);
			JOptionPane.showMessageDialog(null, "Checking Out this book successfully!!!");
			
			//================Update Operation: Reduce number '1' in "availability" in TABLE BOOK_COPIES================
			// UPDATE BOOK_COPIES SET availability = availability + 1 WHERE Book_id='0515136379' AND Branch_id=4; //���г������
			String Update = " UPDATE BOOK_COPIES SET availability = availability - 1 WHERE Book_id='"+ISBN+"' AND Branch_id="+BranchID+"; ";
			stmt.executeUpdate(Update);
			/**/
			rs.close();
			conn.close();
		} 
		catch(SQLException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage());
		}
	}

	// SSN: 051513637
	public static void CreateNewAccount(String[] infoList)
	{
		//========================SZM�ľ�����ģ�飺input parameters checking!========================
		String[] nameList = {"SSN", "Fname", "Lname", "Street", "City", "State"}; // Phone is optional!
		for (int i = 0; i < nameList.length; i++)
			if(infoList[i].length() == 0){
				JOptionPane.showMessageDialog(null, "The '"+nameList[i]+"' is NULL!");
				return;
			}
		// ==========First, check SSN number Length. If correct, it will convert it to other format.==========
		// '972756100' ====> '972-75-6100'
		if(infoList[0].length() > 0){ //The number length of SSN should be 9!!!
			String regEx="[^0-9]";   
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(infoList[0]);
			String ssn = m.replaceAll("").trim(); //������SSN��ֻ��������
			if(ssn.length() != 9) {
				JOptionPane.showMessageDialog(null, "The number length of SSN is not equal to 9, please check it!");
				return;
			}
			// '972756100' ====> '972-75-6100'
			infoList[0] = ssn.substring(0, 3) + "-" + ssn.substring(3, 5) + "-" + ssn.substring(5, 9);
			//JOptionPane.showMessageDialog(null, "The input ssn is " + infoList[0]);			
		}
		
		// ==========if you input phone number, it will check and convert it to certain format.==========
		if(infoList[6].length() > 0 ) // check phone number
		{
			String regEx="[^0-9]";   
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(infoList[6]);
			String phone = m.replaceAll("").trim(); //������phone number��ֻ��������
			if(phone.length() != 10) {
				JOptionPane.showMessageDialog(null, "The length of phone number is not equal to 10, please check it!");
				return;
			}
			// ��phone number��9�����ִ���� (214) 917-8414 �ĸ�ʽ���磺substring(0, 3)��ȡǰ3����ĸ, 0����ǰ��һ��λ��
			infoList[6] = "(" + phone.substring(0, 3) + ") " + phone.substring(3, 6) + "-" + phone.substring(6, 10);
			//JOptionPane.showMessageDialog(null, "The input phone number is " + infoList[6]);
		}

		ResultSet rs = null;
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", password);
			Statement stmt = conn.createStatement();
			stmt.execute("use library;");
			
			/*================Build Card Number!!! ================*/
			String maxCardNumber = "SELECT MAX(Card_no) as maxCardNumber FROM BORROWER;";
			rs = stmt.executeQuery(maxCardNumber); rs.next();
			String tmp = rs.getString("maxCardNumber");
			String CardNumberStr = tmp.substring(tmp.length()-6, tmp.length()); //��ȡ��6λ�ַ���
			int CardNum = Integer.parseInt(CardNumberStr) + 1; //ת�����������ټ�1
			String BuildCardNo = "ID";
			int tmpInt = 100000; //��ʼ��
			for (int i = 1; i <= 6; i++) { //�ݹ۲죬CardNo.�����ֲ�����6λ
				BuildCardNo += Integer.toString(CardNum / tmpInt);
				CardNum = CardNum % tmpInt;
				tmpInt = tmpInt/10;
			}
			
			// SELECT COUNT(Ssn) AS ssn FROM BORROWER WHERE Ssn = '972-75-6100';
			/*================Check SSN Number in BORROWER TEBLE!!! ================*/
			String checkSSN = "SELECT COUNT(Ssn) AS ssnCnt FROM BORROWER WHERE Ssn = '"+infoList[0]+"';";
			rs = stmt.executeQuery(checkSSN); rs.next();
			String ssnCnt = rs.getString("ssnCnt");
			if(Integer.parseInt(ssnCnt) > 0) {
				JOptionPane.showMessageDialog(null, "SSN is repeated! Borrowers are allowed to possess exactly one library card!");
				return;
			}
			
			/*================Insert New User Data================*/
			// Card_no, ssn, first_name, last_name, email, address, city, state, phone
			// BuildCardNo, infoList[0], infoList[1], infoList[2], NULL, infoList[3], infoList[4], infoList[5], infoList[6]
			String InsertNewUserData = " INSERT INTO BORROWER " +
					"VALUES ('"+BuildCardNo+"', '"+infoList[0]+"', '"+infoList[1]+"', '"+infoList[2]+"', NULL, " +
							"'"+infoList[3]+"', '"+infoList[4]+"', '"+infoList[5]+"', '"+infoList[6]+"' );";
			stmt.executeUpdate(InsertNewUserData);
			// SELECT * FROM BORROWER WHERE Card_no = 'ID001001'; //����ʵ�ʲ�ѯ��֤
			// DELETE FROM BORROWER WHERE Card_no = 'ID001001'; //����ɾ��

			JOptionPane.showMessageDialog(null, "Create Your Account Sucessully! Please note: Your Card No. is " + BuildCardNo);
			rs.close();
			conn.close();
		}
		catch(SQLException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage());
		}
	}
	
	public static int FinesTableUpdatedDisplay(JTable tableFines, String CardNum, boolean FilterOutPaid)
	{
		ResultSet rs = null;
		try {
			Connection conn  = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", password);
			Statement stmt = conn.createStatement();
			stmt.execute("use library;");
			//JOptionPane.showMessageDialog(null, "Successfully use library ");
			
			/**=====================��1����FINES��paid=0����Ҫ����Fine Amount=======================*/
			// ǧ��ע�⣺ÿ��������һ����Ҫ�Ӹ��ո񣡣�����������ȻSQLһ����ִ�б�������������
			// Due_Date < date(now()) �� Due_Date < Date_in ���������������Ƕ���ģ�����ȡ��Ҳû����
			// "AND Due_Date < date(now()) " ���� "AND Due_Date < Date_in"
			String ObtainUpdateList = "(SELECT FINES.Loan_id, DATEDIFF(date(now()), Due_Date)*0.25 AS new_fine_amt " +
					"FROM (FINES JOIN BOOK_LOANS ON FINES.Loan_id = BOOK_LOANS.Loan_id) " +
					"WHERE paid=0 AND Date_in IS NULL AND fine_amt <> DATEDIFF(date(now()), Due_Date)*0.25) " +
					"UNION " +
					"(SELECT FINES.Loan_id, DATEDIFF(Date_in, Due_Date)*0.25 AS new_fine_amt " +
					"FROM (FINES JOIN BOOK_LOANS ON BOOK_LOANS.Loan_id = FINES.Loan_id) " +
					"WHERE paid=0 AND Date_in IS NOT NULL AND fine_amt <> DATEDIFF(Date_in, Due_Date)*0.25); ";
			
			rs = stmt.executeQuery(ObtainUpdateList);
			//JOptionPane.showMessageDialog(null, "Successfully Obtain the UpdateList");
			
			// ����Ҫ��һ����ά������л��棬����ֱ����while�и��£��򱨴� ��operation is not allowed after resultset is closed��
			List<ArrayList<String>> StrList = new ArrayList<ArrayList<String>>();
			StrList.add(new ArrayList<String>()); //һ����Ҫ�����һ������
			StrList.add(new ArrayList<String>()); 
			while (rs.next()) {
				String LoanID = rs.getString("Loan_id");
				String newFineAmt = rs.getString("new_fine_amt");
				StrList.get(0).add(LoanID);
				StrList.get(1).add(newFineAmt);
				//String Update = "UPDATE FINES SET fine_amt = "+newFineAmt+" WHERE Loan_id = '"+LoanID+"'; ";
				//stmt.executeUpdate(Update);
			}
			for(int i=0; i<StrList.get(0).size(); i++) {
				String Update = "UPDATE FINES SET fine_amt = "+StrList.get(1).get(i)+" WHERE Loan_id = '"+StrList.get(0).get(i)+"'; ";
				stmt.executeUpdate(Update);
			}

			
			/**===========�ⲽ������ӵģ�ɾ��Fines����FineAmountΪ����tuple�������ɸı��������ʱ���������===========*/
			String delete = "DELETE FROM FINES WHERE fine_amt < 0;"; //��Ϊ�ڸ��·����¼��ʱ�򣬼����������������Ҫ����Ϊ0
			stmt.executeUpdate(delete);
			
			/**===========��2������FINES����û�е�tuple(����book_loans��)���뵽FINES����Щtuple��paidȫ��0===========*/
			String Insert = 
					"Insert into FINES(Loan_id, fine_amt, paid) " +
					"(select BOOK_LOANS.Loan_id, DATEDIFF(date(now()), Due_Date)*0.25, 0 from BOOK_LOANS " +
					"WHERE Date_in IS NULL AND Due_Date < date(now()) AND " +
					"NOT EXISTS (SELECT * FROM FINES WHERE BOOK_LOANS.Loan_id = FINES.Loan_id)) " +
					"UNION " +
					"(select BOOK_LOANS.Loan_id, DATEDIFF(Date_in, Due_Date)*0.25, 0 from BOOK_LOANS " +
					"WHERE Date_in IS NOT NULL AND Due_Date < Date_in AND " +
					"NOT EXISTS (SELECT * FROM FINES WHERE BOOK_LOANS.Loan_id = FINES.Loan_id)); ";
			stmt.executeUpdate(Insert);
			
			//JOptionPane.showMessageDialog(null, "Successfully updates/refreshes entries in the FINES table!");
			
			/**===========��3�����Ѹ��º��FINES table��ʾ����Ļ===========*/
			String FinesDisplay = "";
			//FinesDisplay = "select Card_no, FINES.*,due_date, date_in from FINES JOIN BOOK_LOANS ON BOOK_LOANS.Loan_id = FINES.Loan_id Order by Card_no; ";
			
			if(CardNum.equals("") && FilterOutPaid == false)
				//FinesDisplay = "select Card_no, FINES.* from FINES JOIN BOOK_LOANS ON BOOK_LOANS.Loan_id = FINES.Loan_id; ";
				FinesDisplay = "select Card_no, SUM(fine_amt) AS SUM_Fine_Amount, paid " +
						"from FINES JOIN BOOK_LOANS ON BOOK_LOANS.Loan_id = FINES.Loan_id GROUP BY Card_no; ";
			else if(CardNum.length()>0 && FilterOutPaid == false)
				FinesDisplay = "select Card_no, SUM(fine_amt) AS SUM_Fine_Amount, paid " +
						"from FINES JOIN BOOK_LOANS ON BOOK_LOANS.Loan_id = FINES.Loan_id " +
						"WHERE Card_no = '"+CardNum+"' GROUP BY Card_no; ";
			else if(CardNum.equals("") && FilterOutPaid == true)
					FinesDisplay = "select Card_no, SUM(fine_amt) AS SUM_Fine_Amount, paid " +
							"from FINES JOIN BOOK_LOANS ON BOOK_LOANS.Loan_id = FINES.Loan_id " +
							"WHERE paid = 0 GROUP BY Card_no; ";
			else if(CardNum.length()>0 && FilterOutPaid == true)
					FinesDisplay = "select Card_no, SUM(fine_amt) AS SUM_Fine_Amount, paid " +
							"from FINES JOIN BOOK_LOANS ON BOOK_LOANS.Loan_id = FINES.Loan_id " +
							"WHERE paid = 0 AND Card_no = '"+CardNum+"' GROUP BY Card_no; ";
			/**/
			rs = stmt.executeQuery(FinesDisplay);
			
			/** Display "Search Result Number" */
			int rowcount = 0;
			if (rs.last()) {
			  rowcount = rs.getRow();
			  rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
			}
			else rowcount = 0;
			
			tableFines.setModel(DbUtils.resultSetToTableModel(rs));
			
			rs.close();
			conn.close();
			return rowcount;
		} 
		catch(SQLException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage());
			return 0;
		}
		// UPDATE BOOK_LOANS SET Date_in = "2016-03-18" WHERE Loan_id = '675';
		// DELETE FROM FINES WHERE Loan_id = 671;
		//select FINES.* from FINES JOIN BOOK_LOANS ON BOOK_LOANS.Loan_id = FINES.Loan_id WHERE Card_no='ID000053';
		//UPDATE FINES SET fine_amt = 6.25 WHERE Loan_id = 189;
	}

	public static void AlterPaid(String Card_no)
	{
		ResultSet rs = null;
		try {
			Connection conn  = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", password);
			Statement stmt = conn.createStatement();
			stmt.execute("use library;");
			
			// ��������� ��Date_in IS NULL�� ��ʾ��鱻������鼮�Ƿ�ȫ�����Ѿ�Check In��
			String isAllCheckIn = "SELECT COUNT(FINES.Loan_id) AS cnt " +
					"FROM BOOK_LOANS JOIN FINES ON BOOK_LOANS.Loan_id = FINES.Loan_id " +
					"WHERE Card_no = '"+Card_no+"' AND paid = 0 AND Date_in IS NULL;";
			rs = stmt.executeQuery(isAllCheckIn); rs.next();
			String cnt = rs.getString("cnt");
			if(Integer.parseInt(cnt) > 0){
				JOptionPane.showMessageDialog(null, "Failed, Your fined Books are still not checked in!");
				return;
			}
			
			// ����paid������JOIN Tables���в�ѯ������������Card_noƥ�� �� paid = 0
			String CardNoPaidQuery = "SELECT FINES.Loan_id " +
					"FROM BOOK_LOANS JOIN FINES ON BOOK_LOANS.Loan_id = FINES.Loan_id " +
					"WHERE Card_no = '"+Card_no+"' AND paid = 0;";
			rs = stmt.executeQuery(CardNoPaidQuery);
			// ����Ҫ��һ��������л��棬����ֱ����whileѭ���и��£������ ��operation is not allowed after resultset is closed��
			List<String> StrTemp = new ArrayList<String>();
			while (rs.next()) {
				String Loan_id = rs.getString("Loan_id");
				StrTemp.add(Loan_id);
			}
			for(int i=0; i<StrTemp.size(); i++) {
				String Update = "UPDATE FINES SET paid = 1 WHERE Loan_id = "+StrTemp.get(i)+"; ";
				stmt.executeUpdate(Update);
			}
			
			JOptionPane.showMessageDialog(null, " Set Paid=1 Successfully!");
			rs.close();
			conn.close();
		} 
		catch(SQLException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage());
		}		
	}
	
	public static void UpdateFINESRecord(String Card_no, double EnterPayment)
	{
		ResultSet rs = null;
		try {
			Connection conn  = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", password);
			Statement stmt = conn.createStatement();
			stmt.execute("use library;");
			
			// ����paid������JOIN Tables���в�ѯ������������Card_noƥ�� �� paid = 1
			String CardNoPaidQuery = "SELECT FINES.Loan_id " +
					"FROM BOOK_LOANS JOIN FINES ON BOOK_LOANS.Loan_id = FINES.Loan_id " +
					"WHERE Card_no = '"+Card_no+"' AND paid = 1;";
			rs = stmt.executeQuery(CardNoPaidQuery);
			
			// ����Ҫ��һ��������л��棬����ֱ����whileѭ���и��£������ ��operation is not allowed after resultset is closed��
			List<String> StrTemp = new ArrayList<String>();
			while (rs.next()) {
				String Loan_id = rs.getString("Loan_id");
				StrTemp.add(Loan_id);
			}
			
			//JOptionPane.showMessageDialog(null, "EnterPayment"+EnterPayment); //<<<���в��ԣ�����Ϊ��������˴��󣡣���
			// �ѵ�һ��tuple�� fine_amt = EnterPayment����ʣ���ȫ������Ϊ0
			stmt.executeUpdate("UPDATE FINES SET fine_amt = "+EnterPayment+" WHERE Loan_id = "+StrTemp.get(0)+"; ");
			// Note: Index�����1��ʼ
			for(int i=1; i<StrTemp.size(); i++)
				stmt.executeUpdate("UPDATE FINES SET fine_amt = 0 WHERE Loan_id = "+StrTemp.get(i)+"; ");
	
			JOptionPane.showMessageDialog(null, "Update FINES Record Successfully!");
			rs.close();
			conn.close();
		} 
		catch(SQLException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage());
		}		
	}

}
