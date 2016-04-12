
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/** ################### ����T�ķ��ͻ�Comparableʹ�� ###################
 * <T extends Comparable>������ʽ�ǳ���Ҫ��������compareTo����������Comparable��
 * a > b ��a.compareTo(b) > 0, 
 * a = b ��a.compareTo(b) == 0, 
 * a < b ��a.compareTo(b) < 0 
 * */
@SuppressWarnings("rawtypes")
class compare<T extends Comparable>
{	
	/*public compare(String operator) { //��������
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
	
	/*//���Զ�Comparable�Ľӿں���compareTo������д��ʵ�ֲ�ͬ����
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}*/
}
	
public class baseFun {
	
	/** ��select������ʾʱ���ѡ�20160204050709��ת���ɡ�2016-02-04_05:07:09�� */
	public static String Convert_Long_To_DATETIME(String str)
	{
		//StringBuilder str = new StringBuilder();
		String tmp = str.substring(0,4)+"-"+str.substring(4,6)+"-"+str.substring(6,8)+"_"+
				str.substring(8,10)+":"+str.substring(10,12)+":"+str.substring(12,14);
		return tmp;
	}
	
	/** ��select������ʾʱ���ѡ�20160204��ת���ɡ�2016-02-04�� */
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
	
	/** ��ϵͳ��information_schema��TABLES����д��һ������ */
	public static void Write_TABLES(String TABLE_SCHEMA, String TABLE_NAME, Long TABLE_ROWS)
	{
		try {
			String path = "./data/information_schema/";
			RandomAccessFile dat = new RandomAccessFile(path+"TABLES.dat", "rw");
			dat.seek(dat.length()); //����Ҫ����ָ�붨λ�����
			long Starting_Address = dat.getFilePointer(); //ע��һ����Ҫ�����׵�ַ��ÿ��ndx��ָ�������ַ
			
			RandomAccessFile SCHEMA_ndx = new RandomAccessFile(path+"TABLES.TABLE_SCHEMA.ndx", "rw");
			RandomAccessFile NAME_ndx = new RandomAccessFile(path+"TABLES.TABLE_NAME.ndx", "rw");
			RandomAccessFile ROWS_ndx = new RandomAccessFile(path+"TABLES.TABLE_ROWS.ndx", "rw");
			SCHEMA_ndx.seek(SCHEMA_ndx.length());	//����Ҫ����ָ�붨λ�����
			NAME_ndx.seek(NAME_ndx.length());	//����Ҫ����ָ�붨λ�����
			ROWS_ndx.seek(ROWS_ndx.length());	//����Ҫ����ָ�붨λ�����
			
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
	
	/** ��ϵͳ��information_schema��COLUMNS����д��һ������ */
	public static void Write_COLUMNS(String SCHEMA, String TABLE, String COLUMN_NAME, int POS, String TYPE, String ISNULL, String KEY)
	{
		try {
			String path = "./data/information_schema/";
			RandomAccessFile dat = new RandomAccessFile(path+"COLUMNS.dat", "rw");
			dat.seek(dat.length()); //����Ҫ����ָ�붨λ�����
			long Starting_Address = dat.getFilePointer(); //ע��һ����Ҫ�����׵�ַ��ÿ��ndx��ָ�������ַ
			
			String[] columnName = {"TABLE_SCHEMA","TABLE_NAME","COLUMN_NAME","ORDINAL_POSITION","COLUMN_TYPE","IS_NULLABLE","COLUMN_KEY"};
			RandomAccessFile[] ndx = new RandomAccessFile[7]; //����һС���ڴ棬���7��ָ�룬ע��ֻ��ָ�룬�������
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
			if(KEY.length() > 0) //���Լ��µģ�ֻ�г��ȴ���0��KEY��������!?
				ndx[6].writeBytes(KEY);
			
			// dat��д��˳���ܸı�
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
			dat.writeBytes(KEY); // <<<<����ط����ܻ������⣬��KEY��""�մ�ʱ����֪����û��д�롮/0��
			
			for(int i=0; i<columnName.length; i++){
				ndx[i].writeLong(Starting_Address); // <<<<<<<<��ע�⣬�ڽ�βһ��Ҫ���Index�ĵ�ַ
				ndx[i].close();
			}
			dat.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/** ��information_schema��COLUMNS����,
	 * @return List<String>�� SelectFactor=0�����صĽ����Table���������Ե�Type����COLUMN_TYPE 
	 * @return List<String>�� SelectFactor=1�����صĽ����Table���������Ե�Name����COLUMN_NAME */
	public static List<String> read_List_From_COLUMNS(String TABLE_NAME, int SelectFactor)
	{
		try {
			RandomAccessFile SCHEMA_ndx = new RandomAccessFile("./data/information_schema/COLUMNS.TABLE_SCHEMA.ndx", "rw");
			RandomAccessFile COLUMNS_dat = new RandomAccessFile("./data/information_schema/COLUMNS.dat", "rw");
			
			List<String> readResult= new ArrayList<String>();
			
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
					if(tableName.equalsIgnoreCase(TABLE_NAME) == true) //TABLE_NAME���
					{
						//��ȡCOLUMN_NAME
						String COLUMN_NAME = read_varchar(COLUMNS_dat);
						
						//��ȡORDINAL_POSITION
						//int POSITION = COLUMNS_dat.readInt();
						COLUMNS_dat.readInt();
						
						//��ȡCOLUMN_TYPE
						String COLUMN_TYPE = baseFun.read_varchar(COLUMNS_dat);
						if(SelectFactor==0)
							readResult.add(COLUMN_TYPE); //��˳�����COLUMN_TYPE������
						else
							readResult.add(COLUMN_NAME); //��˳�����COLUMN_NAME������
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
