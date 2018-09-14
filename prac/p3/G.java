import java.text.SimpleDateFormat; 
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Random; 
import java.util.concurrent.Semaphore; 

public class G{
    public final static long start_time=time();
    public final static int T_PRICE=10,
    O_PRICE=20,
    O_QUANT=20,
    O_THR=10,
    PREP_TIME=60, //seconds
    DLRY_TIME=120;
    /*
    static means exist without instantiation!
    */
    // For prep time calculation
    public static Semaphore teaSema = new Semaphore(1);
    public static int teaCounter = 0;

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
    public static String timestamp(LocalDateTime date){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date);
    }        
    public static String timestamp(int day){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(currDate.plusDays(day));
    }        
    public static String timestamp(){        
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(currDate.plusDays(day_number));
    }
}

