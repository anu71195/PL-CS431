import java.util.*; 
public class Hello {
	public static Vector a;
	public static HashMap< Integer,Integer> h= new HashMap< Integer,Integer>();
	public static HashMap< Integer,Integer> h2;
	Hello(){
		a = new Vector();
		h.put(1,2);
		h.put(2,3);
		System.out.println("Yo");
	}
	public static void main(String [] args) {
		new Hello();
		h2= new HashMap< Integer,Integer>();
		// a.add(1);
		// fn(a);
		// System.out.println(a.get(0));
		h2= new HashMap< Integer,Integer>();
		h2.putAll(h);
		h.put(3,3);
		System.out.println(h);
		h=h2;
		System.out.println(h);
	}
	public static void fn(Vector a){
		Vector b =(Vector) a.clone();
		b.add(0,-1);
		System.out.println(b.get(0));
	}
}