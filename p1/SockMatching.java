import java.util.Vector;
import java.util.Random; 
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

class G {
    /*The static keyword makes variables globally accessible, while their class is loaded.*/
    public static final String[] Colors={"Red","Green","Blue","White"};
    public static final int NUM_COLORS = Colors.length,
    NUM_ARMS=2,
    TRAY_SIZE = 100,
    MAX_TRIES=5,
    THINK_TIME=200,
    WAIT_TIME=1000;
}

public class SockMatching {
    public static void main(String args[]) {
        // Shared objects
        Vector sharedQueue = new Vector();
        AtomicInteger  matchedColor = new AtomicInteger();
        // Matching machine thread
        Thread matchingMachine = new Thread(new MatchingMachine(matchedColor,sharedQueue, G.TRAY_SIZE, G.MAX_TRIES, G.THINK_TIME), "MM");
        Thread shelfManager = new Thread(new ShelfManager(matchedColor, G.THINK_TIME), "SM");
        // Arms threads
        for(int i=1;i<=G.NUM_ARMS;i++){
            Thread arm1 = new Thread(new Arm(sharedQueue, G.TRAY_SIZE, G.MAX_TRIES, G.THINK_TIME), "Arm"+i);
            arm1.start();
        }
        matchingMachine.start();
        shelfManager.start();
    }
}

class Arm implements Runnable {

    private final Vector sharedQueue;
    private final int TRAY_SIZE,MAX_TRIES,THINK_TIME;
    private int tries=0;
    private Random rand = new Random(); 

    public Arm(Vector sharedQueue, int size, int max_tries, int think_time) {
        this.sharedQueue = sharedQueue;
        tries=0;
        this.TRAY_SIZE = size;
        this.MAX_TRIES = max_tries;
        this.THINK_TIME = think_time;

    }
    public void log(String msg){
        System.out.println("("+tries+"/"+MAX_TRIES+") "+Thread.currentThread().getName()+": "+msg);
    }
    @Override
    public void run() {
        int i;
        while(tries<MAX_TRIES){
            try{
                synchronized (sharedQueue) {
                    // tries++;
                    i=rand.nextInt(G.NUM_COLORS);
                    log("Picked up "+ G.Colors[i]+ " Sock from Heap");
                    while (sharedQueue.size() == TRAY_SIZE){
                        if(tries==MAX_TRIES){
                            log("Max tries reached");
                            return;
                        }
                        log("Tray full, waiting.\n");
                        sharedQueue.wait(1000);// blocking call
                        tries++;
                        log("Waiting done. Had picked up "+ G.Colors[i]+" Sock");
                    }
                    log("Tray has space, Passed " + G.Colors[i]+" Sock to tray\n");
                    sharedQueue.add(i);
                    // log("Tray released.\n");
                    sharedQueue.notifyAll();
                    Thread.sleep(THINK_TIME);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Arm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class ShelfManager implements Runnable {
    private final AtomicInteger matchedColor;
    private final int THINK_TIME;
    private final int counts[];
    public ShelfManager(AtomicInteger matchedColor, int think_time) {
        this.matchedColor=matchedColor;
        this.THINK_TIME=think_time;
        counts = new int[G.NUM_COLORS];
        for (int i=0;i<counts.length;i++) 
            counts[i]=0;
    }

    public void log(String msg){
        System.out.println(Thread.currentThread().getName()+": "+msg);
    }
    public void sortPair() throws InterruptedException{
        synchronized(matchedColor){
            log("Waiting for Matching Machine to send a pair");
            matchedColor.wait();
            int i=matchedColor.get();
            counts[i]++;
            log("Received "+G.Colors[i]+" pair and Stored in shelf.");
            System.out.print("\n\t\t Colors: ");
            for (i=0;i<counts.length;i++) 
                System.out.print(G.Colors[i]+": "+counts[i]+", ");
            System.out.print("\n\n");
            
            Thread.sleep(THINK_TIME);
            matchedColor.notifyAll();
        }
    }

    @Override
    public void run() {
        while(true){
            try{
                sortPair();
            } catch (InterruptedException ex) {
                Logger.getLogger(ShelfManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
class MatchingMachine implements Runnable {

    private final AtomicInteger matchedColor;
    private final Vector sharedQueue;
    private final int TRAY_SIZE,MAX_TRIES,THINK_TIME;
    private final int counts[];
    private int tries=0,matchedCount=0;

    public MatchingMachine(AtomicInteger matchedColor,Vector sharedQueue, int size, int max_tries, int think_time) {
        this.matchedColor = matchedColor;
        this.sharedQueue = sharedQueue;
        /*Note: In Java an array cannot change its size once it is created*/
        counts = new int[G.NUM_COLORS];
        for (int i=0;i<counts.length;i++) 
            counts[i]=0;
        tries=0;
        this.TRAY_SIZE = size;
        this.MAX_TRIES = max_tries;
        this.THINK_TIME = think_time;
    }
    public void sendMatched(int i) throws InterruptedException{
        //wait for Shelf Manager to classify
        log("Waiting to set matchedColor");
        synchronized(matchedColor){
            //set matched color
            matchedColor.set(i);
            matchedCount++;
            //tell Shelf Manager 
            log("Done setting matchedColor\n");
            matchedColor.notifyAll();
        }

    }

    public void log(String msg){
        System.out.println("("+tries+"/"+MAX_TRIES+")["+matchedCount+"] "+Thread.currentThread().getName()+": "+msg);
    }
    @Override
    public void run() {
        int i;
        while(tries<MAX_TRIES){
            synchronized (sharedQueue) {// blocking 
                try {
                    // tries++;
                    log("Looked at Tray.");
                    while (sharedQueue.size() == 0){
                        if(tries==MAX_TRIES){
                            log("Max tries reached");
                            return;
                        }
                        log("Tray empty, waiting.\n");
                        sharedQueue.wait(G.WAIT_TIME);// blocking call
                        tries++;
                        log("Waiting done.");
                    }
                    i= (Integer) sharedQueue.remove(0);
                    counts[i]++;
                    log("Picked up " + G.Colors[i]+ " Sock from Tray");
                    System.out.print("\n\t\t Colors: ");
                    for (int j=0;j<counts.length;j++) 
                        System.out.print(G.Colors[j]+": "+counts[j]+", ");
                    System.out.print("\n\n");
                    /*
                    Matching algo here
                    */
                    if(counts[i]==2){
                        counts[i]=0;
                        sendMatched(i);
                    }
                    Thread.sleep(THINK_TIME);
                    // notifies at end of this block
                    sharedQueue.notifyAll();
                    // log("Tray released.\n");
                } catch (InterruptedException ex) {
                    Logger.getLogger(MatchingMachine.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
