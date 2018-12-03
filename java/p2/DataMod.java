import java.io.*;  
import java.util.*; 
import java.lang.*; 
import java.util.concurrent.locks.*;

class file_op //cass to do file operations
{  

	void write_m(Vector<Records> input_v,String filename, boolean append)//write function wwith option of append or not
	{
		try
		{    
			FileWriter fw=new FileWriter(filename,append);    
			for(int i=0;i<input_v.size();i++)
			{
				Records current_record=new Records();
				current_record=input_v.get(i);//get the ith record and store it in current record
				fw.write(""+current_record.roll+","+current_record.name+","+current_record.email+","+current_record.marks+","+current_record.teacher+"\n");//now store the current record in the file
			}
			fw.close();    
		}
		catch(Exception e){System.out.println(e);}      
	}
	void write(Vector<Records> input_v,String filename){//write to the file
		write_m(input_v,filename,false);	
	}
	Vector<Records> read(String filename){//reading the file
		String line = null;
		Vector<Records> output_v = new Vector<Records>(); 
		try 
		{
			FileReader fileReader = new FileReader(filename);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while((line = bufferedReader.readLine()) != null) //tille the pointer reaches the end of the file read the line and store it in the line and storing it in the record
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
			bufferedReader.close();    //closing the buffer
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
    // Used for sorting in ascending order by roll number 
	public int compare(Records a, Records b) 
	{ 
		return a.roll - b.roll; 
	} 
} 
class Sortbyname implements Comparator<Records> 
{ 
    // Used for sorting in ascending order by name
	public int compare(Records a, Records b) 
	{ 
		return a.name.compareTo(b.name); 
	} 
} 

class sort_op//sorting operations
{
	void sortbyroll(Vector<Records> input_v)//sort records by roll
	{
		Collections.sort(input_v,new Sortbyroll());
	}
	void sortbyname(Vector<Records> input_v)//sort records byname
	{
		Collections.sort(input_v,new Sortbyname());
	}

}

class Records//records class
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
	Records(){}
	Records(Records r){
		this.roll=r.roll;
		this.marks=r.marks;
		this.name=r.name;
		this.email=r.email;
		this.teacher=r.teacher;
	}
}

class vec_op//vector operations
{
	void print_vector(Vector  input)//print vector
	{
		for(int i=0;i<input.size();i++)
		{
			System.out.println(input.get(i));
		}
		System.out.println("\n");
	}
	Vector initialize_zeros(int n)//creating the vector with initialized n zeros in it
	{
		Vector output=new Vector();
		for(int i=0;i<n;i++)
		{
			output.add(0);
		}
		return output;
	}
}
class user_op//user operations
{
	Vector take_input()
	{
		Vector all_output=new Vector();
		int i_input;
		String s_input;
		// At a time take input 2 times
		for(int j=0;j<2;j++)//take input two times for two threads
		{
			Vector output=new Vector();
			BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in));
			try 
			{
				while(true){//getteacher's name with error handling
					System.out.print("Enter Teacher's Name(CC/TA1/TA2): ");
					s_input = reader.readLine(); 
					if(s_input.compareTo("CC")!=0 && s_input.compareTo("TA2")!=0 && s_input.compareTo("TA1")!=0)
						System.out.println("\nWRONG INPUT!!!!!  TRY AGAIN");
					else 
						break;
				}
				output.add(s_input);

				System.out.print("Enter Student Roll number: ");
				while(true){//get student rollnumber
					try{
						i_input=Integer.valueOf(reader.readLine());
						break;
					}
					catch(NumberFormatException e){
						System.out.println("\nWRONG INPUT!!!!!  TRY AGAIN");
					}
				}
				output.add(i_input);

				System.out.println("Update Mark : 1. Increase");
				System.out.println("              2. Decrease");
				i_input=Integer.valueOf(reader.readLine());
				while(i_input!=1 && i_input!=2)//get whether the marks be decreased or increased 
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

class with_sync implements Runnable//class that implements threads with sync
{
	String teacher;
	int roll,id_op,marks_id;//id_op increment decrement operation, marks_id marks incrementdecrement
	Vector<Records>  records;
	Vector input;
	HashMap< Integer,Integer>record_flag;
	ReadWriteLock recordLock;//fileLock,
	Vector<ReadWriteLock> recordLocks;
	public with_sync(Vector input,Vector<Records>  pass_records,HashMap< Integer,Integer> record_flag,Vector<ReadWriteLock> recordLocks)//constructor intializes the object with these values
	{
		this.input=input;
		this.record_flag=  record_flag;
		teacher=(String)input.get(0);
		this.roll=(int)input.get(1);
		id_op=(int)input.get(2);
		marks_id=(int)input.get(3);
		records=pass_records;
		// this.fileLock=fileLock;
		this.recordLocks=recordLocks;
	}
	@Override
	public void run()//on the start of the thread
	{	
		// System.out.println("("+Thread.currentThread().getName()+") "+teacher+" started..");
		sort_op so = new sort_op();
		file_op fo = new file_op();
		int current_marks,current_roll;
		Random rand = new Random();
		// For file write safety, required even for without synchronization else system will give error
		for(int i=0;i<records.size();i++)	//for each record
		{
			// System.out.println(teacher + ">"+ records.get(i).roll);
			recordLock=(ReadWriteLock)recordLocks.get(i);//get the lock for the ith record
			recordLock.readLock().lock();//read lock
			current_marks=records.get(i).marks;
			current_roll=records.get(i).roll;
			if(current_roll==this.roll){//if the roll number in the database
				// no write lock here - add random sleep to introduce unevenness
				try{
					Thread.sleep(rand.nextInt(100));//sleep randomly between 0 and 100milliseconds
				}
				catch(InterruptedException ex){
					System.out.println(ex);
				}
				// System.out.println(teacher + ">"+i+"<"+ records.get(i).roll);
				if(record_flag.get(roll)==null || teacher.equals("CC"))//if the teacher is cc
				{
					recordLock.readLock().unlock();//unlock the readlock
					// ^ have to unlock before getting write lock to avoid a deadlock: 
					// more at https://stackoverflow.com/questions/16901640/why-in-reentrantreadandwritelock-the-readlock-should-be-unlocked-before-write
					
					// here other thread might acquire writelock to update marks
					// current_marks=records.get(i).marks; //get latest again
					// here other thread might acquire writelock to update marks
					
					// also other thread might update record_flag too
					recordLock.writeLock().lock();//acquire write lock

					if(teacher.equals("CC")){
						record_flag.put(roll, new Integer(1)); //updat the record flag so that now ta1 and ta2 won't be able to updat the same record
					}
					
					if(record_flag.get(roll)==null || teacher.equals("CC")){ // get latest condition again
						//get latest value again
						current_marks=records.get(i).marks; 
						// do the write
						records.get(i).marks+= (id_op==1?1:-1)*marks_id;
						System.out.println("("+Thread.currentThread().getName()+") "+teacher+": Updated marks: "+current_marks+" -> "+records.get(i).marks);
					}
					else{ 
						System.out.println("("+Thread.currentThread().getName()+") "+teacher+": Couldn't Update marks: "+current_marks+" -> "+records.get(i).marks);
					}
					// get readlock before leaving writelock! that's smart!
					recordLock.readLock().lock();//get the red lock
					recordLock.writeLock().unlock();//release write lock
				}
				else{
					System.out.println("("+Thread.currentThread().getName()+") "+teacher+": Couldn't Update marks: "+current_marks+" -> "+records.get(i).marks);
				}
			}
			recordLock.readLock().unlock();//release read lock
		}		
	}
}

class without_sync implements Runnable//class that implements threads without sync
{	
	String teacher;
	int roll,id_op,marks_id;//id_op increment decrement operation, marks_id marks incrementdecrement
	Vector<Records>  records;
	Vector input;
	HashMap< Integer,Integer>record_flag;
	// ReadWriteLock fileLock;
	public without_sync(Vector input,Vector<Records>  pass_records,HashMap< Integer,Integer> record_flag)//constructor intializes the object with these values
	{
		this.input=input;
		this.record_flag=  record_flag;
		teacher=(String)input.get(0);
		roll=(int)input.get(1);
		id_op=(int)input.get(2);
		marks_id=(int)input.get(3);
		records=pass_records;
		// this.fileLock=fileLock;
	}
	@Override
	public void run()//on the start of the thread
	{
		// System.out.println("("+Thread.currentThread().getName()+") "+teacher+" started..");
		sort_op so = new sort_op();
		file_op fo=new file_op();
		int current_marks,current_roll;
		Random rand = new Random();
		for(int i=0;i<records.size();i++)	//for each record in the database
		{
			Records r=records.get(i);//get ith record 
			current_marks=r.marks;
			current_roll=r.roll;
			if(current_roll==this.roll){//check if the roll number given as input is in the database
				if(record_flag.get(roll)==null||teacher.equals("CC"))//if roll number in database check the teacher priority
				{
					// no write lock here
					try{
						Thread.sleep(rand.nextInt(100));//randomly sleep betewen 0 to 100 milliseconds
					}
					catch(InterruptedException ex){
						System.out.println(ex);
					}
					if(teacher.equals("CC"))//if teacher is CC
						record_flag.put(roll, new Integer(1));//update record flag htati snow ta1 and ta2 can't modify this record 
					r.marks=current_marks+((id_op==1)?1:-1)*marks_id;//update the marks
					System.out.println("("+Thread.currentThread().getName()+") "+teacher+": Updated marks: "+current_marks+" -> "+records.get(i).marks);
				}
				else
					System.out.println("("+Thread.currentThread().getName()+") "+teacher+": Couldn't Update marks: "+current_marks+" -> "+records.get(i).marks);
			}
		}
	}
}

public class DataMod//main class
{
	public static void main(String []args) //main function
	{
		//initializations
		file_op fo=new file_op();			
		Vector user_input=new Vector();
		vec_op vo=new vec_op();
		user_op uo=new user_op();
		sort_op so = new sort_op();
		int i_input;
		String s_input;
		BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in));
		// ReadWriteLock fileLock = new ReentrantReadWriteLock();
		HashMap< Integer,Integer> record_flag =  new HashMap< Integer,Integer>(); 
		HashMap< Integer,Integer> record_flag_prev =  new HashMap< Integer,Integer>(); 

			//records to be passed as the argument first needs to be cloned
		Vector<Records>  all_records=fo.read("studInfo.txt");
		so.sortbyroll(all_records);//sort records with respect to roll
		
		Vector<ReadWriteLock> recordLocks = new Vector<ReadWriteLock>();
		for(int i=0; i<all_records.size();i++)
			recordLocks.add(new ReentrantReadWriteLock());//making locks for each record

   		////////////////////////////////////////////////////////////////////////////////

		Vector<Records>  pass_records;//records that would be passed as parameters to the functions used subsequently
		pass_records = new Vector<Records>();
		for(Records r : all_records){
			pass_records.add(new Records(r));
		}
		s_input="no";
		while(true)
		{
			if(s_input.equals("yes")){
				// restore file before current loop
				record_flag=record_flag_prev;
				pass_records = new Vector<Records>();
				for(Records r : all_records){
					pass_records.add(new Records(r));
				}
			}else{
				// update all_records from file
				all_records=fo.read("studInfo.txt");
				// keep record_flag updated
			}
			// copy hashmap before entering current loop
			record_flag_prev =  new HashMap< Integer,Integer>();
			record_flag_prev.putAll(record_flag);

			System.out.println("\nCurrent flags: "+record_flag);
			System.out.println("\nCurrent file: ");
			for(Records r : pass_records)//printing all the records
			{
				try {r.print_record(); }
				catch(IOException e) {System.out.println("IOException"); }
			}
			System.out.println();
			if(s_input.equals("no")){
				user_input=uo.take_input();
			}
			System.out.println("Choose one:");
			System.out.println("1. Without Synchronization");
			System.out.println("2. With Synchronization");
			try
			{
				i_input=Integer.valueOf(reader.readLine());//input forwhether the user needs to run the pgoram with syncronization or without it 1 stands for without sync and 2 for with ysnc
				while(i_input!=1 &&i_input!=2)//error handling loop if the input for with and without synchronization is not given properly
				{
					System.out.println("\nWRONG INPUT!!!!!  TRY AGAIN");
					System.out.println("Choose one:");
					System.out.println("1. Without Synchronization");
					System.out.println("2. With Synchronization");
					i_input=Integer.valueOf(reader.readLine());
				}
				System.out.println();
				Thread t1,t2;
				if(i_input==1)//without synchronization
				{
					t1 = new Thread(new without_sync((Vector)user_input.get(0),pass_records,record_flag), "t1");//initializing thread 1
					t2 = new Thread(new without_sync((Vector)user_input.get(1),pass_records,record_flag), "t2");//intializing thread 2
				}
				else//with synchronization
				{
					t1 = new Thread(new with_sync((Vector)user_input.get(0),pass_records,record_flag,recordLocks), "t1");//intializing thread 1
					t2 = new Thread(new with_sync((Vector)user_input.get(1),pass_records,record_flag,recordLocks), "t2");//intializing thrad 2
				}				
				//start threads
				t1.start();//start thread 1
				t2.start();//start thrad 2
				try
				{
					// Join main thread with others i.e. wait for both threads to finish
					t1.join();
					t2.join();									
				}
				catch(InterruptedException e)
				{
					System.out.println("InterruptedException");
				}
			}
			catch(IOException e) 
			{
				System.out.println("IOException");
			}

			System.out.println("\nCheck the output files\n");
			// fileLock.writeLock().lock(); <- no need

			so.sortbyname(pass_records);//sort the records by name
			fo.write(pass_records,"Sorted_Name.txt");//store them in the file sorted_name.txt
			System.out.println("\nSorted by Name\n");
			for(Records r : pass_records)
			{
				try {r.print_record(); }
				catch(IOException e) {System.out.println("IOException"); }
			}

			so.sortbyroll(pass_records);//sort the records by roll
			fo.write(pass_records,"Sorted_Roll.txt");//store them in sorted_roll.txt
			fo.write(pass_records,"studInfo.txt");//update the original file with latest values
			System.out.println("\nSorted by Roll\n");
			for(Records r : pass_records)//print all the records
			{
				try {r.print_record(); }
				catch(IOException e) {System.out.println("IOException"); }
			}

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
			}
			catch(IOException e) 
			{
				System.out.println("IOException");
			}
			System.out.println();
		}
	}
}