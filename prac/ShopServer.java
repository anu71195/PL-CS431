import java.net.*; 
import java.io.*; 
import java.util.Scanner;
import java.util.logging.Level;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import java.util.Random; 
import java.text.SimpleDateFormat; 
import java.util.Vector; 
import java.util.EnumMap; 
import java.util.concurrent.Semaphore; 
/*
No guarantees are made about fairness of the threads acquiring permits from the Semaphore. That is, there is no guarantee that the first thread to call acquire() is also the first thread to obtain a permit. If the first thread is blocked waiting for a permit, then a second thread checking for a permit just as a permit is released, may actually obtain the permit ahead of thread 1.
If you want to enforce fairness, the Semaphore class has a constructor that takes a boolean telling if the semaphore should enforce fairness.
    // Semaphore's arg true ensures FCFS

*/
enum Itemtype 
{ 
    COOKIES, SNACKS, TEA ,COFFEE
};
class G{
    public final static long start_time=time();
    public final static int T_PRICE=10,
    O_PRICE=20,
    O_QUANT=20,
    O_THR=10,
    PKG_TIME=2000,
    DLRY_TIME=5000;
    /*
    static means exist without instantiation!
    */
    public final static EnumMap<Itemtype, Integer> Prices = new EnumMap<Itemtype, Integer>(Itemtype.class);    
    public final static Itemtype itemtypes[]= Itemtype.values();
    public final static Random rand = new Random(); 

