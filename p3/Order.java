import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Vector; 

class Record {
    // one time definition
    public final String timestamp;
    public final int quantity, rate, price;
    // changables
    public Item item;
    Record(String timestamp, Item item, int quantity){
        this.timestamp=timestamp;
        // this.t_ms=G.time();
        this.item=item;
        this.rate=item.rate;
        this.quantity=quantity;
        this.price=item.rate*quantity;
    }
}
public class Order {
    // Class variables
    private static int order_count=0;
    public static String header="orderno,\tcust_id,\t address,\t order status,\t timestamp,\t item,\t rate,\t qty,\t price\n";

    // Object variables
    private final int id;
    private final String cust_name,cust_addr;
    public boolean rejected;
    public int sold_day,exp_delivery;
    // private LocalDateTime sold_date;
    // public long sold_at;//milliseconds
    Vector<Record> records;
    Order(String cust_name,String cust_addr){
        this.order_count++;//on each instance
        this.id=this.order_count; //store current val
        this.cust_name=cust_name;
        this.cust_addr=cust_addr;
        this.records = new Vector();
        this.rejected = false;
        // this.sold_at = -1;
    }
    public String get_receipt(){
        String receipt="";
        // receipt+=(this.rejected?"Out of stock":("Sold at: "+(this.sold_at-G.start_time)+"ms"))+"\n";
        // receipt+=this.header;
        String sepa=" |\t ";
        long total_price=0;
        for (Record r : records){
            receipt+= "Order"+this.id+sepa+cust_name+sepa+cust_addr+sepa;
            receipt+=(this.rejected?"REJECTED!":"SOLD!")+sepa;
            receipt+=r.timestamp+sepa;//(r.t_ms-G.start_time)+"ms,
            receipt+=r.item.type+sepa;
            receipt+=r.rate+sepa+r.quantity+sepa+r.price;
            receipt+="\n";
            total_price+=r.price;
        }
        if(!this.rejected)receipt+= "\n\tTotal Cost: "+total_price+"\n";
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
            if(r.item.quantity>-1){
                if(r.item.quantity<r.quantity){
                    all_avl=false;
                    break;
                }
            }
        }
        if(all_avl){
             //modify
            int teaCount=0,coffeeCount = 0;
            for(Record r : records){
                if(r.item.type == Itemtype.TEA)
                    teaCount += r.quantity;
                else if(r.item.type == Itemtype.COFFEE)
                    coffeeCount += r.quantity;
            }
            // this.sold_at=G.time();
            // this.sold_date=G.timestamp();
            this.sold_day=G.day_number;
            try{
                // Delivery time calc:
                this.exp_delivery = G.DLRY_TIME;
                
                // add to tea counter only if tea is ordered
                if(teaCount>0){
                    G.teaSema.acquire();
                    G.teaCounter+=teaCount;
                    this.exp_delivery +=  G.PREP_TIME * G.teaCounter;
                    G.teaSema.release();
                }
                if(coffeeCount>0){
                    G.coffeeSema.acquire();
                    G.coffeeCounter+=coffeeCount;
                    this.exp_delivery +=  G.PREP_TIME * G.coffeeCounter;
                    G.coffeeSema.release();
                }
                
                this.exp_delivery *= G.MINUTE_DURN;
                // this.delivered_at = (G.time()-G.start_time)/60000 + exp_delivery;
            }
            catch(InterruptedException ex){
                Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
            }
            for(Record r : records){
                if(r.item.quantity>-1){
                    r.item.quantity -= r.quantity;
                    if(r.item.quantity < r.item.threshold)
                        r.item.to_purchase=true;
                }
            }
        }
        else{
            //reject
            this.rejected=true;
        }
        for(Record r : records){
            r.item.sema.release();
        }
    }    
}
