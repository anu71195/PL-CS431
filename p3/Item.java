import java.util.concurrent.Semaphore; 
import java.util.Vector; 

enum Itemtype 
{ 
    TEA ,COFFEE, COOKIES, SNACKS
};
public class Item {
    //reference
    public final Itemtype type;
    public final Semaphore sema;
    public final int rate,threshold;
    public boolean to_purchase;
    public int quantity;
    Item(Itemtype type,int quantity,int threshold,int rate, Semaphore sema){
        this.type=type;
        this.sema=sema;
        this.rate=rate;
        this.threshold=threshold;
        this.quantity=quantity;
        this.to_purchase=false;        
    } 
}
