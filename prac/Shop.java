import java.util.logging.Level;
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
    DLRY_TIME=5000,
    DAY_DURATION=10000;//calculations at end
    /*
    static means exist without instantiation!
    */
    public final static EnumMap<Itemtype, Integer> Prices = new EnumMap<Itemtype, Integer>(Itemtype.class);    
    public final static Itemtype itemtypes[]= Itemtype.values();
    public final static Random rand = new Random(); 

    public static long time(){
        return System.currentTimeMillis();
    }
    public static void print(String msg){
        System.out.println(msg);
    }
    public static String timestamp(){
        return new SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
    }
}
class Customer{
    public final Vector<Order> orders;
    public final String name;
    Customer(String name){
        this.name=name;
        this.orders= new Vector();
    }
    public void order(Vector<Item> items, Vector<Integer> quantities){
        // Vector of {Itemtype itemtype, int quantity}
        Order o = new Order(this.name);
        int i=0;
        for(Integer q : quantities){
            if(q>0){
                o.add_record(items.get(i),q);
            }
            i++;
        }
        this.orders.add(o);
        o.clicked();
    }
}
class Shop
{ 
    public final Vector<Integer> dailySales;
    public final Vector<Item> items;
    public final Vector<Customer> customers;
    Shop(){
        dailySales = new Vector<Integer>();
        items = new Vector<Item>();        
        customers = new Vector<Customer>();        
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
        // added thru sockets accept
        customers.add(new Customer("C1"));
        customers.add(new Customer("C2"));
        for(int j=0;j<customers.size();j++){
            Customer c1 = customers.get(j);
            Vector<Integer> quantities = new Vector<Integer>(items.size());
            for(int i=0;i<items.size();i++){
                quantities.add(G.rand.nextInt(G.O_THR));
            }
            c1.order(items,quantities);
        }

        for(Customer c : this.customers){
            for(Order o : c.orders){
                G.print(o.get_receipt());
            }
        }
    }
    public static void main(String args[])  
    {   
        // init Shop
        Shop s=new Shop();      

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
        receipt+=(this.rejected?"Not Sold":("Sold at: "+(this.sold_at-G.start_time)+"ms"))+"\n";
        receipt+="timestamp,\t t_ms,\t item,\t rate,\t quantity,\t price\n";
        for (Record r : records){
            receipt+=r.t+",\t";
            receipt+=(r.t_ms-G.start_time)+"ms,\t";
            receipt+=r.item.type+",\t";
            receipt+=r.rate+",\t"+r.quantity+",\t"+r.price+"\n";
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