    public static long time(){
        return System.currentTimeMillis();
    }
    public static void println(String msg){
        System.out.println(msg);
    }
    public static void print(String msg){
        System.out.print(msg);
    }
    public static LocalDateTime currDate= LocalDateTime.now();
    public static int day_number=0;
    public static String timestamp(){        
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(currDate.plusDays(day_number));
    }
}

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
         String line = ""; 

    // reads message from client until "exit" is sent 
         while (!line.equals("exit")) 
         { 
            try
            { 
                line = in.readUTF(); 
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
                // G.println(o.get_receipt());
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

public void sendOrder(Order o){    
    try{
        out.writeUTF(o.get_receipt()); 
    }
    catch(IOException ex){
        System.out.println("IOer: "+ex);
    }
}
public void sendOrders(){    
    String reply="";
    for(Order o : curr_cust.orders){
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
public class ShopServer implements Runnable 
{ 
    //initialize socket and input stream 
    private ServerSocket    server   = null; 
    private int cust_count  =   1,day_number=0;
    private final Scanner in;
    public final Vector<Integer> dailySales;
    public final Vector<Item> items;
    public final Vector<Customer> customers;
    
    // constructor with port 
    public ShopServer(int port){
        in = new Scanner(System.in);
        // starts server and waits for a connection 
        dailySales = new Vector<Integer>();
        items = new Vector<Item>();        
        customers = new Vector<Customer>();        

    // Initialize item prices and stock quantities
        for(Itemtype it : G.itemtypes){
            if(it == Itemtype.TEA || it == Itemtype.COFFEE){
                G.Prices.put(it,G.T_PRICE);
                items.add(new Item(it,-1, -1,G.Prices.get(it),new Semaphore(1,true)));
            }
            else{
                G.Prices.put(it,G.O_PRICE);
                items.add(new Item(it,G.O_QUANT, G.O_THR, G.Prices.get(it),new Semaphore(1,true)));
            }
        }     
    }  

    public void run(){
        try{
            server = new ServerSocket(port); 
            System.out.println("ShopServer started"); 
            Thread r1 = new Thread(new ServerHandle());  
            r1.start();
            // start taking input

            System.out.println("Waiting for a client ..."); 
            while(true)
            {
                Socket socket = server.accept(); 
                G.day_number++;
            // added thru sockets accept
                customers.add(new Customer("C"+cust_count));
         //TODO: make this THREAD SAFE?
                Customer curr_cust = customers.get(cust_count-1);
                cust_count++;
                Thread a = new Thread(new CustService(items,socket,curr_cust), curr_cust.name);
                a.start();   
            } 
        } catch(IOException  i){
            System.out.println(i); 
        } 
    }    
    public void printOrders(){    
        G.println("");
        for(Customer c : this.customers){
            for(Order o : c.orders){
                G.println(o.get_receipt());
            }                
        }                
    }

    public void printDayWiseStats(){    
        G.println("");
        for(Customer c : this.customers){
            for(Order o : c.orders){
                G.println(o.get_receipt());
            }                
        }                
    }
    public static void main(String args[]) 
    { 
        Thread shopServer = new Thread(new ShopServer(5000),"ShopServer"); 
        shopServer.start();
        int menuChoice,start,end;
        while(true){
            G.print("1. Print Orders\n2. Print Stats");
            menuChoice=in.nextInt();
            if(menuChoice==1)
                shopServer.printOrders();
            else{
                G.print("Enter range of days (0<=i<=j<="+G.day_number+"): ");
                start=in.nextInt(); end=in.nextInt();
                if(start>=0 && start<=end && end <= G.day_number){
                    shopServer.printDayWiseStats(start,end);
                }
                else{
                    G.println("Invalid input!");
                }
            }
        }
    } 
} 
class Customer{
    public final Vector<Order> orders;
    public final String name;
    Customer(String name){
        this.name=name;
        this.orders= new Vector();
    }
    public Order order(Vector<Item> items, Vector<Integer> quantities){
        // Vector of {Itemtype itemtype, int quantity}
        Order o = new Order(this.name);
        int i=0,q;
        for(Item item :items){
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
}

class Item {
    //reference
    public final Itemtype type;
    public final Semaphore sema;
    public final int rate,THR;
    public int quantity;
    Item(Itemtype type,int quantity,int threshold,int rate, Semaphore sema){
        this.type=type;
        this.sema=sema;
        this.rate=rate;
        this.THR=threshold;
        this.quantity=quantity;
    } 
}

class Record {
    // one time definition
    public final String t;
    public final int quantity, rate, price;
    public final long t_ms;
    // changables
    public boolean rejected;
    public Item item;
    Record(String t, Item item, int quantity){
        this.t=t;
        this.t_ms=G.time();
        this.item=item;
        this.rate=item.rate;
        this.quantity=quantity;
        this.price=item.rate*quantity;
    }
}
class Order {
    //reference
    private static int order_count=0;
    private final String cust_name;
    private final int id;
    public boolean rejected;
    public long sold_at;//milliseconds
    Vector<Record> records;
    Order(String cust_name){
        this.order_count++;//on each instance
        this.id=this.order_count; //store current val
        this.cust_name=cust_name;
        this.records = new Vector();
        this.rejected = false;
        this.sold_at = -1;
    }
    public String get_receipt(){
        String receipt="Order no: "+this.id+", Customer: "+cust_name+", Status: ";
        receipt+=(this.rejected?"Out of stock":("Sold at: "+(this.sold_at-G.start_time)+"ms"))+"\n";
        receipt+="timestamp,\t t_ms,\t item,\t rate,\t ordered,\t price\n";
        for (Record r : records){
            receipt+=r.t+",\t";
            receipt+=(r.t_ms-G.start_time)+"ms,\t";
            receipt+=r.item.type+",\t";
            receipt+=r.rate+",\t"+r.quantity+",\t"+r.price+",\n";
        }
        return receipt;
    }
    public void add_record(Item item, int quantity){
        records.add(new Record(G.timestamp(),item,quantity));
    }
    public void clicked(){
        // acquire lock on each record types
        for(Record r : records){
            try{
                r.item.sema.acquire();
            }
            catch(InterruptedException ex){
                Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        boolean all_avl=true;
        for(Record r : records){
            if(r.item.quantity>-1 && r.item.quantity - r.quantity < r.item.THR){
                all_avl=false;
                break;
            }
        }
        if(!all_avl){
            //reject
            this.rejected=true;
            // //reject records?
            // for( r : records)
            //     r.rejected=true;
        }
        else{
             //modify
            for(Record r : records){
                if(r.item.quantity>-1){
                    r.item.quantity -= r.quantity;
                }
            }
            this.sold_at=G.time();
            // Deliver after
        }

        for(Record r : records){
            r.item.sema.release();
        }
    }    
}
