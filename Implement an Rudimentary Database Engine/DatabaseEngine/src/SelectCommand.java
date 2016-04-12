import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class SelectCommand {

	public static void SelectFromTable(String command)
	{
		String[] operatorType = {">","=","<",">=","<=","<>","!="};
		/** 很重要的预处理，把">"加空格变成" > " */
		for(int i=0; i<operatorType.length; i++) {
			if(command.contains(operatorType[i]) == true)
				command = command.replace(operatorType[i], " "+operatorType[i]+" ");
		}
		//System.out.println("command: "+command); //查看修改后的命令
		
		String[] pre = command.split("\\s+");
		//for(int i=0; i<pre.length; i++) System.out.print(pre[i]+" "); System.out.print("\n");
		String[] str;
		/** 这个判断非常重要，可能条件判断字符串中含有空格！比如：WHERE name='Jason Day' */
		if(pre.length > 8) {
			str = new String[8];
			for(int i=0; i<=7; i++) str[i] = pre[i]; //必须先为前8个单元赋值
			for(int i=8; i<pre.length; i++)
				str[7] = str[7]+" "+pre[i]; //合并操作符之后的字符串，注意添加空格
		}else {
			str = command.split("\\s+");
		}
		
		int len = str.length;
		for(int i=0; i<len; i++)
			str[i] = str[i].trim(); //消除空格
		
		String tableName = str[3]; //此系统中，str[3]一定是tableName
		
		//========== check table whether or not Existing In Current Schema ==========
		boolean tableCheck = isExistingTableInCurrentSchema(tableName);
		if(tableCheck == false) 
			return;

		//################# 执行select-from命令 #################
		if(len == 4) {
			Exe_Select_From_Cmd(tableName);
		}
		
		//################# 执行select-from-where命令 #################
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
			
			//============= 判断列的属性名字ColumnName: str[5]是否在 =============
			List<String> NameList = baseFun.read_List_From_COLUMNS(tableName,1); // 1--表示返回columName List
			boolean isColumnName = CHeckingColumnName(str[5], NameList);
			if(isColumnName == false) {
				System.out.println("Failed! The '"+str[5]+"' is not in Table's attribute list!");
				return;
			}
				
			//============= Checking 字符输入内容的上标号是否一致 =============
			// 取其str[5]--columnName的columnType
			String[] tmp = read_columnType_From_COLUMNS(str[3], str[5]); //str[3]--tableName; str[5]--columnName
			String columnType = tmp[1];
			if(columnType.toLowerCase().contains("char")) {
				if( str[7].contains("\'")==false  && str[7].contains("\"")==false ){
					System.out.println("Failed! the TYPE of \" "+str[7]+" \" is not matching the String TYPE!");
					return;
				}
				String[] symbols = {"\'","\""};
				str[7] = baseFun.removeSymbols(str[7], symbols); //去掉日期中不相关的符号
			}
			else if(columnType.equalsIgnoreCase("DATE") | columnType.equalsIgnoreCase("DATETIME")) {
				String[] symbols = {"-","_",":","\'","\""};
				str[7] = baseFun.removeSymbols(str[7], symbols); //去掉日期中不相关的符号
			}
			else {
				if( str[7].contains("\'")==true  | str[7].contains("\"")==true) {
					System.out.println("Failed! The condision is uncorrect!");
					return;
				}
			}
			//System.out.println("str[7] = " + str[7]);
			//类型依次是：String tableName, String column_name, String operator, int value/long date
			Exe_Select_From_Where_Cmd(str[3], str[5], str[6], str[7]);
			
		}
	} 
	
	//============= 判断列的属性名字ColumnName: str[5]是否在 =============
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
    		if(dir.exists() == true)	//如果相应的"tableName.column_name.ndx"存在
    		{
    			RandomAccessFile ndx = new RandomAccessFile(columnPath, "rw");
    			
    			/** 在系统COLUMNS表中，根据columnName的名字提取其columnType和originalPosition */
    			String[] tmp = read_columnType_From_COLUMNS(tableName, columnName);
    			int originalPosition = Integer.parseInt(tmp[0]); //originalPosition比正常的数据index都要大1
    			String columnType = tmp[1];
    			
    			/** 经典的截止条件！ Zhangming Sun原创 */
    			while(ndx.getFilePointer() < ndx.length())
    			{
    				readDataOneTime(columnType, ndx);	//读一次数据，目的只是为了移动指针
    				long address = ndx.readLong(); 		//address是这一行数据所在的指针
    				
    				/** 根据首地址address，从.dat文件中读取一整行地址 */
    				dat.seek(address);	//切换到这一行首地址处
    				String[] OneRowData = new String[TypeList.size()];
    				for(int i = 0; i < TypeList.size(); i++) {
    					/** 这个很重要：根据不同的数据类型，从.dat文件中读取一次数据*/
    					OneRowData[i] = readDataOneTime(TypeList.get(i), dat);
    				}
    				
    				/** Executing Operator to compare! */
    				String m1 = OneRowData[originalPosition-1]; // read from .dat by the help from .ndx
    				String m2 = value; // it is provided by user
    				boolean feedback = ExecutingOperator(operator, m1, m2, columnType); //比较m1和m2
    				
    				/** display the one whole row data */
    				if(feedback == true) {
        				for(int i = 0; i < OneRowData.length; i++) // TypeList.size() = OneRowData.length
        				{
        					/** 显示前，对日期格式进行转换 */
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
		if(dir.exists() == false)	//如果相应的"tableName.dat"不存在
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
		
		//VARCHAR 和 CHAR 同时包含"CHAR"，还有"BYTE"，它们同时进行字符串比较
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
			
			/** 经典的截止条件！ Zhangming Sun原创*/
			while(dat.getFilePointer() < dat.length())
			{
				for(int i = 0; i < TypeList.size(); i++)
				{
					/** 这个很重要：根据不同的数据类型，从.dat文件中读取一次数据*/
					String oneData = readDataOneTime(TypeList.get(i), dat);
					
					/** 显示前，对日期格式进行转换 */
					if(TypeList.get(i).equalsIgnoreCase("DATE")) {
						oneData = baseFun.Convert_Long_To_DATE(oneData);
					}
					if(TypeList.get(i).equalsIgnoreCase("DATETIME")) {
						oneData = baseFun.Convert_Long_To_DATETIME(oneData);
					}
					
					/** 注意: 可以用“|”或者“\t”进行分离 */
					System.out.print(oneData + "\t");
				}
				System.out.println(""); //换行！！！
			}
			dat.close(); //一定要关闭，不然 “drop table XXX;”会出错！！！
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/** This function is very important! 根据数据类型，从.dat或.ndx最新指针处读一个数据 */
	public static String readDataOneTime(String dataType, RandomAccessFile dat)
	{
		String result = null;
		try {
			if(dataType.toUpperCase().contains("VARCHAR") == true){
				result = baseFun.read_varchar(dat);
			}
			else if(dataType.toUpperCase().contains("CHAR") == true){
				String typeStr = dataType;
				String tmp = typeStr.substring(5, typeStr.length()-1); //提取长度，比如"char(64)"中的"64"
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

	/** 返回的是一个columnType 和 其original position*/
	public static String[] read_columnType_From_COLUMNS(String TABLE_NAME, String COLUMN_NAME)
	{
		try {
			RandomAccessFile SCHEMA_ndx = new RandomAccessFile("./data/information_schema/COLUMNS.TABLE_SCHEMA.ndx", "rw");
			RandomAccessFile COLUMNS_dat = new RandomAccessFile("./data/information_schema/COLUMNS.dat", "rw");
			
			String[] readResult = new String[2];
			
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
						//读取COLUMN_NAME
						String columnName = baseFun.read_varchar(COLUMNS_dat);
						if(columnName.equalsIgnoreCase(COLUMN_NAME) == true) //COLUMN_NAME匹配
						{
							//读取ORDINAL_POSITION
							int ORDINAL_POSITION = COLUMNS_dat.readInt(); 
							//读取COLUMN_TYPE
							String COLUMN_TYPE = baseFun.read_varchar(COLUMNS_dat);
							
							readResult[0] = Integer.toString(ORDINAL_POSITION); //转换成String
							readResult[1] = COLUMN_TYPE;
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

}
