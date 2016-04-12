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
		/** ################################# Ԥ���� #################################
		 * �ⲿ����Ҫ�ǻ�ȡfinalString������ȡ��һ��"("�����һ��")"֮�������
		 */
		String newCommand = ""; // ת����һ���ַ���
		for(int i=0; i<substr.length; i++)
			newCommand += substr[i]+" ";
		newCommand = newCommand.trim(); //ȥ�����һ���ո񣡣���
		
		int position = newCommand.indexOf("("); //��ȡ��һ��"("��λ��
		String frontString = newCommand.substring(0, position);
		//System.out.println(frontString.trim());
		String[] frontStr = frontString.trim().split(" ");
		if (position == -1 | newCommand.indexOf(")") == -1 | frontStr.length > 3){
			System.out.println("The database system can not recognize this command!");
			return;
		}
		String finalString = newCommand.substring(position+1, newCommand.length()-1); //��ȡ��һ��"("�����һ��")"֮�������
		//System.out.println(finalString.trim());
		
		String TableName = frontStr[2].trim(); // ��ȡ���TableName����Ҫ��
		
		// ####################### ��鵱ǰSchema�Ƿ����Ҫ������Table #######################
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
			
			// #################################��һ���֣�check�����ʽ�Ƿ����#################################
			if(part.length>5 | part.length<=1) {
				System.out.println("Error1: Fail to create! The '"+str[i]+"' is wrong!");
				return;
			}
			//������5��С�ڵ���1��������ͳ��ȴ���3������£������λ�Ƿ���"not null"������"primary key"
			if(part.length > 3 && !((part[part.length-2].equalsIgnoreCase("not") && part[part.length-1].equalsIgnoreCase("null")) |
						(part[part.length-2].equalsIgnoreCase("primary") && part[part.length-1].equalsIgnoreCase("key"))))
			{
				System.out.println("Error0: Fail to create! The '"+str[i]+"' is wrong!");
				return;
			}
			if(part.length == 5 | part.length == 3) { //lengthΪ3��5ʱ��ֻ��Ҫ���part[1]��part[2]����
				//��2��3λ�϶���"short int" ���� "long int", ���߱���
				if(!((part[1].equalsIgnoreCase("short") && part[2].equalsIgnoreCase("int")) 
						| (part[1].equalsIgnoreCase("long") && part[2].equalsIgnoreCase("int"))))
				{
					System.out.println("Error2: Fail to create! The '"+str[i]+"' is wrong!");
					return;
				}
				// ...�˴���������
				//System.out.println("Create Table successfully!");
			}
			else if(part.length == 4 | part.length == 2) //lengthΪ2��4ʱ��ֻ��Ҫ���part[1]����
			{
				if( !(part[1].toLowerCase().contains("char(")==true |
						part[1].toLowerCase().contains("varchar(")==true))
				{
					int j;
					// check�Ƿ��м��������dataType��һԱ���߱���
					for(j=0; j<dataType1.length; j++){
						if( part[1].equalsIgnoreCase(dataType1[j]))
								break;
					}
					if(j >= dataType1.length){
						System.out.println("Error3: Fail to create! The '"+str[i]+"' is wrong!");
						return;
					}
				}
				// ...�˴���������
				//System.out.println("Create successfully!");
			}else{
				System.out.println("Error4: Fail to create! The '"+str[i]+"' is wrong!");
			}
			
			// #######################�ڶ����֣��Ѵ����������д��ϵͳschema��Tables#######################
			String TYPE = "";
			String ISNULL = "";
			String KEY = ""; //���key����ʱ��������Ϊ�մ�""
			
			// =======Ԥ����һ��=======
			if(part.length == 5 | part.length == 4) {
				if(part.length == 5)
					TYPE = part[1].trim() + " " + part[2].trim();
				else
					TYPE = part[1].trim();
				ISNULL = "NO";
				if(part[part.length-1].equalsIgnoreCase("key")) //˵����not null������primary key
					 KEY = "PRI";
			}
			else if(part.length == 3 | part.length == 2) {
				if(part.length == 3)
					TYPE = part[1].trim() + " " + part[2].trim();
				else
					TYPE = part[1].trim();
				ISNULL = "YES";
				//KEY = "";	//���key��֮ǰ�Ѿ�����Ϊ�մ�""
			}

			String SCHEMA = DatabaseEngine.currentSchema;
			String TABLE = TableName;
			String COLUMN_NAME = part[0];
			int POS = i+1;
			
			/** ����CreateTable���������ĵ����
			 * SCHEMA, TABLE, COLUMN_NAME, POS, TYPE, ISNULL, KEY 
			 **/
			baseFun.Write_COLUMNS(SCHEMA, TABLE, COLUMN_NAME, POS, TYPE, ISNULL, KEY);
			
			//System.out.println(SCHEMA+" "+TABLE+" "+COLUMN_NAME+" "+POS+" "+TYPE+" "+ISNULL+" "+KEY);
			//Write_COLUMNS("information_schema", "SCHEMATA", "SCHEMA_NAME", 1, "varchar(64)", "NO", "");
		}
		/** ����CreateTable�������Ҫ���֮һ */
		baseFun.Write_TABLES(DatabaseEngine.currentSchema, TableName, (long) 0);
		//System.out.println(DatabaseEngine.currentSchema+" "+TableName+" "+0);
		
		// ####################### ��ǰĿ¼�´�����Ӧ��dat��ndx�Ŀ��ļ� #######################
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
