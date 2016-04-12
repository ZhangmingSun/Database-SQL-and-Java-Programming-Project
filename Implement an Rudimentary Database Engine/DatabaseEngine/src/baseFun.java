
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/** ################### 泛型T的泛型化Comparable使用 ###################
 * <T extends Comparable>这种形式非常重要！！！，compareTo方法是来自Comparable的
 * a > b 是a.compareTo(b) > 0, 
 * a = b 是a.compareTo(b) == 0, 
 * a < b 是a.compareTo(b) < 0 
 * */
@SuppressWarnings("rawtypes")
class compare<T extends Comparable>
{	
	/*public compare(String operator) { //析构函数
		  this.operator = operator;
	}*/
	
	@SuppressWarnings("unchecked")
	public boolean compareTwoNum(T t1, T t2, String operator) {
		if(operator.equals(">"))
		{
			if (t1.compareTo(t2) > 0) // t1 > t2
				return true;
			else 
				return false;
		}
		else if(operator.equals("="))
		{
			if( t1.compareTo(t2) == 0) // t1 = t2
				return true;
			else 
				return false;				
		}
		else if(operator.equals("<"))
		{
			if( t1.compareTo(t2) < 0) // t1 < t2
				return true;
			else 
				return false;				
		}
		else if(operator.equals(">="))
		{
			if( t1.compareTo(t2) >= 0) // t1 >= t2
				return true;
			else 
				return false;				
		}
		else if(operator.equals("<="))
		{
			if( t1.compareTo(t2) <= 0) // t1 <= t2
				return true;
			else
				return false;		
		}
		else if(operator.equals("<>") | operator.equals("!="))
		{
			if( t1.compareTo(t2) != 0) // t1 <> t2
				return true;
			else 
				return false;				
		}
		else
			return false;
	}
	
	/*//可以对Comparable的接口函数compareTo进行重写，实现不同功能
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}*/
}
	
public class baseFun {
	
	/** 在select最终显示时，把“20160204050709”转换成“2016-02-04_05:07:09” */
	public static String Convert_Long_To_DATETIME(String str)
	{
		//StringBuilder str = new StringBuilder();
		String tmp = str.substring(0,4)+"-"+str.substring(4,6)+"-"+str.substring(6,8)+"_"+
				str.substring(8,10)+":"+str.substring(10,12)+":"+str.substring(12,14);
		return tmp;
	}
	
	/** 在select最终显示时，把“20160204”转换成“2016-02-04” */
	public static String Convert_Long_To_DATE(String str)
	{
		String tmp = str.substring(0,4)+"-"+str.substring(4,6)+"-"+str.substring(6,8);
		return tmp;
	}
	
