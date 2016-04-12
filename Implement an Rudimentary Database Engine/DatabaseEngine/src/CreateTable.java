import java.io.File;
import java.io.RandomAccessFile;

/** example: 
create schema zoo_schema;
use zoo_schema;

CREATE TABLE zoo (
	id int primary key,
	name varchar(25),
	quantity short int,
	probability float
);
insert into table zoo values(1, "bird1", 110, 12.5);
 */
public class CreateTable {

	//public static void create_table(String command, String TableName)
	//@SuppressWarnings("resource")
	public static void create_table(String[] substr)
	{
		/** ################################# 预处理 #################################
		 * 这部分主要是获取finalString，即截取第一个"("和最后一个")"之间的内容
		 */
		String newCommand = ""; // 转换成一个字符串
		for(int i=0; i<substr.length; i++)
			newCommand += substr[i]+" ";
		newCommand = newCommand.trim(); //去除最后一个空格！！！
		
		int position = newCommand.indexOf("("); //获取第一个"("的位置
		String frontString = newCommand.substring(0, position);
		//System.out.println(frontString.trim());
		String[] frontStr = frontString.trim().split(" ");
		if (position == -1 | newCommand.indexOf(")") == -1 | frontStr.length > 3){
			System.out.println("The database system can not recognize this command!");
			return;
		}
		String finalString = newCommand.substring(position+1, newCommand.length()-1); //截取第一个"("和最后一个")"之间的内容
		//System.out.println(finalString.trim());
		
		String TableName = frontStr[2].trim(); // 提取这个TableName很重要！
		
		// ####################### 检查当前Schema是否存在要创建的Table #######################
		String tablePath = "./data/" + DatabaseEngine.currentSchema + "/" + TableName + ".dat";
		File dirFile = new File(tablePath);
		if(dirFile.exists() == true){
			System.out.println("Created Failed! The table name conflicts with another table name in current schema!");
			return;
		}
		
		// ################################# #################################
		String[] dataType1 = {"BYTE","SHORT","SHORT INT","INT","LONG","LONG INT","FLOAT","DOUBLE","DATETIME","DATE"};
		//String[] dataType2 = {"CHAR","VARCHAR"};
		
		String[] str = finalString.trim().split(",");
		
		for(int i=0; i<str.length; i++)
		{
			String[] part = str[i].trim().split(" ");
			
			// #################################第一部分：check输入格式是否出错#################################
			if(part.length>5 | part.length<=1) {
				System.out.println("Error1: Fail to create! The '"+str[i]+"' is wrong!");
				return;
			}
			//检查大于5或小于等于1的情况，和长度大于3的情况下，最后两位是否是"not null"或者是"primary key"
			if(part.length > 3 && !((part[part.length-2].equalsIgnoreCase("not") && part[part.length-1].equalsIgnoreCase("null")) |
						(part[part.length-2].equalsIgnoreCase("primary") && part[part.length-1].equalsIgnoreCase("key"))))
			{
				System.out.println("Error0: Fail to create! The '"+str[i]+"' is wrong!");
				return;
			}
			if(part.length == 5 | part.length == 3) { //length为3或5时，只需要检测part[1]和part[2]即可
				//第2和3位肯定是"short int" 或者 "long int", 否者报错
				if(!((part[1].equalsIgnoreCase("short") && part[2].equalsIgnoreCase("int")) 
						| (part[1].equalsIgnoreCase("long") && part[2].equalsIgnoreCase("int"))))
				{
					System.out.println("Error2: Fail to create! The '"+str[i]+"' is wrong!");
					return;
				}
				// ...此处正常操作
				//System.out.println("Create Table successfully!");
			}
			else if(part.length == 4 | part.length == 2) //length为2或4时，只需要检测part[1]即可
			{
				if( !(part[1].toLowerCase().contains("char(")==true |
						part[1].toLowerCase().contains("varchar(")==true))
				{
					int j;
					// check是否中间这个数是dataType中一员否者报错
					for(j=0; j<dataType1.length; j++){
						if( part[1].equalsIgnoreCase(dataType1[j]))
								break;
					}
					if(j >= dataType1.length){
						System.out.println("Error3: Fail to create! The '"+str[i]+"' is wrong!");
						return;
					}
				}
				// ...此处正常操作
				//System.out.println("Create successfully!");
			}else{
				System.out.println("Error4: Fail to create! The '"+str[i]+"' is wrong!");
			}
			
			// #######################第二部分：把创建表格内容写入系统schema的Tables#######################
			String TYPE = "";
			String ISNULL = "";
			String KEY = ""; //这个key定义时，必须先为空串""
			
			// =======预处理一下=======
			if(part.length == 5 | part.length == 4) {
				if(part.length == 5)
					TYPE = part[1].trim() + " " + part[2].trim();
				else
					TYPE = part[1].trim();
				ISNULL = "NO";
				if(part[part.length-1].equalsIgnoreCase("key")) //说明是not null，不是primary key
					 KEY = "PRI";
			}
			else if(part.length == 3 | part.length == 2) {
				if(part.length == 3)
					TYPE = part[1].trim() + " " + part[2].trim();
				else
					TYPE = part[1].trim();
				ISNULL = "YES";
				//KEY = "";	//这个key，之前已经定义为空串""
			}

			String SCHEMA = DatabaseEngine.currentSchema;
			String TABLE = TableName;
			String COLUMN_NAME = part[0];
			int POS = i+1;
			
			/** 这是CreateTable命令的最核心的语句
			 * SCHEMA, TABLE, COLUMN_NAME, POS, TYPE, ISNULL, KEY 
			 **/
			baseFun.Write_COLUMNS(SCHEMA, TABLE, COLUMN_NAME, POS, TYPE, ISNULL, KEY);
			
			//System.out.println(SCHEMA+" "+TABLE+" "+COLUMN_NAME+" "+POS+" "+TYPE+" "+ISNULL+" "+KEY);
			//Write_COLUMNS("information_schema", "SCHEMATA", "SCHEMA_NAME", 1, "varchar(64)", "NO", "");
		}
		/** 这是CreateTable命令的重要语句之一 */
		baseFun.Write_TABLES(DatabaseEngine.currentSchema, TableName, (long) 0);
		//System.out.println(DatabaseEngine.currentSchema+" "+TableName+" "+0);
		
		// ####################### 当前目录下创建相应的dat和ndx的空文件 #######################
		String path = "./data/" + DatabaseEngine.currentSchema + "/";
		try {
			RandomAccessFile dat = new RandomAccessFile(path + TableName + ".dat", "rw");
			//RandomAccessFile ndx = new RandomAccessFile(path+TableName+COLUMNS+".dat", "rw");
			dat.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println("Create Table successfully!");
	}
}
