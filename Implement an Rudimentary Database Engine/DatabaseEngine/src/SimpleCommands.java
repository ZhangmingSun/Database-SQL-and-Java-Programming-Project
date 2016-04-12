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
    			dir.mkdir(); // create root directory “data” if it does not exist.
    		
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
				if(dirFile.mkdir() == true)	// create File directory under “data” directory
				{
					// Insert this scheme name to system SCHEMATA table
	    			String path = "./data/information_schema";
		    		//File systemSchema = new File(path);
		    		RandomAccessFile schemaDat = new RandomAccessFile(path+"/SCHEMATA.dat", "rw");
		    		schemaDat.seek(schemaDat.length()); //获得末尾指针！这很关键，且是前提条件！
		    		
//		    		RandomAccessFile schemaNdx = new RandomAccessFile(path+"/SCHEMATA.SCHEMA_NAME.ndx", "rw");	    		
//		    		schemaNdx.writeByte(schemaName.length());
//		    		schemaNdx.writeBytes(schemaName);
//		    		schemaNdx.writeLong(schemaDat.getFilePointer()); //.dat当前的指针地址赋值给.ndx
		    		
		    		schemaDat.writeByte(schemaName.length());
		    		schemaDat.writeBytes(schemaName);
		    		
		    		schemaDat.close(); //很重要，用完后必须关闭
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
    		dat.close(); // 注意需要关闭
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
				String[] str = nameList.get(i).split("\\."); /** 注意一定要加双杠"\\" */
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
	 *  Drop Table的时候，需要把它给冲刷掉；并且把所有跟Table相关的.dat文件和.ndx文件删掉*/
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
					String[] str = nameList.get(i).split("\\."); /** 注意一定要加双杠"\\" */
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
	
	/** 所谓delete，就是把所有的tableName全部变成 “XXX” 或 其他特殊字符 */
	public static void delete_tableName_inCOLUMNS(String TABLE_NAME) {
		try {
			RandomAccessFile SCHEMA_ndx = new RandomAccessFile("./data/information_schema/COLUMNS.TABLE_SCHEMA.ndx", "rw");
			RandomAccessFile COLUMNS_dat = new RandomAccessFile("./data/information_schema/COLUMNS.dat", "rw");
			//String[] readResult = new String[2];
			
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
					
					//======很重要：需要提前保存它的数据位置指针======
					long tmpPointer = COLUMNS_dat.getFilePointer();
					
					//读取TABLE_NAME
					String tableName = baseFun.read_varchar(COLUMNS_dat);
					if(tableName.equalsIgnoreCase(TABLE_NAME) == true) //TABLE_NAME匹配
					{
						//build a temp string "XXX..." which length is same with tableName
						String newTableName = "";
						for(int i = 0; i < tableName.length(); i++)
							newTableName += "X";

						COLUMNS_dat.seek(tmpPointer); //回到之前的位置，很重要！！！
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
