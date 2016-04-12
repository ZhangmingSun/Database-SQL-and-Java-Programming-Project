import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class SelectCommand {

	public static void SelectFromTable(String command)
	{
		String[] operatorType = {">","=","<",">=","<=","<>","!="};
		/** ����Ҫ��Ԥ������">"�ӿո���" > " */
		for(int i=0; i<operatorType.length; i++) {
			if(command.contains(operatorType[i]) == true)
				command = command.replace(operatorType[i], " "+operatorType[i]+" ");
		}
		//System.out.println("command: "+command); //�鿴�޸ĺ������
		
		String[] pre = command.split("\\s+");
		//for(int i=0; i<pre.length; i++) System.out.print(pre[i]+" "); System.out.print("\n");
		String[] str;
		/** ����жϷǳ���Ҫ�����������ж��ַ����к��пո񣡱��磺WHERE name='Jason Day' */
		if(pre.length > 8) {
			str = new String[8];
			for(int i=0; i<=7; i++) str[i] = pre[i]; //������Ϊǰ8����Ԫ��ֵ
			for(int i=8; i<pre.length; i++)
				str[7] = str[7]+" "+pre[i]; //�ϲ�������֮����ַ�����ע����ӿո�
		}else {
			str = command.split("\\s+");
		}
		
		int len = str.length;
		for(int i=0; i<len; i++)
			str[i] = str[i].trim(); //�����ո�
		
		String tableName = str[3]; //��ϵͳ�У�str[3]һ����tableName
		
		//========== check table whether or not Existing In Current Schema ==========
		boolean tableCheck = isExistingTableInCurrentSchema(tableName);
		if(tableCheck == false) 
			return;

		//################# ִ��select-from���� #################
		if(len == 4) {
			Exe_Select_From_Cmd(tableName);
		}
		
		//################# ִ��select-from-where���� #################
		if(len > 4)
		{
			//============= Checking format and length =============
			if( len < 8 | str[4].equalsIgnoreCase("where") == false) {
				System.out.println("Failed! The command is error! Please check it!");
				return;
			}
			//============= Checking Operator!!! =============
			int cnt = 0;
			for(int i=0; i<operatorType.length; i++)
				if(str[6].equals(operatorType[i]) == false)
					cnt++;
			if(cnt == operatorType.length) {
				System.out.println("Failed! The operator '"+str[6]+"' is not qualified!");
				return;
			}
			
			//============= �ж��е���������ColumnName: str[5]�Ƿ��� =============
			List<String> NameList = baseFun.read_List_From_COLUMNS(tableName,1); // 1--��ʾ����columName List
			boolean isColumnName = CHeckingColumnName(str[5], NameList);
			if(isColumnName == false) {
				System.out.println("Failed! The '"+str[5]+"' is not in Table's attribute list!");
				return;
			}
				
			//============= Checking �ַ��������ݵ��ϱ���Ƿ�һ�� =============
			// ȡ��str[5]--columnName��columnType
			String[] tmp = read_columnType_From_COLUMNS(str[3], str[5]); //str[3]--tableName; str[5]--columnName
			String columnType = tmp[1];
			if(columnType.toLowerCase().contains("char")) {
				if( str[7].contains("\'")==false  && str[7].contains("\"")==false ){
					System.out.println("Failed! the TYPE of \" "+str[7]+" \" is not matching the String TYPE!");
					return;
				}
				String[] symbols = {"\'","\""};
				str[7] = baseFun.removeSymbols(str[7], symbols); //ȥ�������в���صķ���
			}
			else if(columnType.equalsIgnoreCase("DATE") | columnType.equalsIgnoreCase("DATETIME")) {
				String[] symbols = {"-","_",":","\'","\""};
				str[7] = baseFun.removeSymbols(str[7], symbols); //ȥ�������в���صķ���
			}
			else {
				if( str[7].contains("\'")==true  | str[7].contains("\"")==true) {
					System.out.println("Failed! The condision is uncorrect!");
					return;
				}
			}
			//System.out.println("str[7] = " + str[7]);
			//���������ǣ�String tableName, String column_name, String operator, int value/long date
			Exe_Select_From_Where_Cmd(str[3], str[5], str[6], str[7]);
			
		}
	} 
	
	//============= �ж��е���������ColumnName: str[5]�Ƿ��� =============
	public static boolean CHeckingColumnName(String columnName, List<String> NameList)
	{
		for(int i=0; i<NameList.size(); i++) {
			if(columnName.equalsIgnoreCase(NameList.get(i)))
				return true;
		}
		return false;
	}
	
	public static void Exe_Select_From_Where_Cmd(String tableName, String columnName, String operator, String value)
	{
		//String[] operatorType = {">","=","<",">=","<="};
		try {
			List<String> TypeList = new ArrayList<String>();
			TypeList = baseFun.read_List_From_COLUMNS(tableName,0);
			
			String tablePath = "./data/" + DatabaseEngine.currentSchema + "/" + tableName + ".dat";
			RandomAccessFile dat = new RandomAccessFile(tablePath, "rw");
			
			String columnPath = "./data/" + DatabaseEngine.currentSchema + "/" + tableName +"."+ columnName +".ndx";
    		File dir = new File(columnPath);
    		if(dir.exists() == true)	//�����Ӧ��"tableName.column_name.ndx"����
    		{
    			RandomAccessFile ndx = new RandomAccessFile(columnPath, "rw");
    			
    			/** ��ϵͳCOLUMNS���У�����columnName��������ȡ��columnType��originalPosition */
    			String[] tmp = read_columnType_From_COLUMNS(tableName, columnName);
    			int originalPosition = Integer.parseInt(tmp[0]); //originalPosition������������index��Ҫ��1
    			String columnType = tmp[1];
    			
    			/** ����Ľ�ֹ������ Zhangming Sunԭ�� */
    			while(ndx.getFilePointer() < ndx.length())
    			{
    				readDataOneTime(columnType, ndx);	//��һ�����ݣ�Ŀ��ֻ��Ϊ���ƶ�ָ��
    				long address = ndx.readLong(); 		//address����һ���������ڵ�ָ��
    				
    				/** �����׵�ַaddress����.dat�ļ��ж�ȡһ���е�ַ */
    				dat.seek(address);	//�л�����һ���׵�ַ��
    				String[] OneRowData = new String[TypeList.size()];
    				for(int i = 0; i < TypeList.size(); i++) {
    					/** �������Ҫ�����ݲ�ͬ���������ͣ���.dat�ļ��ж�ȡһ������*/
    					OneRowData[i] = readDataOneTime(TypeList.get(i), dat);
    				}
    				
    				/** Executing Operator to compare! */
    				String m1 = OneRowData[originalPosition-1]; // read from .dat by the help from .ndx
    				String m2 = value; // it is provided by user
    				boolean feedback = ExecutingOperator(operator, m1, m2, columnType); //�Ƚ�m1��m2
    				
    				/** display the one whole row data */
    				if(feedback == true) {
        				for(int i = 0; i < OneRowData.length; i++) // TypeList.size() = OneRowData.length
        				{
        					/** ��ʾǰ�������ڸ�ʽ����ת�� */
        					if(TypeList.get(i).equalsIgnoreCase("DATE")) {
        						OneRowData[i] = baseFun.Convert_Long_To_DATE(OneRowData[i]);
        					}
        					if(TypeList.get(i).equalsIgnoreCase("DATETIME")) {
        						OneRowData[i] = baseFun.Convert_Long_To_DATETIME(OneRowData[i]);
        					}
        					System.out.print(OneRowData[i] + "\t");
        				}
        				System.out.print("\n");
    				}
    			}
    			ndx.close();
    		}
    		dat.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			//e.printStackTrace();
		}
	}
	
	/** Judge whether or not Table is existing under the current schema */
	public static boolean isExistingTableInCurrentSchema(String tableName)
	{
		String tablePath = "./data/" + DatabaseEngine.currentSchema + "/" + tableName + ".dat";
		File dir = new File(tablePath);
		if(dir.exists() == false)	//�����Ӧ��"tableName.dat"������
		{
			System.out.println("Failed! the Table \""+tableName+"\" does not exist in current schema!");
			return false;
		}
		return true;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean ExecutingOperator(String operator, String m1, String m2, String dataType)
	{
		//String[] operatorType = {">","=","<",">=","<=","<>","!="};
		
		//VARCHAR �� CHAR ͬʱ����"CHAR"������"BYTE"������ͬʱ�����ַ����Ƚ�
		if(dataType.toUpperCase().contains("CHAR") == true | dataType.equalsIgnoreCase("BYTE")){
			compare<String> cmp = new compare();
			return cmp.compareTwoNum(m1, m2, operator);
		}
		else if(dataType.equalsIgnoreCase("INT")){
			int cmp1 = Integer.parseInt(m1);
			int cmp2 = Integer.parseInt(m2);
			compare<Integer> cmp = new compare();
			return cmp.compareTwoNum(cmp1, cmp2, operator);
		}
		else if(dataType.equalsIgnoreCase("SHORT") | dataType.equalsIgnoreCase("SHORT INT")){
			Short cmp1 = Short.parseShort(m1);
			Short cmp2 = Short.parseShort(m2);
			compare<Short> cmp = new compare();
			return cmp.compareTwoNum(cmp1, cmp2, operator);
		}
		else if(dataType.equalsIgnoreCase("LONG") | dataType.equalsIgnoreCase("LONG INT") |
				dataType.equalsIgnoreCase("DATETIME") | dataType.equalsIgnoreCase("DATE")){
			long cmp1 = Long.parseLong(m1);
			long cmp2 = Long.parseLong(m2);
			compare<Long> cmp = new compare();
			return cmp.compareTwoNum(cmp1, cmp2, operator);
		}
		else if(dataType.equalsIgnoreCase("FLOAT")){
			float cmp1 = Float.parseFloat(m1);
			float cmp2 = Float.parseFloat(m2);
			compare<Float> cmp = new compare();
			return cmp.compareTwoNum(cmp1, cmp2, operator);
		}
		else if(dataType.equalsIgnoreCase("DOUBLE")){
			double cmp1 = Double.parseDouble(m1);
			double cmp2 = Double.parseDouble(m2);
			compare<Double> cmp = new compare();
			return cmp.compareTwoNum(cmp1, cmp2, operator);
		}
		else{}
		return true;
	}
	
	public static void Exe_Select_From_Cmd(String tableName)
	{
		try {
			List<String> TypeList = new ArrayList<String>();
			TypeList = baseFun.read_List_From_COLUMNS(tableName,0);
			
			String tablePath = "./data/" + DatabaseEngine.currentSchema + "/" + tableName + ".dat";
			RandomAccessFile dat = new RandomAccessFile(tablePath, "rw");
			
			/** ����Ľ�ֹ������ Zhangming Sunԭ��*/
			while(dat.getFilePointer() < dat.length())
			{
				for(int i = 0; i < TypeList.size(); i++)
				{
					/** �������Ҫ�����ݲ�ͬ���������ͣ���.dat�ļ��ж�ȡһ������*/
					String oneData = readDataOneTime(TypeList.get(i), dat);
					
					/** ��ʾǰ�������ڸ�ʽ����ת�� */
					if(TypeList.get(i).equalsIgnoreCase("DATE")) {
						oneData = baseFun.Convert_Long_To_DATE(oneData);
					}
					if(TypeList.get(i).equalsIgnoreCase("DATETIME")) {
						oneData = baseFun.Convert_Long_To_DATETIME(oneData);
					}
					
					/** ע��: �����á�|�����ߡ�\t�����з��� */
					System.out.print(oneData + "\t");
				}
				System.out.println(""); //���У�����
			}
			dat.close(); //һ��Ҫ�رգ���Ȼ ��drop table XXX;�����������
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/** This function is very important! �����������ͣ���.dat��.ndx����ָ�봦��һ������ */
	public static String readDataOneTime(String dataType, RandomAccessFile dat)
	{
		String result = null;
		try {
			if(dataType.toUpperCase().contains("VARCHAR") == true){
				result = baseFun.read_varchar(dat);
			}
			else if(dataType.toUpperCase().contains("CHAR") == true){
				String typeStr = dataType;
				String tmp = typeStr.substring(5, typeStr.length()-1); //��ȡ���ȣ�����"char(64)"�е�"64"
				int length = Integer.parseInt(tmp);
				StringBuilder str = new StringBuilder();
				for(int j = 0; j < length; j++)
					str.append((char)dat.readByte());
				result = str.toString();
			}
			else if(dataType.equalsIgnoreCase("BYTE")){
				//System.out.print((char)dat.readByte());
				result = ""+dat.readByte();
			}
			else if(dataType.equalsIgnoreCase("INT")){
				//System.out.print(dat.readInt());
				result = ""+dat.readInt();
			}
			else if(dataType.equalsIgnoreCase("SHORT") | dataType.equalsIgnoreCase("SHORT INT")){
				//System.out.print(dat.readShort());
				result = ""+dat.readShort();
			}
			else if(dataType.equalsIgnoreCase("LONG") | dataType.equalsIgnoreCase("LONG INT") |
					dataType.equalsIgnoreCase("DATETIME") | dataType.equalsIgnoreCase("DATE")){
				//System.out.print(dat.readLong());
				result = ""+dat.readLong();
			}
			else if(dataType.equalsIgnoreCase("FLOAT")){
				//System.out.print(dat.readFloat());
				result = ""+dat.readFloat();
			}
			else if(dataType.equalsIgnoreCase("DOUBLE")){
				//System.out.print(dat.readDouble());
				result = ""+dat.readDouble();
			}
			else{}
			return result;
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	/** ���ص���һ��columnType �� ��original position*/
	public static String[] read_columnType_From_COLUMNS(String TABLE_NAME, String COLUMN_NAME)
	{
		try {
			RandomAccessFile SCHEMA_ndx = new RandomAccessFile("./data/information_schema/COLUMNS.TABLE_SCHEMA.ndx", "rw");
			RandomAccessFile COLUMNS_dat = new RandomAccessFile("./data/information_schema/COLUMNS.dat", "rw");
			
			String[] readResult = new String[2];
			
			/** ����Ľ�ֹ������ SCHEMA_ndx.getFilePointer()<SCHEMA_ndx.length() ̫������Լ��ˣ�����*/
			while(SCHEMA_ndx.getFilePointer() < SCHEMA_ndx.length())
			{
				//��ȡCOLUMNS.TABLE_SCHEMA.ndx�е�TABLE_SCHEMA
				String tableSchema = baseFun.read_varchar(SCHEMA_ndx);
				long address = SCHEMA_ndx.readLong(); //���������������һ��
				if(tableSchema.equalsIgnoreCase(DatabaseEngine.currentSchema) == true) //��currentSchema���
				{
					COLUMNS_dat.seek(address);
					baseFun.read_varchar(COLUMNS_dat); //��ȡTABLE_SCHEMA���������������û�ã�ֻ��Ϊ��ָ���ƶ�
					//��ȡTABLE_NAME
					String tableName = baseFun.read_varchar(COLUMNS_dat);
					if(tableName.equalsIgnoreCase(TABLE_NAME) == true) //TABLE_NAMEƥ��
					{
						//��ȡCOLUMN_NAME
						String columnName = baseFun.read_varchar(COLUMNS_dat);
						if(columnName.equalsIgnoreCase(COLUMN_NAME) == true) //COLUMN_NAMEƥ��
						{
							//��ȡORDINAL_POSITION
							int ORDINAL_POSITION = COLUMNS_dat.readInt(); 
							//��ȡCOLUMN_TYPE
							String COLUMN_TYPE = baseFun.read_varchar(COLUMNS_dat);
							
							readResult[0] = Integer.toString(ORDINAL_POSITION); //ת����String
							readResult[1] = COLUMN_TYPE;
							break; //ֱ������ѭ��
						}
					}
				}
			}
			SCHEMA_ndx.close();
			COLUMNS_dat.close();
			return readResult;
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			//e.printStackTrace();
		}
		return null;
	}

}
