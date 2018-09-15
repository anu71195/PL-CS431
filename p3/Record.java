import java.util.Vector; 

public class Record {
    // one time definition
    public final String timestamp;
    public final int quantity, rate, price;
    // public final long t_ms;
    // changables
    public boolean rejected;
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