
import java.io.RandomAccessFile;

public class demoTest {
	
	static String  SchemanameTablenameDat = "Tablename.dat";
	static String SchemanameTablenameColumnnameNdx = "Tablename.Columnname.ndx";
	
    public static void main(String[] args) {
    	
//    	int m1 = 0; int m2 = 5;
//    	compare<Integer> test1 = new compare();
//    	System.out.println(test1.max(m1, m2));
//    	
//    	float mm1 = (float) 9.9; float mm2 = (float) 8.8;
//    	compare<Float> test2 = new compare();
//    	System.out.println(test2.max(mm1, mm2));
    	
//    	String str1 = baseFun.Convert_Long_To_DATETIME("20160204050709");
//    	System.out.println(str1);
//    	String str2 = baseFun.Convert_Long_To_DATE("20160204");
//    	System.out.println(str2);
    	
    	String b = "'2016-03-23_13:52:23'";
		String[] symbols = {"-","_",":","\'"};
		b = baseFun.removeSymbols(b, symbols);
		System.out.println("replace output: " + b);
		
    	/*int m1 = 0; int m2 = 5;
    	compare<Integer> test1 = new compare();
    	System.out.println(test1.compareTwoNum(m1, m2, "<>"));
    	
    	String s1 = "test"; String s2 = "test";
    	compare<String> str1 = new compare();
    	System.out.println(str1.compareTwoNum(s1, s2, ">="));
    	*/
    	
    	writeToHardware();
		
    	//String str = "char(64)".substring(5, "char(64)".length()-1);
    	//System.out.println("str = " + str);
    	System.out.println("str = " + "char(64)".length());
    }
     
    
    /** getFilePointer()：获取当前的指针Index，刚打开文件时Index=1
     * seek()：移动当前文件里移动用的，注意：初始位置是0
     * length()：获取文件大小的
     * skipBytes()：跳过多少字节数
     */
    public static void writeToHardware()
    {
    	int id;
    	String name;
    	short quantity;
    	float probability;
		id = 1;
		name = "alpha";
		quantity = 847;
		probability = 0.341f;
		try {
			//open the certain file
			RandomAccessFile widgetTableFile = new RandomAccessFile(SchemanameTablenameDat, "rw");
			RandomAccessFile tableIdIndex = new RandomAccessFile(SchemanameTablenameColumnnameNdx, "rw");
			//JOptionPane.showMessageDialog(null, ""+widgetTableFile.getFilePointer());
			
			//System.out.println(Long.toString(tableIdIndex.getFilePointer()));
			/** =============把写的指针移动到第9个Byte位置，注意：初始位置是0=============*/
			//tableIdIndex.seek(8);
			//System.out.println(Long.toString(tableIdIndex.getFilePointer()));
			

			/** =============写数据=============*/
			tableIdIndex.writeInt(9);	//Int--4个Byte
			tableIdIndex.writeLong(widgetTableFile.getFilePointer());

			//System.out.println(tableIdIndex.length()); //输出长度是40
			//System.out.println(Long.toString(tableIdIndex.getFilePointer()));
			
			widgetTableFile.writeInt(id);
			System.out.println("writeInt(id) getFilePointer(): " + widgetTableFile.getFilePointer());
			widgetTableFile.writeByte(name.length());
			widgetTableFile.writeBytes(name);
			System.out.println("writeInt(name) getFilePointer(): " + widgetTableFile.getFilePointer());
			widgetTableFile.writeShort(quantity);
			widgetTableFile.writeFloat(probability);
			
			tableIdIndex.close();
			widgetTableFile.close();
			
			/** =============读数据()=============*/
			RandomAccessFile widgetRead = new RandomAccessFile(SchemanameTablenameDat, "rw");
			System.out.print(widgetRead.readInt());
			System.out.print("\t");
			byte varcharLength = widgetRead.readByte();
			for(int i = 0; i < varcharLength; i++)
				System.out.print((char)widgetRead.readByte());
			System.out.print("\t");
			System.out.print(widgetRead.readShort());
			System.out.print("\t");
			System.out.println(widgetRead.readFloat());
			
			widgetRead.close();
			
			//RandomAccessFile tableIdIndex1 = new RandomAccessFile(SchemanameTablenameColumnnameNdx, "rw");
			
			//widgetTableFile.close();
			//tableIdIndex.close();
			
			/*tableIdIndex.writeInt(id+5);
			//tableIdIndex.writeLong(widgetTableFile.getFilePointer());
			tableIdIndex.writeInt(id);
			System.out.println(Long.toString(widgetTableFile.getFilePointer()));
			widgetTableFile.writeInt(id);
			widgetTableFile.writeByte(name.length());
			widgetTableFile.writeBytes(name);
			widgetTableFile.writeShort(quantity);
			widgetTableFile.writeFloat(probability);*/			
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Error in connection: " + e.getMessage());
			//JOptionPane.showMessageDialog(null, e.getMessage());
		}
    }
}
