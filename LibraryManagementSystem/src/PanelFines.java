import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;


public class PanelFines extends JLayeredPane {
	private static final long serialVersionUID = 1L;
	
	private JTable tableFines;
	private JTextField textCardNumber;
	private JCheckBox CheckBoxFilterOutPaid;
	private JLabel labelFinesNum;
	
	public PanelFines() {
		JScrollPane scrollPaneFine = new JScrollPane();
		scrollPaneFine.setBounds(142, 139, 713, 318);
		add(scrollPaneFine);
		
		tableFines = new JTable();
		scrollPaneFine.setViewportView(tableFines);
		
		labelFinesNum = new JLabel("");
		labelFinesNum.setBounds(682, 467, 173, 15);
		add(labelFinesNum);
		
		//=======================Refresh Fines=======================
		JButton ButtonRefreshFines = new JButton("Update/Refresh FINES");
		ButtonRefreshFines.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// >>>>>> card_no中包含747的选项含有两次违规记录，可能会被用来测试
				
				String CardNum = textCardNumber.getText().trim();
				if(CardNum.length()>0 && CardNum.length()!=8)
					JOptionPane.showMessageDialog(null, "The length of Card No. is not equal to 8 !!!");
				
				boolean FilterOutPaid = CheckBoxFilterOutPaid.isSelected(); //没选中返回false，选中返回true

				int cnt = mysqlOperation.FinesTableUpdatedDisplay(tableFines, CardNum, FilterOutPaid);
				labelFinesNum.setText("Search Result Number: " + cnt);
			}
		});
		ButtonRefreshFines.setForeground(Color.BLUE);
		ButtonRefreshFines.setFont(new Font("微软雅黑", Font.BOLD, 14));
		ButtonRefreshFines.setBounds(300, 74, 200, 45);
		add(ButtonRefreshFines);
		
		//=======================Enter Payment=======================
		JButton ButtonEnterPayment = new JButton("Update FINES Record");
		ButtonEnterPayment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				//BigDecimal EnterPayment = new BigDecimal("0.00");
				String Card_no = (String)tableFines.getValueAt(tableFines.getSelectedRow(), 0);
				int paid = (int)tableFines.getValueAt(tableFines.getSelectedRow(), 2);
				
				//=========很关键，tableFines默认返回是double，但是修改后返回是String，所以使用Object类型=========
				Object ob = (Object)tableFines.getValueAt(tableFines.getSelectedRow(), 1);
				double EnterPayment = Double.parseDouble(ob.toString());
				//JOptionPane.showMessageDialog(null, EnterPayment); return;
				
				if(paid == 0)
					JOptionPane.showMessageDialog(null, "Because paid=FALSE, Fail to Update!");	
				else
					mysqlOperation.UpdateFINESRecord(Card_no, EnterPayment);/**/		
			}
		});
		ButtonEnterPayment.setForeground(Color.BLUE);
		ButtonEnterPayment.setFont(new Font("微软雅黑", Font.BOLD, 13));
		ButtonEnterPayment.setBounds(666, 74, 189, 45);
		add(ButtonEnterPayment);
		
		//=======================Enter Paid=======================
		JButton ButtonPaid = new JButton("Enter Paid");
		ButtonPaid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int paid = (int)tableFines.getValueAt(tableFines.getSelectedRow(), 2);
				String Card_no = (String)tableFines.getValueAt(tableFines.getSelectedRow(), 0);
				
				if(paid == 0)
					mysqlOperation.AlterPaid(Card_no);
				else
					JOptionPane.showMessageDialog(null, "This book has been paid!");
			}
		});
		ButtonPaid.setForeground(Color.BLUE);
		ButtonPaid.setFont(new Font("微软雅黑", Font.BOLD, 14));
		ButtonPaid.setBounds(528, 60, 118, 62);
		add(ButtonPaid);
		
		textCardNumber = new JTextField();
		textCardNumber.setBounds(142, 60, 118, 23);
		add(textCardNumber);
		textCardNumber.setColumns(10);
		
		JLabel LabelCardNumber = new JLabel("Card No. :");
		LabelCardNumber.setFont(new Font("微软雅黑", Font.BOLD, 13));
		LabelCardNumber.setBounds(142, 40, 91, 15);
		add(LabelCardNumber);
		
		CheckBoxFilterOutPaid = new JCheckBox("Filter Out Paid Fines");
		CheckBoxFilterOutPaid.setForeground(new Color(0, 128, 0));
		CheckBoxFilterOutPaid.setFont(new Font("微软雅黑", Font.BOLD, 11));
		CheckBoxFilterOutPaid.setBounds(142, 96, 139, 23);
		add(CheckBoxFilterOutPaid);
	}
}
