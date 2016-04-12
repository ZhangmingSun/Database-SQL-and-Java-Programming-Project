
import java.util.Scanner;

/** How to execute the .jar file 
 * >>>> java -jar davisql.jar
 * */
public class DatabaseEngine {
	public static String prompt = "davisql> ";
	
	public static String widgetTableFileName = "widgets.dat";
	public static String tableIdIndexName = "widgets.id.ndx";
	
	public static String currentSchema = "information_schema"; // when initialization, default is "information_schema"
	//public static String currentSchema = "zoo_schema";
	

	/** parse and execute the commands */
	public static void ParseAndExecutingCommand(String command)
	{
		String[] str = command.split("\\s+");
		int len = str.length;
		for(int i=0; i<len; i++)
			str[i] = str[i].trim(); //消除空格
		//System.out.println("str.length: " + str.length);
		
		if(len==3 && str[0].equalsIgnoreCase("create") && str[1].equalsIgnoreCase("schema")) {
			SimpleCommands.create_schema(str[2]);	//create scheme XXX;
		}
		else if(len==2 && str[0].equalsIgnoreCase("show") && str[1].equalsIgnoreCase("schemas")) {
			SimpleCommands.show_schema(); 	//show scheme;
		}
		else if(len==2 && str[0].equalsIgnoreCase("use")) {
			SimpleCommands.use_schema(str[1]);	//use scheme XXX;
		}
		else if(len>5 && str[0].equalsIgnoreCase("create") && str[1].equalsIgnoreCase("table"))
		{
			/** very important! It is to create table! */
			CreateTable.create_table(str); //create table XXX;
		}
		else if(len>=4 && str[0].equalsIgnoreCase("insert") && str[1].equalsIgnoreCase("into") 
				/*&& str[2].equalsIgnoreCase("table") && str[4].equalsIgnoreCase("values")*/)
		{
			String tableName = str[2].trim();
			InsertTable.insert_table(command, tableName); //insert table
		}
		else if(len>=4 && str[0].equalsIgnoreCase("select") && str[1].equalsIgnoreCase("*")
				&& str[2].equalsIgnoreCase("from"))
		{
			SelectCommand.SelectFromTable(command); //select-from-where
			//System.out.println("SelectFromTable!");
		}
		else if(len==2 && str[0].equalsIgnoreCase("show") && str[1].equalsIgnoreCase("tables")) {
			SimpleCommands.show_tables();
		}
		else if(len==3 && str[0].equalsIgnoreCase("drop") && str[1].equalsIgnoreCase("table")) {
			SimpleCommands.drop_table(str[2]);
		}
		else if(len==1 && str[0].equalsIgnoreCase("help")) {
			help();
		}
		/*else if(len==3 && str[0].equalsIgnoreCase("test") ) //仅仅为了测试用
		{
			String[] tmp = InsertTable.read_From_COLUMNS("zoo_schema", str[1].trim(), Integer.parseInt(str[2].trim())); 
			String columnName = tmp[0];
			String dataType = tmp[1];
			System.out.println("dataType="+dataType+"; columnName="+columnName);
		}
		else if(len==2 && str[0].equalsIgnoreCase("test") ) //仅仅为了测试用
		{
			//List<String> result = new ArrayList<String>();
			//result = SelectCommand.read_Type_From_COLUMNS(str[1]); 
			//for(int i=0; i<result.size(); i++)
				//System.out.println("Type=" + result.get(i));
			SelectCommand.Exe_Select_From_Cmd(str[1]);
		}*/
		else {
			System.out.println("The database system can not recognize this command!");
		}
	}
	
    public static void main(String[] args) {
    	
    	/** if the root directory "data" is not exist, it should initialize it. */
    	SimpleCommands.InitializeSystemSchema();
    	
		/* Display the welcome splash screen */
		splashScreen();

		//hardCodedCreateTableWithIndex();
		
		/** Each time the semicolon(;) delimiter is entered, the userCommand String is re-populated.*/

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in).useDelimiter(";");
		String userCommand;	// Variable to collect user input from the prompt
		
		do {
			System.out.print(prompt);
			/** 此处“scanner.next()”表示computer一直在等待，直到你输入！*/
			userCommand = scanner.next().trim(); //去首尾空格
			//System.out.println("Command = '" + userCommand + "';");
			
			/**Very important! Parse And Executing the input Command*/
			ParseAndExecutingCommand(userCommand);
			
		} while(!userCommand.equalsIgnoreCase("exit"));
		System.out.println("Exiting...");
		
		/*try {
			System.in.close();
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();}
		*/

    } /* End main() method */


//  ===========================================================================
//  STATIC METHOD DEFINTIONS BEGIN HERE
//  ===========================================================================
	/** Help: Display supported commands*/
	public static void help() {
		System.out.println(line("*",80));
		System.out.println();
		System.out.println("1. SHOW SCHEMAS		– Displays all schemas defined in your database.");
		System.out.println("2. USE			– Chooses a schema.");
		System.out.println("3. SHOW TABLES		– Displays all tables in the currently chosen schema.");
		System.out.println("4. CREATE SCHEMA	– Creates a new schema to hold tables.");
		System.out.println("5. CREATE TABLE		– Creates a new table schema, i.e. a new empty table.");
		System.out.println("6. INSERT INTO TABLE	– Inserts a row/record into a table. ");
		System.out.println("7. DROP TABLE		– Remove a table schema, and all of its contained data.");
		System.out.println("8. SELECT-FROM-WHERE	– style query");
		System.out.println("9. EXIT			– Exit the program");
		System.out.println();
		System.out.println(line("*",80));
	}
	
	/** Display the welcome "splash screen"*/
	public static void splashScreen() {
		System.out.println(line("*",80));
        System.out.println("Welcome to DavisBaseLite"); // Display the string.
		version();
		System.out.println("Type \"help;\" to display supported commands.");
		System.out.println(line("*",80));
	}

	/**
	 * @param s The String to be repeated
	 * @param num The number of time to repeat String s.
	 * @return String A String object, which is the String s appended to itself num times.
	 */
	public static String line(String s,int num) {
		String a = "";
		for(int i=0;i<num;i++) {
			a += s;
		}
		return a;
	}

	public static void version() {
		System.out.println("DavisBaseLite v1.0\n");
	}

}


