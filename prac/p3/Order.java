import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Vector; 
public class Order {
    // Class variables
    private static int order_count=0;
    public static String header="orderno,\tcust_id,\t order status,\t timestamp,\t item,\t rate,\t qty,\t price\n";

    // Object variables
    private final int id;
    private final String cust_name;
    public boolean rejected;
    public int sold_day,exp_delivery;
    // private LocalDateTime sold_date;
    // public long sold_at;//milliseconds
    Vector<Record> records;
    Order(String cust_name){
        this.order_count++;//on each instance
        this.id=this.order_count; //store current val
        this.cust_name=cust_name;
        this.records = new Vector();
        this.rejected = false;
        // this.sold_at = -1;
    }
    public String get_receipt(){
        String receipt="";
        // receipt+=(this.rejected?"Out of stock":("Sold at: "+(this.sold_at-G.start_time)+"ms"))+"\n";
        // receipt+=this.header;
        String sepa=",\t ";
        for (Record r : records){
            receipt+= "Order"+this.id+sepa+cust_name+sepa;
            receipt+=(this.rejected?"Rejected!":"Sold!")+sepa;
            receipt+=r.timestamp+sepa;//(r.t_ms-G.start_time)+"ms,
            receipt+=r.item.type+sepa;
            receipt+=r.rate+sepa+r.quantity+sepa+r.price;
            receipt+=",\n";
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
            if(r.item.quantity>-1 && r.item.quantity - r.quantity < r.item.threshold){
                if(r.item.quantity>r.quantity){
                    all_avl=false;
                    break;
                }
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
            int teaCoffeeCount = 0;
            for(Record r : records){
                if(r.quantity>-1 && (r.item.type == Itemtype.TEA || r.item.type == Itemtype.COFFEE)){
                    teaCoffeeCount += r.quantity;
                }
            }
            // this.sold_at=G.time();
            // this.sold_date=G.timestamp();
            this.sold_day=G.day_number;
            try{
            // Deliverytime calc:
                G.teaSema.acquire();
                G.teaCounter+=teaCoffeeCount;
                this.exp_delivery = G.DLRY_TIME + G.PREP_TIME * G.teaCounter;
            // this.delivered_at = (G.time()-G.start_time)/60000 + exp_delivery;
                G.teaSema.release();

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

        for(Record r : records){
            r.item.sema.release();
        }
    }    
}
