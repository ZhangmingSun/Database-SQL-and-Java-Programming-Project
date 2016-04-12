import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/** example: 
create schema zoo_schema;
use zoo_schema;

CREATE TABLE zoo (
	id int primary key,
	name varchar(25),
	quantity short int,
	probability float
);
insert into zoo values(1, 'bird1', 110, 12.5);
 */
public class InsertTable {
	
	// Test: insert into table XXX values();
	// Test: insert into table XXX values ();
	public static void insert_table(String command, String tableName)
	{
		//======================��鵱ǰSchema�Ƿ����Ҫ�����Table======================
		String tablePath = "./data/" + DatabaseEngine.currentSchema + "/" + tableName + ".dat";
		File dirFile = new File(tablePath);
		if(!dirFile.exists()){
			System.out.println("Failed! The table '"+ tableName +"' does not exist in the current schema!");
			return;
		}
		
		//======================Checking keyword "VALUES"======================
		int position = command.indexOf("("); //��ȡ��һ��"("��λ��
		String values1 = command.substring(position-6, position); // check keyword "VALUES"
		String values2 = command.substring(position-7, position);
		//System.out.println("values1�� " + values1.trim());
		//System.out.println("values2�� " + values2.trim());
		if(values1.trim().equalsIgnoreCase("values")==false && values2.trim().equalsIgnoreCase("values")==false)
		{
			System.out.println("The database system can not recognize this command!");
			return;
		}
		
		//======================��ȡinsert�����ڵ�����======================
		String insertDataStr = command.substring(position+1, command.length()-1).trim();
		//System.out.println("insertDataStr=" + insertDataStr);
		
		List<String> TypeList = new ArrayList<String>();
		TypeList = baseFun.read_List_From_COLUMNS(tableName,0);
		String[] str = insertDataStr.split(","); // <<<<<<ע��˴��� ��,�� ����insert�����ڵ�����
		/** �������ֵ�ĸ����������Եĸ������򱨴�*/
		if(str.length != TypeList.size()){
			System.out.println("The Insert Number of Data is Wrong! Please check it.");
			return;
		}
		
		//==========�����ո���ַ����е�������==========
		for(int i=0; i<str.length; i++) {
			str[i] = str[i].trim(); //�����ո�
			String[] symbols = {"\'","\""};
			str[i] = baseFun.removeSymbols(str[i], symbols); //�����ַ����е�������
		}
		
		for(int i=0; i<TypeList.size(); i++) {
			
			//==========ɨ�����������ڵĸ�ʽ==========
			if(TypeList.get(i).equalsIgnoreCase("DATETIME") | TypeList.get(i).equalsIgnoreCase("DATE")){
				String[] symbols = {"-","_",":"};
				String preStr = str[i];
				str[i] = baseFun.removeSymbols(str[i], symbols);
				if((TypeList.get(i).equalsIgnoreCase("DATETIME") && str[i].length()!=14) |
						(TypeList.get(i).equalsIgnoreCase("DATE") && str[i].length()!=8))
				{
					System.out.println("The input '"+preStr+"' is Wrong! Please check it.");
					return;
				}
			}
			//==========ɨ����char(n)�ĳ����Ƿ�һ��==========
			else if(TypeList.get(i).toUpperCase().contains("CHAR")==true 
					&& TypeList.get(i).toUpperCase().contains("VARCHAR")==false)
			{
				String length = TypeList.get(i).substring(5, TypeList.get(i).length()-1); //������ȡ"char(64)"�е�"64"
				if(str[i].length() > Integer.parseInt(length)) {
					System.out.println("Failed! The length of '"+str[i]+"' is larger than the length in TYPE "+TypeList.get(i));
					return;
				}
			}
			//==========ɨ����BYTE�ĳ����Ƿ񳬳���Χ==========
			else if(TypeList.get(i).equalsIgnoreCase("BYTE")==true){
				if((long)Long.parseLong(str[i]) > (long)127) { //��ΪByte�ĳ�����
					System.out.println("Failed! The length of '"+str[i]+"' is larger than the length of BYTE");
					return;
				}
			}
		}
		//==========ɨ����Primary Key�Ƿ��ظ�������˵ID��SSN��==========
		String[] keyInfo = obtainPrimaryKeyInfo(tableName);
		//����keyInfo��OriginalPosition����str[i]�����л�ò����IDֵ
		int originalPosition = Integer.parseInt(keyInfo[1]);
		String value = str[originalPosition-1]; //Note: һ����Ҫ��originalPosition-1��
		
		boolean isKeyConflict = CheckingPrimaryKey(tableName, keyInfo, value);
		if(isKeyConflict == false) {
			System.out.println("Failed! The input primary key '"+value+"' conflicts with another primary key in Table.");
			return;
		}
		
		/** ################################# ��ʼ�������� #################################
		{"BYTE","SHORT","SHORT INT","INT","LONG","LONG INT","FLOAT","DOUBLE","DATETIME","DATE"} {"CHAR","VARCHAR"}
		//String tablePath = "./data/" + DatabaseEngine.currentSchema + "/" + tableName + ".dat";*/
		try {
			RandomAccessFile dat = new RandomAccessFile(tablePath, "rw");
			dat.seek(dat.length()); //ע���ָ��ָ��ĩβ
			long Starting_Address = dat.getFilePointer(); //ע��һ����Ҫ�����׵�ַ��ÿ��ndx��ָ�������ַ
			//System.out.println("Starting_Address = "+Starting_Address);
			
			for(int i=0; i<str.length; i++) //str[i]����Ҫ���������
			{
				/** ǧ��ע�⣺ORDINAL_POSITION�Ǵ�1��ʼ����i�Ǵ�0��ʼ����һ��Ҫ��1 */
				int column_position = i + 1;
				String[] tmp = read_From_COLUMNS(DatabaseEngine.currentSchema, tableName, column_position);
				String columnName = tmp[0];
				String dataType = tmp[1];
				//System.out.println("COLLUMN_NAME = "+columnName+"; Type = "+dataType);
				
				String columnPath = "./data/" + DatabaseEngine.currentSchema + "/" + tableName +"."+ columnName +".ndx";
				RandomAccessFile ndx = new RandomAccessFile(columnPath, "rw");
				ndx.seek(ndx.length()); //�ǳ���Ҫ������ע���ָ��ָ��ĩβ

				//============== Insert .dat and ndx ==============
				//{"BYTE","SHORT","SHORT INT","INT","LONG","LONG INT","FLOAT","DOUBLE","DATETIME","DATE"}
				/** ǧ��ע�⣺��Ϊ"VARCHAR"����"CHAR"������"VARCHAR"��˳��һ��Ҫ��"CHAR"��ǰ */
				if(dataType.toUpperCase().contains("VARCHAR") == true) {
					dat.writeByte(str[i].length());
					dat.writeBytes(str[i]);
					ndx.writeByte(str[i].length());
					ndx.writeBytes(str[i]);
					//ndx.writeLong(Starting_Address);
				}
				else if(dataType.toUpperCase().contains("CHAR") == true)
				{
					String length = dataType.substring(5, dataType.length()-1); //������ȡ"char(64)"�е�"64"
					dat.writeBytes(str[i]); // �Ȱ�str[i]д��ȥ
					ndx.writeBytes(str[i]);
					for(int j = str[i].length(); j < Integer.parseInt(length); j++) // str[i]���Ȳ�������ȫ"0x00"
					{
						dat.writeByte(0x00); //һ���ֽ�һ���ֽڵ�д...
						ndx.writeByte(0x00);
					}
					//ndx.writeLong(Starting_Address);
				}
				else if(dataType.equalsIgnoreCase("BYTE"))
				{
					dat.writeByte(Byte.parseByte(str[i]));
					ndx.writeByte(Byte.parseByte(str[i]));
					//ndx.writeLong(Starting_Address);
				}
				else if(dataType.equalsIgnoreCase("INT"))
				{
					dat.writeInt(Integer.parseInt(str[i]));
					ndx.writeInt(Integer.parseInt(str[i]));
					//ndx.writeLong(Starting_Address);
				}
				else if(dataType.equalsIgnoreCase("SHORT") | dataType.equalsIgnoreCase("SHORT INT"))
				{
					dat.writeShort((short)Integer.parseInt(str[i]));
					ndx.writeShort((short)Integer.parseInt(str[i]));
					//ndx.writeLong(Starting_Address);
				}
				else if(dataType.equalsIgnoreCase("LONG") | dataType.equalsIgnoreCase("LONG INT"))
				{
					dat.writeLong(Long.parseLong(str[i]));
					ndx.writeLong(Long.parseLong(str[i]));
					//ndx.writeLong(Starting_Address);
				}
				else if(dataType.equalsIgnoreCase("FLOAT"))
				{
					dat.writeFloat(Float.parseFloat(str[i]));
					ndx.writeFloat(Float.parseFloat(str[i]));
					//ndx.writeLong(Starting_Address);
				}
				else if(dataType.equalsIgnoreCase("DOUBLE"))
				{
					dat.writeDouble(Double.parseDouble(str[i]));
					ndx.writeDouble(Double.parseDouble(str[i]));
					//ndx.writeLong(Starting_Address);
				}
				else if(dataType.equalsIgnoreCase("DATETIME") | dataType.equalsIgnoreCase("DATE"))
				{
					String[] symbols = {"-","_",":"};
					str[i] = baseFun.removeSymbols(str[i], symbols);
					dat.writeLong(Long.parseLong(str[i]));
					ndx.writeLong(Long.parseLong(str[i]));
					//ndx.writeLong(Starting_Address);
				}			
				else{}
				
				/** ��ע�⣬�ڽ�βһ��Ҫ���Index�ĵ�ַ*/
				ndx.writeLong(Starting_Address);
				
				ndx.close(); //ע�⣺һ��Ҫ�ر�
				//System.out.println("Insert str["+i+"]: " + str[i]);
			}
			dat.close(); //ע�⣺һ��Ҫ�ر�
			
			/** #################### �������ݺ���Ҫ����TABLES�е�TABLE_ROWS+1 ####################*/
			//��������������������������������������������������������������
			//��������������������������������������������������������������
			//��������������������������������������������������������������
			//��������������������������������������������������������������
			
			System.out.println("Insert Successfully!" );
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/** ��ϵͳ���ж�ȡPrimary Key����Ϣ��
	 * String[0] : COLUMN NAME
	 * String[1] : ORDINAL_POSITION
	 * String[2] : COLUMN_TYPE
	 * */
	public static String[] obtainPrimaryKeyInfo(String TABLE_NAME)
	{
		try {
			RandomAccessFile SCHEMA_ndx = new RandomAccessFile("./data/information_schema/COLUMNS.TABLE_SCHEMA.ndx", "rw");
			RandomAccessFile COLUMNS_dat = new RandomAccessFile("./data/information_schema/COLUMNS.dat", "rw");
			
			String[] readResult = new String[3];
			
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
						/** #######################��Ҫ�޸ĵĴ��룬�������ֶ�����####################### */
						//��ȡCOLUMN_NAME
						String COLUMN_NAME = baseFun.read_varchar(COLUMNS_dat);
						//��ȡORDINAL_POSITION
						int ORDINAL_POSITION = COLUMNS_dat.readInt();
						//��ȡCOLUMN_TYPE
						String COLUMN_TYPE = baseFun.read_varchar(COLUMNS_dat);
						//��ȡIS_NULLABLE
						baseFun.read_varchar(COLUMNS_dat); //û�ã�ֻ��Ϊ���ƶ�ָ������
						//��ȡCOLUMN_KEY
						String primaryKey = baseFun.read_varchar(COLUMNS_dat); //
						
						if(primaryKey.equalsIgnoreCase("PRI") == true) //˵����primaryKey
						{
							readResult[0] = COLUMN_NAME;
							readResult[1] = Integer.toString(ORDINAL_POSITION); //ת����String
							readResult[2] = COLUMN_TYPE;
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
	
	/** CheckingPrimaryKey��������Exe_Select_From_Where_Cmd�����ķ��棬
	 * ��Ϊ��������һ��valueȥ����.dat�ļ�����ÿһ�������еĶ�Ӧֵ���бȽ� 
	 * @param value: ���ǲ������ݵ�primary keyֵ����Ҫ��.dat�ļ������ݽ��жԱ�
	 * */
	public static boolean CheckingPrimaryKey(String tableName, String[] prameters, String value)
	{
		//String[] operatorType = {">","=","<",">=","<=","<>","!="};
		try {
			String operator = "<>"; //ִ�в���ȵıȽϣ����������ȵ�������򷵻�false
			String columnName = prameters[0];
			int originalPosition = Integer.parseInt(prameters[1]);
			String columnType = prameters[2];
			
			List<String> TypeList = new ArrayList<String>();
			TypeList = baseFun.read_List_From_COLUMNS(tableName,0);
			
			String tablePath = "./data/" + DatabaseEngine.currentSchema + "/" + tableName + ".dat";
			RandomAccessFile dat = new RandomAccessFile(tablePath, "rw");
			String columnPath = "./data/" + DatabaseEngine.currentSchema + "/" + tableName +"."+ columnName +".ndx";
    		File dir = new File(columnPath);
    		if(dir.exists() == true)	//�����Ӧ��"tableName.column_name.ndx"����
    		{
    			RandomAccessFile ndx = new RandomAccessFile(columnPath, "rw");
    			
    			/** ����Ľ�ֹ������ Zhangming Sunԭ�� */
    			while(ndx.getFilePointer() < ndx.length())
    			{
    				SelectCommand.readDataOneTime(columnType, ndx);	//��һ�����ݣ�Ŀ��ֻ��Ϊ���ƶ�ָ��
    				long address = ndx.readLong(); 		//address����һ���������ڵ�ָ��
    				
    				/** �����׵�ַaddress����.dat�ļ��ж�ȡһ���е�ַ */
    				dat.seek(address);	//�л�����һ���׵�ַ��
    				String[] OneRowData = new String[TypeList.size()];
    				for(int i = 0; i < TypeList.size(); i++) {
    					/** �������Ҫ�����ݲ�ͬ���������ͣ���.dat�ļ��ж�ȡһ������*/
    					OneRowData[i] = SelectCommand.readDataOneTime(TypeList.get(i), dat);
    				}
    				
    				/** Executing Operator to compare! */
    				String m1 = OneRowData[originalPosition-1]; // read from .dat by the help from .ndx
    				String m2 = value; // it is provided by user
    				boolean feedback = SelectCommand.ExecutingOperator(operator, m1, m2, columnType); //�Ƚ�m1��m2
    				if(feedback == false) //˵����������ȵ�primary key����Ҫ����
    				{
    					dat.close(); ndx.close();
    					return false;
    				}
    			}
    			ndx.close();
    			//return true; //��primary key��ȫ�������
    		}
			dat.close();
			return true; //˵��һ�����ݶ�û�б�����
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	
	/** SCHEMA_ndx��ͷ��ʼ����, COLUMNS_dat��Ҫ��seek�ĵط���ʼ��˳���ȡһ������������
	 *  TABLE_SCHEMA ����Ҫ���ұ����ڵ�SCHEMA
	 **/
	public static String[] read_From_COLUMNS(String TABLE_SCHEMA, String TABLE_NAME, int ORDINAL_POSITION)
	{
		try {
			RandomAccessFile SCHEMA_ndx = new RandomAccessFile("./data/information_schema/COLUMNS.TABLE_SCHEMA.ndx", "rw");
			RandomAccessFile COLUMNS_dat = new RandomAccessFile("./data/information_schema/COLUMNS.dat", "rw");
			
			String[] readResult = new String[2]; //���صĽ��
			
			boolean recordFound = false;
			while(!recordFound)
			{
				//��ȡCOLUMNS.TABLE_SCHEMA.ndx�е�TABLE_SCHEMA
				String tableSchema = baseFun.read_varchar(SCHEMA_ndx);
				long address = SCHEMA_ndx.readLong(); //���������������һ��
				
				if(tableSchema.equalsIgnoreCase(TABLE_SCHEMA) == true) //��ʾCOLUMNS��shcema���Զ�Ӧ
				{
					COLUMNS_dat.seek(address); 
					baseFun.read_varchar(COLUMNS_dat); //��ȡTABLE_SCHEMA���������������û�ã�ֻ��Ϊ��ָ���ƶ�
					
					//��ȡTABLE_NAME
					String tableName = baseFun.read_varchar(COLUMNS_dat);
					if(tableName.equalsIgnoreCase(TABLE_NAME) == true) //TABLE_NAME���
					{
						//��ȡCOLUMN_NAME
						String COLUMN_NAME = baseFun.read_varchar(COLUMNS_dat);
						//��ȡORDINAL_POSITION
						int POSITION = COLUMNS_dat.readInt();
						
						if(ORDINAL_POSITION == POSITION)
						{
							//��ȡCOLUMN_TYPE
							String COLUMN_TYPE = baseFun.read_varchar(COLUMNS_dat);
							
							readResult[0] = COLUMN_NAME; //
							readResult[1] = COLUMN_TYPE;
							recordFound = true; //�˳�ѭ��������
							//System.out.println("dataType="+COLUMN_TYPE+"; columnName="+COLUMN_NAME);
							//return readResult;
						}
					}
				}
			}
			SCHEMA_ndx.close();
			COLUMNS_dat.close();
			return readResult;
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	
}

