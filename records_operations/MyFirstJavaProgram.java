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

		// readWriteLock.readLock().lock();

		    // multiple readers can enter this section
		    // if not locked for writing, and not writers waiting
		    // to lock for writing.

		// readWriteLock.readLock().unlock();


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
	}
}

public class MyFirstJavaProgram 
{
   public static void main(String []args) 
   {
   		file_op fo=new file_op();
		Vector<Records> v = new Vector<Records>(); 
		Vector<Records>  all_records=new Vector<Records> ();
		Vector<Records>  pass_records=new Vector<Records> ();//recordsto be passed as the argumentfirst needs to be cloned
		Vector user_input=new Vector();
		vec_op vop=new vec_op();
		user_op uop=new user_op();
		String filename="t.txt";
		Records record=new Records();
		sort_op so = new sort_op();
		int i_input;
		String s_input,priority_input;
		BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in));
		ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
						Map< Integer,Integer> record_flag =  new HashMap< Integer,Integer>(); 

					// System.out.println("Set priority? (yes/no)");
					// priority_input=reader.readLine();
					// if(priority_input.compareTo("yes")==0)
					// {
					// 	System.out.print("thread 1 priority : ");
					// 	t1_priority=Integer.valueOf(reader.readLine());
					// 	System.out.print("thread 2 priority : ");
					// 	t2_priority=Integer.valueOf(reader.readLine());
					// }
   		////////////////////////////////////////////////////////////////////////////////
   		
   		all_records=fo.read("t3.txt");
   		pass_records=(Vector<Records>)all_records.clone();
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
		s_input="yes";
   		while(s_input.compareTo("yes")==0)
   		{
   			user_input=uop.take_input();
			System.out.println("Choose one:");
			System.out.println("1. Without Synchronization");
			System.out.println("2. With Synchronization");
			// System.out.println(((String)((Vector)user_input.get(0)).get(0)).compareTo("CC"));
			// if(((String)((Vector)user_input.get(0)).get(0)).compareTo("CC")==0)t1_priority=6;
			// else if(((String)((Vector)user_input.get(1)).get(0)).compareTo("CC")==0)t2_priority=6;
			try
			{
				i_input=Integer.valueOf(reader.readLine());
				if(i_input==2)
				{
					Thread t1 = new Thread(new with_sync((Vector)user_input.get(0),pass_records,record_flag,readWriteLock), "t1");
					Thread t2 = new Thread(new with_sync((Vector)user_input.get(1),pass_records,record_flag,readWriteLock), "t2");					
					t1.start();
					t2.start();
				}
				else if(i_input==1)
				{
					Thread t1 = new Thread(new without_sync((Vector)user_input.get(0),pass_records,record_flag,readWriteLock), "t1");
					Thread t2 = new Thread(new without_sync((Vector)user_input.get(1),pass_records,record_flag,readWriteLock), "t2");
					t1.start();
					t2.start();
				}
				
			}
			catch(IOException e) 
			{
				System.out.println("IOException");
			}
			System.out.println("Check the output files");
			System.out.println("Do you want to continue? yes/no");
			try
			{
				s_input=reader.readLine();
			}
			catch(IOException e) 
			{
				System.out.println("IOException");
			}
		}

		
   }
   		

}






// The class demonstrates how to get an exclusive file lock that prevents other threads and processes / JVMs from
// obtaining a lock on the same file. To do this, you need to synchronize a block on a file and acquire FileLock. See
// comments below for more details. Run this class in multiple JVMs and see each thread of each JVM acquires a lock in
// an orderly fasion.
// public class FileLocking extends Thread
// {
//     private static final File file = new File("lock.test");
    
//     public static void main(String[] args) throws Exception
//     {
//         for (int i = 0; i < 5; i++) {
//             new FileLocking().start();
//         }
//     }

//     @Override
//     public void run()
//     {
//         // synchronized on a static variable "file" so that threads don't try to acquire a lock on it at the same
//         // time. Javadoc of FileLock states "File locks are held on behalf of the entire Java virtual machine. They are
//         // not suitable for controlling access to a file by multiple threads within the same virtual machine." What this
//         // actually means is that if two threads tries to get a lock on the same file, one fails with
//         // OverlappingFileLockException instead of waiting for a release of the lock.
//         synchronized(file) {
//             FileOutputStream fos = null;
//             try {
//                 fos = new FileOutputStream(file);

//                 // Synchronization doesn't have any effect on threads running on a different JVM, of
//                 // course. FileChannel.lock() acquires a lock on a file that prevents another process from getting a
//                 // lock on it. The method waits until a lock is released if the lock is held by a different process, as
//                 // opposed to throwing exception when the lock is held by a thread in the same JVM.
//                 FileLock lock = fos.getChannel().lock();
//                 System.out.println("Got a lock in " + getName());

//                 // do some file write operation

//                 sleep(1000);
//                 fos.close();
//             } catch (Exception e) {
//                 e.printStackTrace();
//             } finally {
//                 if (fos != null) {
//                     try {
//                         fos.close();
//                     } catch (IOException ex) {
//                     }
//                 }
//             }
//         }
//     }
            
// }