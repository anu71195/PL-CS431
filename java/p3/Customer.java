import java.io.*; 
import java.net.*; 
import java.util.Vector; 
class Customer implements Runnable {
    public final Socket socket;
    public final Vector<Order> orders;
    public String name,address;
    private DataInputStream in       =  null; 
    private DataOutputStream out       =  null; 
    Customer(Socket socket,String name,String address){
        this.name=name;
        this.address=address;
        this.socket=socket;
        this.orders= new Vector();
    }   

    public Order order(Vector<Integer> quantities){
        // Vector of {Itemtype itemtype, int quantity}
        Order o = new Order(this.name,this.address);
        int i=0,q;
        for(Item item : G.items){
            q = (Integer)quantities.get(i);
            if(q>0){
                o.add_record(item,q);
            }
            i++;
        }
        this.orders.add(o);
        // Synchronized by locks on ordered records
        o.clicked();
        return o;
    }

    public void printStocks(){
        G.print("\t Current Stocks: ");
        for (Item item: G.items){
            G.print(item.type+": "+(item.quantity==-1?"inf":item.quantity)+"\t");
        }
        G.println("");
    }
    @Override
    public void run(){
        G.println_t("Client "+this.name+" connected"); 
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
                    G.print("Customer "+this.name+" changed details to ");
                    this.name=line.split(" ")[1];
                    this.address=line.split(" ")[2];
                    G.println(this.name+" at "+this.address);
                    sendName();                    
                }
                else if(line.equals("availability")){
                    sendAvls();                    
                }
                else if(line.equals("allorders")){
                    sendOrders();                    
                }
                else{
                    // order
                    G.println_t("Got Order string : "+line);
                    if(line.equals("") || line.split(",").length!=G.items.size()){
                        G.print("Invalid input: \""+line+"\"");
                        continue;
                    }
                    Vector quantities = new Vector<Integer>();
                    boolean nonzero=false;

                    for(String s : line.split(",")){
                        int x=Integer.parseInt(s);
                        quantities.add(x);
                        if(x!=0)nonzero=true;
                    }

                    if(nonzero){
                        Order o = this.order(quantities);
                        sendOrder(o);
                    }
                    else{
                        sendMessage("Empty Order!");
                    }
                    printStocks();
                }
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

public void sendMessage(String msg){    
    try{
        out.writeUTF(msg); 
        out.writeUTF("<END>");
    }
    catch(IOException ex){
        System.out.println("IOer: "+ex);
    }
}
public void sendName(){    
    sendMessage("Customer: "+this.name+" at address: "+this.address);
    G.println("Customer: "+this.name+" at address: "+this.address);
}
public void sendOrder(Order o){    
    sendMessage(Order.header+o.get_receipt()+"\n\tExpected delivery: "+(o.rejected?"ORDER REJECTED!":(o.exp_delivery/G.MINUTE_DURN)+" minutes."));
}
public void sendAvls(){    
    /*
    No use of lock on availablity. because by the time response reaches client, the other thread would have updated item quantities anyway.
    */
    String reply="";
    int i=0;
    for(Item it : G.items){
        reply+=it.quantity;
        if(i!=G.items.size())
            reply+=",";
        i++;
    }
    sendMessage(reply);
}
public void sendOrders(){    
    String reply;
    if(this.orders.size()==0){
        reply="Nothing ordered yet!";
    }
    else{
        reply=Order.header;
        for(Order o : this.orders)
            reply+=o.get_receipt();
    }
    sendMessage(reply);
}
}