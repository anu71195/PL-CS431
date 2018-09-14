import java.util.Collections;
import java.util.Vector;
public class Hello {
	public static Vector a;
	Hello(){
		a = new Vector();
		System.out.println("Yo");
	}
	public static void main(String [] args) {
		new Hello();
		a.add(1);
		fn(a);
		System.out.println(a.get(0));
	}
	public static void fn(Vector a){
		Vector b =(Vector) a.clone();
		b.add(0,-1);
		System.out.println(b.get(0));
	}
}