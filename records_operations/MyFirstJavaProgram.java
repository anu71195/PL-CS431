import java.io.*;  
import java.util.*; 
class file_op 
{  

	void append(Vector<Records> input_v,String filename)
	{
	     try
	     {    
	       FileWriter fw=new FileWriter(filename,true);    
	       for(int i=0;i<input_v.size();i++)
	       {
	     	 Records current_record=new Records();
	     	 current_record=input_v.get(i);
	     	 fw.write(""+current_record.roll+","+current_record.name+","+current_record.email+","+current_record.marks+","+current_record.teacher+"\n");
	       }
	       fw.close();    
	      }
	      catch(Exception e){System.out.println(e);}      
	  }
	 void write(Vector<Records> input_v,String filename)
	{
	     try
	     {    
	       FileWriter fw=new FileWriter(filename);    
	       for(int i=0;i<input_v.size();i++)
	       {
	     	 Records current_record=new Records();
	     	 current_record=input_v.get(i);
	     	 fw.write(""+current_record.roll+","+current_record.name+","+current_record.email+","+current_record.marks+","+current_record.teacher+"\n");
	       }
	       fw.close();    
	      }
	      catch(Exception e){System.out.println(e);}      
	  }
	  Vector<Records> read(String filename)
	  {
	        String line = null;
	        Vector<Records> output_v = new Vector<Records>(); 
	        Records record=new Records();
	        try 
	        {
	            FileReader fileReader = new FileReader(filename);
	            BufferedReader bufferedReader = new BufferedReader(fileReader);
	            while((line = bufferedReader.readLine()) != null) 
	            {
	                String[] arrOfStr=line.split(",");
	                record.roll=Integer.valueOf(arrOfStr[0]);
					record.marks=Integer.parseInt(arrOfStr[3]);
					record.name=arrOfStr[1];
					record.email=arrOfStr[2];
					record.teacher=arrOfStr[4];
	                output_v.add(record);
	            }   
	            bufferedReader.close();         
	        }
	        catch(FileNotFoundException ex) 
	        {
	            System.out.println("Unable to open file '" + filename + "'");                
	        }
	        catch(IOException ex) 
	        {
	            System.out.println("Error reading file '" + filename + "'");                  
	        }
	        return output_v;
	  }
} 

class Records
{
	int roll,marks;
	String name,email,teacher;
	void print_record()
	{
		System.out.println(roll);
		System.out.println(name);
		System.out.println(email);
		System.out.println(marks);
		System.out.println(teacher+"\n");

	}
}





public class MyFirstJavaProgram 
{
   public static void main(String []args) 
   {
   		file_op fwe=new file_op();
		Vector<Records> v = new Vector<Records>(); 
		Vector<Records>  all_records=new Vector<Records> ();
		String filename="t.txt";
		Records record=new Records();

		record.roll=150101010;
		record.marks=7;
		record.name="Anurag Ramteke";
		record.email="anurag.ramteke@iitg.ernet.in";
		record.teacher="ta1";

		v.add(record);

   		fwe.write(v,filename);
   		fwe.write(v,filename);
   		fwe.append(v,filename);

   		all_records=fwe.read(filename);
   		fwe.append(all_records,filename);
   		all_records.get(0).print_record();
   }
}