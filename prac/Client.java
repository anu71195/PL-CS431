// A Java program for a Client 
import java.net.*; 
import java.io.*; 

public class Client 
{ 
    // initialize socket and input output streams 
    private Socket socket            = null; 
    private DataInputStream  sys_in   = null; 
    private DataInputStream  sock_in   = null; 
    private DataOutputStream sock_out     = null; 

    // constructor to put ip address and port 
    public Client(String address, int port) 
    { 
        // establish a connection 
        try
        { 
            socket = new Socket(address, port); 
            System.out.println("Connected"); 

            // takes input from terminal 
            sys_in  = new DataInputStream(System.in); 
            // sends output to the socket 
            sock_out    = new DataOutputStream(socket.getOutputStream()); 
            sock_in  = new DataInputStream(socket.getInputStream()); 
            // route sys_in to sock_out   
            Thread r1 = new Thread(new Sys2Sock(socket, sys_in,sock_out));  
            // route  sock_in to sys_out   
            Thread r2 = new Thread(new Sock2Sys(socket, sock_in));  
            r1.start();
            r2.start();
        } 
        catch(UnknownHostException u) 
        { 
            System.out.println("UHerr: "+u); 
        } 
        catch(IOException i) 
        { 
            System.out.println("Could not connect: "+i); 
        } 
    } 

    public static void main(String args[]) 
    { 
        Client client = new Client("127.0.0.1", 5000); 
    } 
} 