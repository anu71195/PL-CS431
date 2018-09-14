import java.io.*;  
import java.util.*; 
import java.lang.*; 
import java.util.concurrent.locks.*;
import java.nio.channels.FileLock;

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
		Collections.sort(input_v,new Sortbyroll());
	}
	void sortbyname(Vector<Records> input_v)
	{
		Collections.sort(input_v,new Sortbyname());
	}

}

class Records
{
	int roll,marks;
	String name,email,teacher;
	void print_record()throws IOException
	{
		System.out.print(roll+"\t");
		System.out.print(name+"\t\t");
		System.out.print(email+"\t\t");
		System.out.print(marks+"\t");
		System.out.print(teacher+"\n");


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
				System.out.print("Enter Teacher’s Name(CC/TA1/TA2): ");
				s_input = reader.readLine(); 

				while(s_input.compareTo("CC")!=0 && s_input.compareTo("TA2")!=0 && s_input.compareTo("TA1")!=0)
				{
					System.out.println("\nWRONG INPUT!!!!!  TRY AGAIN");
					System.out.print("Enter Teacher’s Name(CC/TA1/TA2): ");
					s_input = reader.readLine(); 
				}
				output.add(s_input);

				System.out.print("Enter Student Roll number: ");
				i_input=Integer.valueOf(reader.readLine());
				output.add(i_input);

			    System.out.println("Update Mark : 1. Increase");
				System.out.println("              2. Decrease");
				i_input=Integer.valueOf(reader.readLine());
				while(i_input!=1 &&i_input!=2)
				{
					System.out.println("\nWRONG INPUT!!!!!  TRY AGAIN");
					System.out.println("Update Mark : 1. Increase");
					System.out.println("              2. Decrease");
					i_input=Integer.valueOf(reader.readLine());

				}
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
				System.out.println("IOException");
			 }
			 all_output.add(output);
		}
		return all_output;

	}
}

class with_sync implements Runnable
{
	String teacher;
	int roll,id_op,marks_id;//id_op increment decrement operation, marks_id marks incrementdecrement
	Vector<Records>  records;
	Vector input;
	Map< Integer,Integer>record_flag;
	ReadWriteLock readWriteLock;
	public with_sync(Vector input,Vector<Records>  pass_records,Map< Integer,Integer> record_flag,ReadWriteLock readWriteLock)
	{
		this.input=input;
		this.record_flag=  record_flag;
		teacher=(String)input.get(0);
		roll=(int)input.get(1);
		id_op=(int)input.get(2);
		marks_id=(int)input.get(3);
		records=pass_records;
		this.readWriteLock=readWriteLock;
	}
	@Override
	public void run()
	{	
		
		sort_op so = new sort_op();
		file_op fo=new file_op();

		readWriteLock.writeLock().lock();
		System.out.println(teacher);
		for(int i=0;i<records.size();i++)	
		{
			if(records.get(i).roll==this.roll)System.out.println("currentmarks "+records.get(i).marks);
			if(records.get(i).roll==this.roll && (record_flag.get(roll)==null||teacher.compareTo("CC")==0))
			{
				if(teacher.compareTo("CC")==0)record_flag.put(roll, new Integer(1)); 
				if(id_op==1)
				{
					records.get(i).marks+=marks_id;
				}
				else if(id_op==2)
				{
					records.get(i).marks-=marks_id;
				}				
			}
			if(records.get(i).roll==this.roll)System.out.println("currentmarks "+records.get(i).marks);
		}
   		so.sortbyroll(records);
   		fo.write(records,"records_by_roll.txt");
   		fo.write(records,"t3.txt");
   		so.sortbyname(records);
   		fo.write(records,"records_by_name.txt");
		readWriteLock.writeLock().unlock();
	}
}
class without_sync implements Runnable
{	
	String teacher;
	int roll,id_op,marks_id;//id_op increment decrement operation, marks_id marks incrementdecrement
	Vector<Records>  records;
	Vector input;
	Map< Integer,Integer>record_flag;
	ReadWriteLock readWriteLock;
	public without_sync(Vector input,Vector<Records>  pass_records,Map< Integer,Integer> record_flag,ReadWriteLock readWriteLock)
	{
		this.input=input;
		this.record_flag=  record_flag;
		teacher=(String)input.get(0);
		roll=(int)input.get(1);
		id_op=(int)input.get(2);
		marks_id=(int)input.get(3);
		records=pass_records;
		this.readWriteLock=readWriteLock;
	}
	@Override
	public void run()
	{
		sort_op so = new sort_op();
		file_op fo=new file_op();
		int current_marks,current_roll;

		System.out.println(teacher);
		for(int i=0;i<records.size();i++)	
		{
			current_marks=records.get(i).marks;
			current_roll=records.get(i).roll;
			if(current_roll==this.roll)System.out.println("currentmarks "+current_marks);
			if(current_roll==this.roll && (record_flag.get(roll)==null||teacher.compareTo("CC")==0))
			{
				if(teacher.compareTo("CC")==0)record_flag.put(roll, new Integer(1)); 
				if(id_op==1)
				{
					records.get(i).marks=current_marks+marks_id;
				}
				else if(id_op==2)
				{
					records.get(i).marks=current_marks-marks_id;
				}				
			}
			if(current_roll==this.roll)System.out.println("currentmarks "+records.get(i).marks);
		}

		readWriteLock.writeLock().lock();
		
		so.sortbyroll(records);
   		fo.write(records,"records_by_roll.txt");
   		fo.write(records,"t3.txt");
   		so.sortbyname(records);
   		fo.write(records,"records_by_name.txt");

		readWriteLock.writeLock().unlock();
		
	}
}

