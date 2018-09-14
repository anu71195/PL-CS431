import java.io.*;  
import java.util.*; 
import java.lang.*; 
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
	        try 
	        {
	            FileReader fileReader = new FileReader(filename);
	            BufferedReader bufferedReader = new BufferedReader(fileReader);
	            while((line = bufferedReader.readLine()) != null) 
	            {
	                String[] arrOfStr=line.split(",");

			        Records record=new Records();
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

class Sortbyroll implements Comparator<Records> 
{ 
    // Used for sorting in ascending order of 
    // roll number 
    public int compare(Records a, Records b) 
    { 
        return a.roll - b.roll; 
    } 
} 
class Sortbyname implements Comparator<Records> 
{ 
    // Used for sorting in ascending order of 
    // roll number 
    public int compare(Records a, Records b) 
    { 
        return a.name.compareTo(b.name); 
    } 
} 
  


class sort_op
{
	 
	
	void sortbyroll(Vector<Records> input_v)
	{
		for(int i=0;i<input_v.size();i++)
		{
			System.out.println(input_v.get(i).roll);
		 
		}
		System.out.println("\n");
		Collections.sort(input_v,new Sortbyroll());
				for(int i=0;i<input_v.size();i++)
		{
			System.out.println(input_v.get(i).roll);
		 
		}

	}
	void sortbyname(Vector<Records> input_v)
	{
		for(int i=0;i<input_v.size();i++)
		{
			System.out.println(input_v.get(i).name);
		 
		}
		System.out.println("\n");
		Collections.sort(input_v,new Sortbyname());
				for(int i=0;i<input_v.size();i++)
		{
			System.out.println(input_v.get(i).name);
		 
		}

	}

}


class Records
{
	int roll,marks;
	String name,email,teacher;
	void print_record()throws IOException
	{
		System.out.println(roll);
		System.out.println(name);
		System.out.println(email);
		System.out.println(marks);
		System.out.println(teacher+"\n");

	}
}



class vec_op
{
	void print_vector(Vector  input)
	{
		for(int i=0;i<input.size();i++)
		{
			System.out.println(input.get(i));
		}
		System.out.println("\n");
	}
	Vector initialize_zeros(int n)
	{
		Vector output=new Vector();
		for(int i=0;i<n;i++)
		{
			output.add(0);
		}
		return output;
	}
}
class user_op
{
	Vector take_input()
	{
		
		Vector all_output=new Vector();
		int i_input;
		String s_input;
		for(int j=0;j<2;j++)
		{
			Vector output=new Vector();
			BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in));
			try 
			{
				System.out.print("Enter Teacher’s Name: ");
				s_input = reader.readLine(); 
				output.add(s_input);

				System.out.print("Enter Student Roll number: ");
				i_input=Integer.valueOf(reader.readLine());
				output.add(i_input);

			    System.out.println("Update Mark : 1. Increase");
				System.out.println("              2. Decrease");
				i_input=Integer.valueOf(reader.readLine());
				output.add(i_input);
				if(i_input==1)
				{
					System.out.print("Mark to add: ");
					i_input=Integer.valueOf(reader.readLine());
					output.add(i_input);

				}
				else if(i_input==2)
				{
					System.out.print("Mark to deduct: ");
					i_input=Integer.valueOf(reader.readLine());
					output.add(i_input);
				}
			 }
			catch(IOException e) 
			{
	    // Code to handle an IOException here
			 }
			 all_output.add(output);
		}
		return all_output;

	}
}
class data_handling
{
	void run(int syn_val,Vector <Records> pass_records,Vector user_input)
	{
		vec_op vop=new vec_op();
		if(syn_val==1)without_sync(pass_records,user_input);
		else if(syn_val==2)with_sync(pass_records,user_input);
		record_flag=vop.initialize_zeros(all_records.size());
	}

	void without_sync(Vector<Records> pass_records,Vector user_input)
	{

	}
	void with_sync(Vector<Records> pass_records,Vector user_input)
	{

	}
}
public class MyFirstJavaProgram 
{
   public static void main(String []args) 
   {
   		file_op fwe=new file_op();
		Vector<Records> v = new Vector<Records>(); 
		Vector<Records>  all_records=new Vector<Records> ();
		Vector<Records>  pass_records=new Vector<Records> ();//recordsto be passed as the argumentfirst needs to be cloned
		Vector user_input=new Vector();
		vec_op vop=new vec_op();
		user_op uop=new user_op();
		Vector record_flag=new Vector();
		String filename="t.txt";
		Records record=new Records();
		sort_op so = new sort_op();
		int i_input;
		String s_input;
		BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in));
		data_handling dh=new data_handling();

		// record.roll=150101010;
		// record.marks=7;
		// record.name="Anurag Ramteke";
		// record.email="anurag.ramteke@iitg.ernet.in";
		// record.teacher="ta1";

		// v.add(record);

  //  		fwe.write(v,filename);
  //  		fwe.write(v,filename);
  //  		fwe.append(v,filename);

  //  		all_records=fwe.read(filename);
  //  		fwe.append(all_records,filename);
  //  		all_records.get(0).print_record();
   		all_records=fwe.read("t3.txt");
   		System.out.println(all_records.size());
   		so.sortbyroll(all_records);
   		so.sortbyname(all_records);



   		////////////////////////////////////
   		
   		
   		System.out.println(record_flag.size());
   		vop.print_vector(record_flag);
   		user_input=uop.take_input();
   		System.out.println(user_input);
		s_input="yes";
   		while(s_input.compareTo("yes")==0)
   		{
			System.out.println("Choose one:");
			System.out.println("1. Without Synchronization");
			System.out.println("2. With Synchronization");
			try
			{
				i_input=Integer.valueOf(reader.readLine());
				pass_records=(Vector<Records>)all_records.clone();
				dh.run(i_input,pass_records,user_input);
			}
			catch(IOException e) 
			{

			}
			System.out.println("Check the output files");
			System.out.println("Do you want to continue? yes/no");
			try
			{
				s_input=reader.readLine();
			}
			catch(IOException e) 
			{

			}
		}


   }
}