	/** This function is very important! It helps to read varchar() data*/
	public static String read_varchar(RandomAccessFile pointer)
	{
		try {
			byte len = pointer.readByte();
			StringBuilder str = new StringBuilder();
			for(int j = 0; j < len; j++)
				str.append((char)pointer.readByte());
			return str.toString();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	/** Remove the specified symbols in the input string. 
	 *  For example: String inputStr = "2016-03-23_13:52:23";
	 *  For example: String[] symbols = {"-","_",":"};
	 **/
	public static String removeSymbols(String inputStr, String[] symbols) {
		for(int i = 0; i < symbols.length; i++)
			inputStr = inputStr.replace(symbols[i], "");
		return inputStr;
	}
	
	/** 往系统的information_schema的TABLES表中写入一行数据 */
	public static void Write_TABLES(String TABLE_SCHEMA, String TABLE_NAME, Long TABLE_ROWS)
	{
		try {
			String path = "./data/information_schema/";
			RandomAccessFile dat = new RandomAccessFile(path+"TABLES.dat", "rw");
			dat.seek(dat.length()); //很重要，把指针定位到最后
			long Starting_Address = dat.getFilePointer(); //注意一定需要保存首地址，每列ndx都指向这个地址
			
			RandomAccessFile SCHEMA_ndx = new RandomAccessFile(path+"TABLES.TABLE_SCHEMA.ndx", "rw");
			RandomAccessFile NAME_ndx = new RandomAccessFile(path+"TABLES.TABLE_NAME.ndx", "rw");
			RandomAccessFile ROWS_ndx = new RandomAccessFile(path+"TABLES.TABLE_ROWS.ndx", "rw");
			SCHEMA_ndx.seek(SCHEMA_ndx.length());	//很重要，把指针定位到最后
			NAME_ndx.seek(NAME_ndx.length());	//很重要，把指针定位到最后
			ROWS_ndx.seek(ROWS_ndx.length());	//很重要，把指针定位到最后
			
			SCHEMA_ndx.writeByte(TABLE_SCHEMA.length());
			SCHEMA_ndx.writeBytes(TABLE_SCHEMA);
			SCHEMA_ndx.writeLong(Starting_Address);
			NAME_ndx.writeByte(TABLE_NAME.length());
			NAME_ndx.writeBytes(TABLE_NAME);
			NAME_ndx.writeLong(Starting_Address);
			ROWS_ndx.writeLong(TABLE_ROWS);
			ROWS_ndx.writeLong(Starting_Address);
			//System.out.println("TABLE_NAME dat.getFilePointer(): " + dat.getFilePointer());
			
			dat.writeByte(TABLE_SCHEMA.length());
			dat.writeBytes(TABLE_SCHEMA);
			dat.writeByte(TABLE_NAME.length());
			dat.writeBytes(TABLE_NAME);
			dat.writeLong(TABLE_ROWS);
			
			SCHEMA_ndx.close();
			NAME_ndx.close();
			ROWS_ndx.close();
			dat.close();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/** 往系统的information_schema的COLUMNS表中写入一行数据 */
	public static void Write_COLUMNS(String SCHEMA, String TABLE, String COLUMN_NAME, int POS, String TYPE, String ISNULL, String KEY)
	{
		try {
			String path = "./data/information_schema/";
			RandomAccessFile dat = new RandomAccessFile(path+"COLUMNS.dat", "rw");
			dat.seek(dat.length()); //很重要，把指针定位到最后
			long Starting_Address = dat.getFilePointer(); //注意一定需要保存首地址，每列ndx都指向这个地址
			
			String[] columnName = {"TABLE_SCHEMA","TABLE_NAME","COLUMN_NAME","ORDINAL_POSITION","COLUMN_TYPE","IS_NULLABLE","COLUMN_KEY"};
			RandomAccessFile[] ndx = new RandomAccessFile[7]; //开辟一小块内存，存放7个指针，注意只是指针，别想多了
			for(int i=0; i<columnName.length; i++) {
				ndx[i] = new RandomAccessFile(path+"COLUMNS."+columnName[i]+".ndx", "rw");
				ndx[i].seek(ndx[i].length());
			}
			
			ndx[0].writeByte(SCHEMA.length());  //1
			ndx[0].writeBytes(SCHEMA);
			ndx[1].writeByte(TABLE.length());	//2
			ndx[1].writeBytes(TABLE);
			ndx[2].writeByte(COLUMN_NAME.length());
			ndx[2].writeBytes(COLUMN_NAME);
			ndx[3].writeInt(POS);
			ndx[4].writeByte(TYPE.length());
			ndx[4].writeBytes(TYPE);
			ndx[5].writeByte(ISNULL.length());
			ndx[5].writeBytes(ISNULL);
			ndx[6].writeByte(KEY.length());
			if(KEY.length() > 0) //我自己猜的，只有长度大于0的KEY才有意义!?
				ndx[6].writeBytes(KEY);
			
			// dat的写入顺序不能改变
			dat.writeByte(SCHEMA.length()); //1
			dat.writeBytes(SCHEMA);
			dat.writeByte(TABLE.length());	//2
			dat.writeBytes(TABLE);
			dat.writeByte(COLUMN_NAME.length());
			dat.writeBytes(COLUMN_NAME);
			dat.writeInt(POS);
			dat.writeByte(TYPE.length());
			dat.writeBytes(TYPE);
			dat.writeByte(ISNULL.length());
			dat.writeBytes(ISNULL);
			dat.writeByte(KEY.length());
			dat.writeBytes(KEY); // <<<<这个地方可能会有问题，当KEY是""空串时，不知道有没有写入‘/0’
			
			for(int i=0; i<columnName.length; i++){
				ndx[i].writeLong(Starting_Address); // <<<<<<<<请注意，在结尾一定要添加Index的地址
				ndx[i].close();
			}
			dat.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/** 从information_schema的COLUMNS表中,
	 * @return List<String>： SelectFactor=0，返回的结果是Table中所有属性的Type，即COLUMN_TYPE 
	 * @return List<String>： SelectFactor=1，返回的结果是Table中所有属性的Name，即COLUMN_NAME */
	public static List<String> read_List_From_COLUMNS(String TABLE_NAME, int SelectFactor)
	{
		try {
			RandomAccessFile SCHEMA_ndx = new RandomAccessFile("./data/information_schema/COLUMNS.TABLE_SCHEMA.ndx", "rw");
			RandomAccessFile COLUMNS_dat = new RandomAccessFile("./data/information_schema/COLUMNS.dat", "rw");
			
			List<String> readResult= new ArrayList<String>();
			
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
					if(tableName.equalsIgnoreCase(TABLE_NAME) == true) //TABLE_NAME相等
					{
						//读取COLUMN_NAME
						String COLUMN_NAME = read_varchar(COLUMNS_dat);
						
						//读取ORDINAL_POSITION
						//int POSITION = COLUMNS_dat.readInt();
						COLUMNS_dat.readInt();
						
						//读取COLUMN_TYPE
						String COLUMN_TYPE = baseFun.read_varchar(COLUMNS_dat);
						if(SelectFactor==0)
							readResult.add(COLUMN_TYPE); //按顺序存入COLUMN_TYPE！！！
						else
							readResult.add(COLUMN_NAME); //按顺序存入COLUMN_NAME！！！
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
	
	/** Return a name list of all files in the current directory! */
	public static List<String> directoryNameList()
    {
		File directory = new File("./data/" + DatabaseEngine.currentSchema);
		List<String> result = new ArrayList<String>();
		if(directory.exists()) {
			File files[] = directory.listFiles();
			for(File file: files) {
				result.add(file.getName());
			}
		}
		return result;
    }
}
