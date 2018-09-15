import java.io.*; 
import java.net.*; 
import java.util.Vector; 
class CustService implements Runnable {
    public final Vector<Item> items;
    public final Socket socket;
    public final Customer curr_cust;
    private DataInputStream in       =  null; 
    private DataOutputStream out       =  null; 
    CustService(Vector items, Socket socket,Customer curr_cust){
        this.items=items;
        this.socket=socket;
        this.curr_cust=curr_cust;
    }   
    public void printStocks(){
        G.print("\t Current Stocks: ");
        for (Item item: items){
            G.print(item.type+": "+(item.quantity==-1?"inf":item.quantity)+"\t");
        }
        G.println("");
    }
    @Override
    public void run(){
        System.out.println("Client "+curr_cust.name+" connected"); 
        printStocks();
        try{

            // takes input from the client socket 
           in = new DataInputStream( 
            new BufferedInputStream(socket.getInputStream())); 

            // sends output to the socket 
           out    = new DataOutputStream(socket.getOutputStream()); 
           sendName();

           String line = ""; 

    // reads message from client until "exit" is sent 
           while (!line.equals("exit")) 
           { 
            try
            { 
                line = in.readUTF(); 
                if(line.startsWith("set")){
                    G.print("Customer "+curr_cust.name+" changed details to ");
                    curr_cust.name=line.split(" ")[1];
                    curr_cust.address=line.split(" ")[2];
                    G.println(curr_cust.name+" at "+curr_cust.address);
                    sendName();
                    continue;
                }
                if(line.equals("allorders")){
                    sendOrders();
                    continue;
                }
                System.out.println("Got input : "+line);
                if(line.equals("") || line.split(",").length!=items.size()){
                    G.print("Invalid input: \""+line+"\"");
                    continue;
                }
                Vector quantities = new Vector<Integer>();
                for(String s : line.split(",")){
                    quantities.add(Integer.parseInt(s));
                }
                Order o = curr_cust.order(items,quantities);
                sendOrder(o);
                printStocks();
            } 
            catch(SocketException i) {
                return;
            }
        }

        System.out.println("Closing connection"); 
            // close connection 
        socket.close(); 
        in.close(); 

    }
    catch(IOException i) 
    { 
        System.out.println(i); 
        return;
    } 
}

public void sendName(){    
    try{
        out.writeUTF("Logged in as : "+curr_cust.name); 
    }
    catch(IOException ex){
        System.out.println("IOer: "+ex);
    }
}
public void sendOrder(Order o){    
    try{
        out.writeUTF(Order.header+o.get_receipt()+"\n\tExpected delivery: "+(o.exp_delivery/G.MINUTE_DURN)+" minutes."); 
    }
    catch(IOException ex){
        System.out.println("IOer: "+ex);
    }
}
public void sendOrders(){    
    String reply;
    if(curr_cust.orders.size()==0){
        reply="Nothing ordered yet!";
    }
    else{
        reply=Order.header;
        for(Order o : curr_cust.orders)
            reply+=o.get_receipt();
    }
    try{
        out.writeUTF(reply); 
    }
    catch(IOException ex){
        System.out.println("IOer: "+ex);
    }
}
}