import java.io.*; 
import java.net.*; 
// import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.Vector; 
import java.util.concurrent.Semaphore; 
import java.util.logging.Level;
import java.util.logging.Logger;
/*
No guarantees are made about fairness of the threads acquiring permits from the Semaphore. That is, there is no guarantee that the first thread to call acquire() is also the first thread to obtain a permit. If the first thread is blocked waiting for a permit, then a second thread checking for a permit just as a permit is released, may actually obtain the permit ahead of thread 1.
If you want to enforce fairness, the Semaphore class has a constructor that takes a boolean telling if the semaphore should enforce fairness.
    // Semaphore's arg true ensures FCFS

*/
class TeaMachine implements Runnable {
    public void run(){
        while(true){
            try{
                Thread.sleep(G.PREP_TIME*G.MINUTE_DURN*1000);
            // }
            // catch(InterruptedException ex){
            //     Logger.getLogger(TeaMachine.class.getName()).log(Level.SEVERE, null, ex);
            // }
            // try{
                G.teaSema.acquire();
                if(G.teaCounter>0)G.teaCounter--;
                G.println("Pending Teas: "+G.teaCounter);
                G.teaSema.release();
            }
            catch(InterruptedException ex){
                Logger.getLogger(TeaMachine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
class CoffeeMachine implements Runnable {
    public void run(){
        while(true){
            try{
                Thread.sleep(G.PREP_TIME*G.MINUTE_DURN*1000);
            // }
            // catch(InterruptedException ex){
            //     Logger.getLogger(TeaMachine.class.getName()).log(Level.SEVERE, null, ex);
            // }
            // try{
                G.coffeeSema.acquire();
                if(G.coffeeCounter>0)G.coffeeCounter--;
                G.println("Pending Coffees: "+G.coffeeCounter);
                G.coffeeSema.release();
            }
            catch(InterruptedException ex){
                Logger.getLogger(CoffeeMachine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
public class ShopServer implements Runnable 
{ 
    //initialize socket and input stream 
    private ServerSocket    server   = null; 
    private int cust_count  =   1,day_number=0,port;
    private static Scanner in = new Scanner(System.in);
    public final Vector<Integer> dailySales;
    public final Vector<Item> items;
    public final Vector<Customer> customers;

    // constructor with port 
    public ShopServer(int port){
        this.port = port;

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
            System.out.println("Waiting for a client ..."); 
            while(true)
            {
                Socket socket = server.accept();
                //Vector is Thread safe - _/TODO: make this THREAD SAFE
                customers.add(new Customer("C"+cust_count,"A"+cust_count));
                Customer curr_cust = customers.get(cust_count-1);
                cust_count++;
                Thread a = new Thread(new CustService(items,socket,curr_cust), curr_cust.name);
                a.start();   
            } 
        } catch(IOException  i){
            System.out.println(i);
            System.out.println("Server exiting");
            System.exit(0); 
        } 
    }    
    public void printPurchaseList(){
        for(Item i : items){
            if(i.to_purchase)
                G.println(i.type+" "+i.quantity);
        }

    }    
    public void printOrders(){    
        G.println("");
        for(Customer c : this.customers){
            for(Order o : c.orders){
                G.print(Order.header);
                G.println(o.get_receipt());
            }                
        }                
    }

    public void printDayWiseStats(int start, int end){
        Vector<Vector<String> > receipts = new Vector<Vector<String> >(end-start+1);
        G.println("");
        for(Customer c : this.customers){
            for(Order o : c.orders){
                if(o.sold_day>=start && o.sold_day<=end)
                    receipts.get(o.sold_day).add(o.get_receipt());
            }                
        }                    
        for(Vector<String> rs : receipts){
            G.println("\n\t Day "+start);
            G.print(Order.header);
            for(String r : rs){
                G.println(r);
            }
        }
    }
    public static void main(String args[]) 
    { 
        ShopServer shopServer = new ShopServer(5000);
        Thread ss = new Thread(shopServer,"ShopServer"); 
        ss.start();
        Thread tm = new Thread(new TeaMachine(),"TeaMachine"); 
        Thread cm = new Thread(new CoffeeMachine(),"CoffeeMachine"); 
        tm.start();
        cm.start();

        int menuChoice,start,end;
        while(true){
            G.println("1. Print Orders\n2. Print Stats\n3. Increase Day\n4. Show Purchase List");
            menuChoice=in.nextInt();
            switch(menuChoice){
                case 1:
                shopServer.printOrders();
                break;
                case 2:
                G.print("Enter (space separated) range of days (0<=i<=j<="+G.day_number+"): ");
                start=in.nextInt(); 
                end=in.nextInt();
                if(start>=0 && start<=end && end <= G.day_number){
                    shopServer.printDayWiseStats(start,end);
                }
                else{
                    G.println("Invalid input!");
                }
                break;
                case 3:
                G.day_number++;
                G.println("Now at date: "+G.timestamp());
                break;
                case 4:
                shopServer.printPurchaseList();
                break;
                default:
                G.println("Invalid input!");
                break;
            }
        }
    }
} 
