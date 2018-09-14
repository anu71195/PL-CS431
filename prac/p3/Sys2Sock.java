import java.net.*; 
import java.io.*; 
public class Sys2Sock implements Runnable{
    private final Socket socket; 
    private final DataInputStream  in; 
    private final DataOutputStream out; 
    Sys2Sock(Socket socket ,DataInputStream  in ,DataOutputStream out){
        this.socket=socket;
        this.in=in;
        this.out=out;
    }
    public void run(){
        // string to read message from in 
        String line = ""; 
        while (true) 
        { 
            try {
                line = in.readLine(); 
                out.writeUTF(line); 
                // out.flush();
            } 
            catch(SocketException i) {
                break;
            }
            catch(IOException i) {
                System.out.println("IOex: "+i); 
                break;
            }
        } 
    }
} 
