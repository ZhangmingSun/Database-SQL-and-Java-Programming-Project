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
		//======================检查当前Schema是否存在要插入的Table======================
		String tablePath = "./data/" + DatabaseEngine.currentSchema + "/" + tableName + ".dat";
		File dirFile = new File(tablePath);
		if(!dirFile.exists()){
			System.out.println("Failed! The table '"+ tableName +"' does not exist in the current schema!");
			return;
		}
		
		//======================Checking keyword "VALUES"======================
		int position = command.indexOf("("); //获取第一个"("的位置
		String values1 = command.substring(position-6, position); // check keyword "VALUES"
		String values2 = command.substring(position-7, position);
		//System.out.println("values1： " + values1.trim());
		//System.out.println("values2： " + values2.trim());
		if(values1.trim().equalsIgnoreCase("values")==false && values2.trim().equalsIgnoreCase("values")==false)
		{
			System.out.println("The database system can not recognize this command!");
			return;
		}
		
		//======================提取insert括号内的内容======================
		String insertDataStr = command.substring(position+1, command.length()-1).trim();
		//System.out.println("insertDataStr=" + insertDataStr);
		
		List<String> TypeList = new ArrayList<String>();
		TypeList = baseFun.read_List_From_COLUMNS(tableName,0);
		String[] str = insertDataStr.split(","); // <<<<<<注意此处用 “,” 分离insert括号内的内容
		/** 如果输入值的个数大于属性的个数，则报错！*/
		if(str.length != TypeList.size()){
			System.out.println("The Insert Number of Data is Wrong! Please check it.");
			return;
		}
		
		//==========消除空格和字符串中的上引号==========
		for(int i=0; i<str.length; i++) {
			str[i] = str[i].trim(); //消除空格
			String[] symbols = {"\'","\""};
			str[i] = baseFun.removeSymbols(str[i], symbols); //消除字符串中的上引号
		}
		
		for(int i=0; i<TypeList.size(); i++) {
			
			//==========扫描检测输入日期的格式==========
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
			//==========扫描检测char(n)的长度是否一致==========
			else if(TypeList.get(i).toUpperCase().contains("CHAR")==true 
					&& TypeList.get(i).toUpperCase().contains("VARCHAR")==false)
			{
				String length = TypeList.get(i).substring(5, TypeList.get(i).length()-1); //比如提取"char(64)"中的"64"
				if(str[i].length() > Integer.parseInt(length)) {
					System.out.println("Failed! The length of '"+str[i]+"' is larger than the length in TYPE "+TypeList.get(i));
					return;
				}
			}
			//==========扫描检测BYTE的长度是否超出范围==========
			else if(TypeList.get(i).equalsIgnoreCase("BYTE")==true){
				if((long)Long.parseLong(str[i]) > (long)127) { //因为Byte的长度是
					System.out.println("Failed! The length of '"+str[i]+"' is larger than the length of BYTE");
					return;
				}
			}
		}
		//==========扫描检测Primary Key是否重复，比如说ID，SSN等==========
		String[] keyInfo = obtainPrimaryKeyInfo(tableName);
		//根据keyInfo的OriginalPosition，从str[i]数组中获得插入的ID值
		int originalPosition = Integer.parseInt(keyInfo[1]);
		String value = str[originalPosition-1]; //Note: 一定需要“originalPosition-1”
		
		boolean isKeyConflict = CheckingPrimaryKey(tableName, keyInfo, value);
		if(isKeyConflict == false) {
			System.out.println("Failed! The input primary key '"+value+"' conflicts with another primary key in Table.");
			return;
		}
		
		/** ################################# 开始插入数据 #################################
		{"BYTE","SHORT","SHORT INT","INT","LONG","LONG INT","FLOAT","DOUBLE","DATETIME","DATE"} {"CHAR","VARCHAR"}
		//String tablePath = "./data/" + DatabaseEngine.currentSchema + "/" + tableName + ".dat";*/
		try {
			RandomAccessFile dat = new RandomAccessFile(tablePath, "rw");
			dat.seek(dat.length()); //注意把指针指向末尾
			long Starting_Address = dat.getFilePointer(); //注意一定需要保存首地址，每列ndx都指向这个地址
			//System.out.println("Starting_Address = "+Starting_Address);
			
			for(int i=0; i<str.length; i++) //str[i]是需要插入的数据
			{
				/** 千万注意：ORDINAL_POSITION是从1开始，而i是从0开始，故一定要加1 */
				int column_position = i + 1;
				String[] tmp = read_From_COLUMNS(DatabaseEngine.currentSchema, tableName, column_position);
				String columnName = tmp[0];
				String dataType = tmp[1];
				//System.out.println("COLLUMN_NAME = "+columnName+"; Type = "+dataType);
				
				String columnPath = "./data/" + DatabaseEngine.currentSchema + "/" + tableName +"."+ columnName +".ndx";
				RandomAccessFile ndx = new RandomAccessFile(columnPath, "rw");
				ndx.seek(ndx.length()); //非常重要！！！注意把指针指向末尾

				//============== Insert .dat and ndx ==============
				//{"BYTE","SHORT","SHORT INT","INT","LONG","LONG INT","FLOAT","DOUBLE","DATETIME","DATE"}
				/** 千万注意：因为"VARCHAR"包含"CHAR"，所以"VARCHAR"的顺序一定要比"CHAR"靠前 */
				if(dataType.toUpperCase().contains("VARCHAR") == true) {
					dat.writeByte(str[i].length());
					dat.writeBytes(str[i]);
					ndx.writeByte(str[i].length());
					ndx.writeBytes(str[i]);
					//ndx.writeLong(Starting_Address);
				}
				else if(dataType.toUpperCase().contains("CHAR") == true)
				{
					String length = dataType.substring(5, dataType.length()-1); //比如提取"char(64)"中的"64"
					dat.writeBytes(str[i]); // 先把str[i]写进去
					ndx.writeBytes(str[i]);
					for(int j = str[i].length(); j < Integer.parseInt(length); j++) // str[i]长度不够，不全"0x00"
					{
						dat.writeByte(0x00); //一个字节一个字节的写...
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
				
				/** 请注意，在结尾一定要添加Index的地址*/
				ndx.writeLong(Starting_Address);
				
				ndx.close(); //注意：一定要关闭
				//System.out.println("Insert str["+i+"]: " + str[i]);
			}
			dat.close(); //注意：一定要关闭
			
			/** #################### 插完数据后，需要更新TABLES中的TABLE_ROWS+1 ####################*/
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
			//。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
			
			System.out.println("Insert Successfully!" );
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/** 从系统表中读取Primary Key的信息：
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
			
			/** 经典的截止条件！ SCHEMA_ndx.getFilePointer()<SCHEMA_ndx.length() 太佩服我自己了，哈！*/
			while(SCHEMA_ndx.getFilePointer() < SCHEMA_ndx.length())
			{
				//读取COLUMNS.TABLE_SCHEMA.ndx中的TABLE_SCHEMA
				String tableSchema = baseFun.read_varchar(SCHEMA_ndx);
				long address = SCHEMA_ndx.readLong(); //这两条语句必须绑定在一起
				if(tableSchema.equalsIgnoreCase(DatabaseEngine.currentSchema) == true) //与currentSchema相等
				{
					COLUMNS_dat.seek(address);
					baseFun.read_varchar(COLUMNS_dat); //读取TABLE_SCHEMA，这读出来的数据没用，只是为了指针移动
					//读取TABLE_NAME
					String tableName = baseFun.read_varchar(COLUMNS_dat);
					if(tableName.equalsIgnoreCase(TABLE_NAME) == true) //TABLE_NAME匹配
					{
						/** #######################需要修改的代码，其他部分都类似####################### */
						//读取COLUMN_NAME
						String COLUMN_NAME = baseFun.read_varchar(COLUMNS_dat);
						//读取ORDINAL_POSITION
						int ORDINAL_POSITION = COLUMNS_dat.readInt();
						//读取COLUMN_TYPE
						String COLUMN_TYPE = baseFun.read_varchar(COLUMNS_dat);
						//读取IS_NULLABLE
						baseFun.read_varchar(COLUMNS_dat); //没用，只是为了移动指针坐标
						//读取COLUMN_KEY
						String primaryKey = baseFun.read_varchar(COLUMNS_dat); //
						
						if(primaryKey.equalsIgnoreCase("PRI") == true) //说明是primaryKey
						{
							readResult[0] = COLUMN_NAME;
							readResult[1] = Integer.toString(ORDINAL_POSITION); //转换成String
							readResult[2] = COLUMN_TYPE;
							break; //直接跳出循环
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
	
	/** CheckingPrimaryKey函数就是Exe_Select_From_Where_Cmd函数的翻版，
	 * 因为它就是拿一个value去跟“.dat文件”中每一条数据中的对应值进行比较 
	 * @param value: 这是插入数据的primary key值，需要跟.dat文件中数据进行对比
	 * */
	public static boolean CheckingPrimaryKey(String tableName, String[] prameters, String value)
	{
		//String[] operatorType = {">","=","<",">=","<=","<>","!="};
		try {
			String operator = "<>"; //执行不想等的比较，如果遇到相等的情况，则返回false
			String columnName = prameters[0];
			int originalPosition = Integer.parseInt(prameters[1]);
			String columnType = prameters[2];
			
			List<String> TypeList = new ArrayList<String>();
			TypeList = baseFun.read_List_From_COLUMNS(tableName,0);
			
			String tablePath = "./data/" + DatabaseEngine.currentSchema + "/" + tableName + ".dat";
			RandomAccessFile dat = new RandomAccessFile(tablePath, "rw");
			String columnPath = "./data/" + DatabaseEngine.currentSchema + "/" + tableName +"."+ columnName +".ndx";
    		File dir = new File(columnPath);
    		if(dir.exists() == true)	//如果相应的"tableName.column_name.ndx"存在
    		{
    			RandomAccessFile ndx = new RandomAccessFile(columnPath, "rw");
    			
    			/** 经典的截止条件！ Zhangming Sun原创 */
    			while(ndx.getFilePointer() < ndx.length())
    			{
    				SelectCommand.readDataOneTime(columnType, ndx);	//读一次数据，目的只是为了移动指针
    				long address = ndx.readLong(); 		//address是这一行数据所在的指针
    				
    				/** 根据首地址address，从.dat文件中读取一整行地址 */
    				dat.seek(address);	//切换到这一行首地址处
    				String[] OneRowData = new String[TypeList.size()];
    				for(int i = 0; i < TypeList.size(); i++) {
    					/** 这个很重要：根据不同的数据类型，从.dat文件中读取一次数据*/
    					OneRowData[i] = SelectCommand.readDataOneTime(TypeList.get(i), dat);
    				}
    				
    				/** Executing Operator to compare! */
    				String m1 = OneRowData[originalPosition-1]; // read from .dat by the help from .ndx
    				String m2 = value; // it is provided by user
    				boolean feedback = SelectCommand.ExecutingOperator(operator, m1, m2, columnType); //比较m1和m2
    				if(feedback == false) //说明遇到了相等的primary key，需要报错
    				{
    					dat.close(); ndx.close();
    					return false;
    				}
    			}
    			ndx.close();
    			//return true; //“primary key”全都不相等
    		}
			dat.close();
			return true; //说明一条数据都没有被插入
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	
	/** SCHEMA_ndx从头开始遍历, COLUMNS_dat需要从seek的地方开始，顺序读取一行中所需数据
	 *  TABLE_SCHEMA 所需要查找表所在的SCHEMA
	 **/
	public static String[] read_From_COLUMNS(String TABLE_SCHEMA, String TABLE_NAME, int ORDINAL_POSITION)
	{
		try {
			RandomAccessFile SCHEMA_ndx = new RandomAccessFile("./data/information_schema/COLUMNS.TABLE_SCHEMA.ndx", "rw");
			RandomAccessFile COLUMNS_dat = new RandomAccessFile("./data/information_schema/COLUMNS.dat", "rw");
			
			String[] readResult = new String[2]; //返回的结果
			
			boolean recordFound = false;
			while(!recordFound)
			{
				//读取COLUMNS.TABLE_SCHEMA.ndx中的TABLE_SCHEMA
				String tableSchema = baseFun.read_varchar(SCHEMA_ndx);
				long address = SCHEMA_ndx.readLong(); //这两条语句必须绑定在一起
				
				if(tableSchema.equalsIgnoreCase(TABLE_SCHEMA) == true) //表示COLUMNS中shcema属性对应
				{
					COLUMNS_dat.seek(address); 
					baseFun.read_varchar(COLUMNS_dat); //读取TABLE_SCHEMA，这读出来的数据没用，只是为了指针移动
					
					//读取TABLE_NAME
					String tableName = baseFun.read_varchar(COLUMNS_dat);
					if(tableName.equalsIgnoreCase(TABLE_NAME) == true) //TABLE_NAME相等
					{
						//读取COLUMN_NAME
						String COLUMN_NAME = baseFun.read_varchar(COLUMNS_dat);
						//读取ORDINAL_POSITION
						int POSITION = COLUMNS_dat.readInt();
						
						if(ORDINAL_POSITION == POSITION)
						{
							//读取COLUMN_TYPE
							String COLUMN_TYPE = baseFun.read_varchar(COLUMNS_dat);
							
							readResult[0] = COLUMN_NAME; //
							readResult[1] = COLUMN_TYPE;
							recordFound = true; //退出循环！！！
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

