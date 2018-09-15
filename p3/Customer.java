import java.util.Vector; 
class Customer{
    public final Vector<Order> orders;
    public String name,address;
    Customer(String name,String address){
        this.name=name;
        this.address=address;
        this.orders= new Vector();
    }
    public Order order(Vector<Item> items, Vector<Integer> quantities){
        // Vector of {Itemtype itemtype, int quantity}
        Order o = new Order(this.name,this.address);
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

