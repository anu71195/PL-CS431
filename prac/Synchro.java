class First{
	public void display(String msg) {
		System.out.println("[");
		try {
			System.out.println(msg);
			Thread.sleep(500);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println ("]");
	}
}
class Second extends Thread{
	String msg;
	First fobj;
	Second (First fp,String str) {
		fobj = fp;
		msg = str;
		start();//jvm can now call the run() method
	}
	public void run() {
		synchronized(fobj){
			fobj.display(msg);
		}
	}
}
public class Synchro
{
	public static void main (String[] args) {
		First fnew = new First();
		Second ss = new Second(fnew, "H");
		Second ss1= new Second (fnew,"O");
		Second ss2 = new Second(fnew,"H");
	}
}