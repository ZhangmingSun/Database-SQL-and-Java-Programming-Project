import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;


public class SimpleCommands {
		
	/** if the root directory "data" is not exist, it should initialize it. */
	public static void InitializeSystemSchema()
	{
    	try {
    		File dir = new File("./data");
    		if(!dir.exists()){
    			dir.mkdir(); // create root directory ��data�� if it does not exist.
    		
    			String path = "./data/information_schema";
	    		File systemSchema = new File(path);
	    		systemSchema.mkdir(); // create system schema if it does not exist.
	    		
	    		RandomAccessFile SCHEMATA_dat = new RandomAccessFile(path+"/SCHEMATA.dat", "rw");
	    		SCHEMATA_dat.writeByte("information_schema".length());
	    		SCHEMATA_dat.writeBytes("information_schema");
	    		SCHEMATA_dat.close();
	    		
	    		baseFun.Write_TABLES("information_schema", "SCHEMATA", (long)1);
	    		baseFun.Write_TABLES("information_schema", "TABLES", (long)3);
	    		baseFun.Write_TABLES("information_schema", "COLUMNS", (long)7);
	    		
	    		baseFun.Write_COLUMNS("information_schema", "SCHEMATA", "SCHEMA_NAME", 1, "varchar(64)", "NO", "");
	    		baseFun.Write_COLUMNS("information_schema", "TABLES", "TABLE_SCHEMA", 1, "varchar(64)", "NO", "");
	    		baseFun.Write_COLUMNS("information_schema", "TABLES", "TABLE_NAME", 2, "varchar(64)", "NO", "");
	    		baseFun.Write_COLUMNS("information_schema", "TABLES", "TABLE_ROWS", 3, "long int", "NO", "");
	    		baseFun.Write_COLUMNS("information_schema", "COLUMNS", "TABLE_SCHEMA", 1, "varchar(64)", "NO", "");
	    		baseFun.Write_COLUMNS("information_schema", "COLUMNS", "TABLE_NAME", 2, "varchar(64)", "NO", "");
	    		baseFun.Write_COLUMNS("information_schema", "COLUMNS", "COLUMN_NAME", 3, "varchar(64)", "NO", "");
	    		baseFun.Write_COLUMNS("information_schema", "COLUMNS", "ORDINAL_POSITION", 4, "int", "NO", "");
	    		baseFun.Write_COLUMNS("information_schema", "COLUMNS", "COLUMN_TYPE", 5, "varchar(64)", "NO", "");
	    		baseFun.Write_COLUMNS("information_schema", "COLUMNS", "IS_NULLABLE", 6, "varchar(3)", "NO", "");
	    		baseFun.Write_COLUMNS("information_schema", "COLUMNS", "COLUMN_KEY", 7, "varchar(3)", "NO", "");
    		}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	// Command: create schema test1;
	/** When you create schema, this scheme will be regarded as the current Schema.*/
	public static void create_schema(String schemaName)
	{
		try {
			File dirFile = new File("./data/"+schemaName);
			if(!dirFile.exists()){
				if(dirFile.mkdir() == true)	// create File directory under ��data�� directory
				{
					// Insert this scheme name to system SCHEMATA table
	    			String path = "./data/information_schema";
		    		//File systemSchema = new File(path);
		    		RandomAccessFile schemaDat = new RandomAccessFile(path+"/SCHEMATA.dat", "rw");
		    		schemaDat.seek(schemaDat.length()); //���ĩβָ�룡��ܹؼ�������ǰ��������
		    		
//		    		RandomAccessFile schemaNdx = new RandomAccessFile(path+"/SCHEMATA.SCHEMA_NAME.ndx", "rw");	    		
//		    		schemaNdx.writeByte(schemaName.length());
//		    		schemaNdx.writeBytes(schemaName);
//		    		schemaNdx.writeLong(schemaDat.getFilePointer()); //.dat��ǰ��ָ���ַ��ֵ��.ndx
		    		
		    		schemaDat.writeByte(schemaName.length());
		    		schemaDat.writeBytes(schemaName);
		    		
		    		schemaDat.close(); //����Ҫ����������ر�
		    		//schemaNdx.close();
					System.out.println("Create schema "+schemaName+" successfully!");
				}
			}else{
				System.out.println("Failed! The schema has already existed in the database!");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/** Displays all schemas defined in your database. */
	public static void show_schema(){
		try {
			String path = "./data/information_schema";
    		RandomAccessFile dat = new RandomAccessFile(path+"/SCHEMATA.dat", "rw");
    		long len = dat.length();
    		long cnt = 0;
    		while(cnt < len)
    		{
	    		byte varcharLength = dat.readByte();
				for(int i = 0; i < varcharLength; i++)
					System.out.print((char)dat.readByte());
				System.out.print("\n");
				cnt += varcharLength + 1;
    		}
    		dat.close(); // ע����Ҫ�ر�
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void use_schema(String schemaName){
		try {
    		File dir = new File("./data/" + schemaName);
    		if(dir.exists()){
    			DatabaseEngine.currentSchema = schemaName; // switch to schemaName
    			System.out.println("Database changed");
    		}else{
    			System.out.println("Failed! The schema '"+schemaName+"' does not exist!");
    		}
    		
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/** Displays all tables in the currectly chosen schema. */
	public static void show_tables(){
		File dir = new File("./data/" + DatabaseEngine.currentSchema);
		if(dir.exists())
		{
			List<String> nameList = baseFun.directoryNameList();
			List<String> tableNameList = new ArrayList<String>();
			
			for(int i = 0; i < nameList.size(); i++) {
				String[] str = nameList.get(i).split("\\."); /** ע��һ��Ҫ��˫��"\\" */
				//System.out.println(nameList.get(i)+" "+str.length);
				if(str.length == 2)
					tableNameList.add(str[0]);
			}
			for(int i = 0; i < tableNameList.size(); i++) {
				//System.out.println(DatabaseEngine.currentSchema+"."+tableNameList.get(i));
				System.out.println(tableNameList.get(i));
			}
		}else{
			//System.out.println("Failed! ......");
		}
	}
	
	/** Remove a table schema, and all of its contained data.
	 *  Drop Table��ʱ����Ҫ��������ˢ�������Ұ����и�Table��ص�.dat�ļ���.ndx�ļ�ɾ��*/
	public static void drop_table(String tablename) {
		File tablePath = new File("./data/"+DatabaseEngine.currentSchema+"/"+tablename+".dat");
		if(tablePath.exists()) {
			if(tablePath.delete() == true)
			{
				// delete tableName in system COLUMNS table
				delete_tableName_inCOLUMNS(tablename);
				
				// delete all related .ndx files in the current schema directory
				List<String> nameList = baseFun.directoryNameList();
				for(int i = 0; i < nameList.size(); i++) {
					String[] str = nameList.get(i).split("\\."); /** ע��һ��Ҫ��˫��"\\" */
					if(str[0].equalsIgnoreCase(tablename)) {
						File tmpPath = new File("./data/"+DatabaseEngine.currentSchema+"/"+nameList.get(i));
						tmpPath.delete();
					}
				}
				System.out.println("Drop table '"+tablename+"' successfully!");
			}
			else
				System.out.println("Can not delete the table '"+tablename+"' due to certain reason!");
		}
		else
			System.out.println("Failed! The table '"+tablename+"' does not exist in the current schema!");
	}
	
	/** ��νdelete�����ǰ����е�tableNameȫ����� ��XXX�� �� ���������ַ� */
	public static void delete_tableName_inCOLUMNS(String TABLE_NAME) {
		try {
			RandomAccessFile SCHEMA_ndx = new RandomAccessFile("./data/information_schema/COLUMNS.TABLE_SCHEMA.ndx", "rw");
			RandomAccessFile COLUMNS_dat = new RandomAccessFile("./data/information_schema/COLUMNS.dat", "rw");
			//String[] readResult = new String[2];
			
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
					
					//======����Ҫ����Ҫ��ǰ������������λ��ָ��======
					long tmpPointer = COLUMNS_dat.getFilePointer();
					
					//��ȡTABLE_NAME
					String tableName = baseFun.read_varchar(COLUMNS_dat);
					if(tableName.equalsIgnoreCase(TABLE_NAME) == true) //TABLE_NAMEƥ��
					{
						//build a temp string "XXX..." which length is same with tableName
						String newTableName = "";
						for(int i = 0; i < tableName.length(); i++)
							newTableName += "X";

						COLUMNS_dat.seek(tmpPointer); //�ص�֮ǰ��λ�ã�����Ҫ������
						COLUMNS_dat.writeByte(newTableName.length());
						COLUMNS_dat.writeBytes(newTableName);
					}
					
				}
			}
			SCHEMA_ndx.close();
			COLUMNS_dat.close();
			//return readResult;
		
		} catch (Exception e) {
			System.out.println(e.getMessage());
			//e.printStackTrace();
		}
	}

}