public class data_mod 
{
   public static void main(String []args) 
   {
   		file_op fo=new file_op();
		Vector<Records>  all_records=new Vector<Records> ();
		Vector<Records>  pass_records=new Vector<Records> ();//recordsto be passed as the argumentfirst needs to be cloned
		Vector user_input=new Vector();
		vec_op vo=new vec_op();
		user_op uo=new user_op();
		sort_op so = new sort_op();
		int i_input;
		String s_input;
		BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in));
		ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		Map< Integer,Integer> record_flag =  new HashMap< Integer,Integer>(); 

   		////////////////////////////////////////////////////////////////////////////////
   		
   		all_records=fo.read("t3.txt");
   		pass_records=(Vector<Records>)all_records.clone();

		s_input="yes";
   		while(s_input.compareTo("yes")==0)
   		{
	   		for(int i=0;i<pass_records.size();i++)
	   		{
	   			try
	   			{
		   			pass_records.get(i).print_record();
		   		}
		   		catch(IOException e) 
				{
					System.out.println("IOException");
				}
	   		}
	   		System.out.println();
   			user_input=uo.take_input();
			System.out.println("Choose one:");
			System.out.println("1. Without Synchronization");
			System.out.println("2. With Synchronization");
			try
			{
				i_input=Integer.valueOf(reader.readLine());
				while(i_input!=1 &&i_input!=2)
				{
					System.out.println("\nWRONG INPUT!!!!!  TRY AGAIN");
					System.out.println("Choose one:");
					System.out.println("1. Without Synchronization");
					System.out.println("2. With Synchronization");
					i_input=Integer.valueOf(reader.readLine());

				}
				System.out.println();
				if(i_input==2)
				{
					Thread t1 = new Thread(new with_sync((Vector)user_input.get(0),pass_records,record_flag,readWriteLock), "t1");
					Thread t2 = new Thread(new with_sync((Vector)user_input.get(1),pass_records,record_flag,readWriteLock), "t2");					
					t1.start();
					t2.start();
					try
					{
						t1.join();
						t2.join();
					}
					catch(InterruptedException e)
					{
						System.out.println("InterruptedException");
					}
				}
				else if(i_input==1)
				{
					Thread t1 = new Thread(new without_sync((Vector)user_input.get(0),pass_records,record_flag,readWriteLock), "t1");
					Thread t2 = new Thread(new without_sync((Vector)user_input.get(1),pass_records,record_flag,readWriteLock), "t2");
					t1.start();
					t2.start();
					try
					{
						t1.join();
						t2.join();
					}
					catch(InterruptedException e)
					{
						System.out.println("InterruptedException");
					}
				}
				
			}
			catch(IOException e) 
			{
				System.out.println("IOException");
			}
			System.out.println("\nCheck the output files\n");
			System.out.println("Do you want to continue? yes/no");
			try
			{
				s_input=reader.readLine();
				while(s_input.compareTo("yes")!=0 && s_input.compareTo("no")!=0 )
				{
					System.out.println("\nWRONG INPUT!!!!!  TRY AGAIN");
					System.out.println("Do you want to continue? yes/no");
					s_input=reader.readLine();
				}
				System.out.println();
			}
			catch(IOException e) 
			{
				System.out.println("IOException");
			}
		}

		
   }
   		

}