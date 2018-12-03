import java.io.*; 
import java.net.*; 
// import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.Vector; 
import java.util.concurrent.Semaphore; 
import java.util.logging.Level;
import java.util.logging.Logger;
/*
*/

class TeaMachine implements Runnable {
    public void run(){
        while(true){
            try{
                Thread.sleep(G.PREP_TIME*G.MINUTE_DURN*1000);
                G.teaSema.acquire();
                if(G.teaCounter>0){
                    G.teaCounter--;
                    G.println_t("Prepared One Tea. Pending Teas: "+G.teaCounter);
                }
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
                G.coffeeSema.acquire();
                if(G.coffeeCounter>0){
                    G.coffeeCounter--;
                    G.println_t("Prepared One Coffee. Pending Coffees: "+G.coffeeCounter);
                }
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
    public final Vector<Customer> customers;
    // constructor with port 
    public ShopServer(int port){
        this.port = port;
        // starts server and waits for a connection 
        dailySales = new Vector<Integer>();
        G.items = new Vector<Item>();        
    // Initialize item prices and stock quantities
        for(Itemtype it : Itemtype.values()){
            if(it == Itemtype.TEA || it == Itemtype.COFFEE){
                G.Prices.put(it,G.T_PRICE);
                G.items.add(new Item(it,-1, -1,G.Prices.get(it),new Semaphore(1,true)));
            }
            else{
                G.Prices.put(it,G.O_PRICE);
                G.items.add(new Item(it,G.O_QUANT, G.O_THR, G.Prices.get(it),new Semaphore(1,true)));
            }
        }
        customers = new Vector<Customer>();             
    }  

    public void run(){
        try{
            server = new ServerSocket(port); 
            G.println_t("ShopServer started"); 
            System.out.println("Waiting for a client ..."); 
            while(true)
            {
                Socket socket = server.accept();
                //Vector is Thread safe - _/TODO: make this THREAD SAFE
                customers.add(new Customer(socket,"C"+cust_count,"A"+cust_count));
                Customer curr_cust = customers.get(cust_count-1);
                cust_count++;
                Thread a = new Thread(curr_cust, curr_cust.name);
                a.start();   
            } 
        } catch(IOException  i){
            System.out.println(i);
            System.out.println("Server exiting");
            System.exit(0); 
        } 
    }    
    public void printPurchaseList(){
        G.println("Purchase list:");
        for(Item i : G.items){
            if(i.to_purchase)
                G.println(i.type+" : "+i.quantity);
        }
        G.println("");
    }    
    public void printOrders(){    
        G.println("Order receipts: ");
        for(Customer c : this.customers){
            for(Order o : c.orders){
                G.print(Order.header);
                G.println(o.get_receipt());
            }                
        }                
        G.println("");
    }

    public void printDayWiseStats(int start, int end){
        G.println("Day wise stats: ");
        Vector<Vector<String> > receipts = new Vector<Vector<String> >(end-start+1);
        for (int i=0;i<end-start+1;i++) {
            receipts.add(new Vector<String>());
        }
        for(Customer c : this.customers){
            for(Order o : c.orders){
                if(!o.rejected && o.sold_day>=start && o.sold_day<=end)
                    receipts.get(o.sold_day).add(o.get_receipt());
            }                
        }                    
        for(Vector<String> rs : receipts){
            G.println("\n\t Day "+start);
            G.print(Order.header);
            for(String r : rs){
                G.println(r);
            }
            start++;
        }
        G.println("");
	    }
    public static void main(String args[]) 
    { 
        ShopServer shopServer = new ShopServer(5000);
        Thread ss = new Thread(shopServer,"ShopServer"); 
        Thread tm = new Thread(new TeaMachine(),"TeaMachine"); 
        Thread cm = new Thread(new CoffeeMachine(),"CoffeeMachine"); 
        ss.start();
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
                G.println("Date value updated to: "+G.timestamp());
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
