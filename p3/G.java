import java.text.SimpleDateFormat; 
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Random; 
import java.util.Vector; 
import java.util.concurrent.Semaphore; 

public class G{
    public final static long start_time=time();
    public final static int T_PRICE=10,
    O_PRICE=20,
    O_QUANT=100,
    O_THR=10,
    MINUTE_DURN=5, //seconds
    PREP_TIME=1, //*MINUTE_DURN
    DLRY_TIME=2;
    
    // random seed
    public final static Random rand = new Random(); 

    // For prep time calculation
    public static Semaphore teaSema = new Semaphore(1);
    public static int teaCounter = 0;
    public static Semaphore coffeeSema = new Semaphore(1);
    public static int coffeeCounter = 0;
    
    // these are initialized by shop server
    public final static EnumMap<Itemtype, Integer> Prices = new EnumMap<Itemtype, Integer>(Itemtype.class);    
    public static Vector<Item> items;

    // current day offset
    public static int day_number=0;
    
    // utility functions
    public static long seconds(){
        return (time()-start_time)/1000;
    }
    public static long time(){
        return System.currentTimeMillis();
    }
    // public static void printt(String msg){
    //     System.out.println(Thread.currentThread().getName()+": "+msg);
    // }
    public static void println_t(String msg){
        System.out.println("["+seconds()+"s] "+msg);
    }
    public static void println(String msg){
        System.out.println(msg);
    }
    public static void print(String msg){
        System.out.print(msg);
    }
    public static String timestamp(LocalDateTime date){
        return DateTimeFormatter.ofPattern("dd-MM HH:mm:ss").format(date);
    }        
    public static String timestamp(int day){
        return DateTimeFormatter.ofPattern("dd-MM HH:mm:ss").format(LocalDateTime.now().plusDays(day));
    }        
    public static String timestamp(){        
        return DateTimeFormatter.ofPattern("dd-MM HH:mm:ss").format(LocalDateTime.now().plusDays(day_number));
    }
}

