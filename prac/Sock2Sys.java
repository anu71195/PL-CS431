import java.net.*; 
import java.io.*; 

public class Sock2Sys implements Runnable{
    private final Socket socket; 
    private final DataInputStream  in; 
    Sock2Sys(Socket socket ,DataInputStream  in){
        this.socket=socket;
        this.in=in;
    }
    public void run(){
        // string to read message from in 
        String line = ""; 
        // keep reading until "Over" is in 
        while (!line.equals("Over")) 
        { 
            try {
                line = in.readUTF(); 
                System.out.println(line); 
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
        System.out.println("Disconnected"); 
        // close the connection 
        try
        { 
            in.close(); 
            socket.close(); 
        } 
        catch(IOException i) 
        { 
            System.out.println("IOEr: "+i); 
        } 
    }
}