import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
wait() tells the calling thread to give up the monitor and go to sleep until some other thread enters the same monitor and calls notify( ).
>> obj.notify() wakes up the first thread that called wait() on the same 'obj'.
simply notify() means its for 'this' object 
*/
public class RightPC {

    public static void main(String args[]) {
        Vector sharedQueue = new Vector();
        int size = 4, max_tries=5, think_time=50;
        Thread prodThread = new Thread(new Producer(sharedQueue, size, max_tries, think_time), "P1");
        Thread prodThread2 = new Thread(new Producer(sharedQueue, size, max_tries, think_time), "P2");
        Thread consThread = new Thread(new Consumer(sharedQueue, size, max_tries, think_time), "C");
        prodThread.start();
        prodThread2.start();
        consThread.start();
    }
}

class Producer implements Runnable {

    private final Vector sharedQueue;
    private final int SIZE,MAX_TRIES,THINK_TIME;
    private int tries=0;


    public Producer(Vector sharedQueue, int size, int max_tries, int think_time) {
        this.sharedQueue = sharedQueue;
        tries=0;
        this.SIZE = size;
        this.MAX_TRIES = max_tries;
        this.THINK_TIME = think_time;
    }
    public void log(String msg){
        System.out.println("("+tries+"/"+MAX_TRIES+") "+Thread.currentThread().getName()+": "+msg);
    }
    @Override
    public void run() {
        int i=0;
        while(tries<MAX_TRIES){
            try{
                synchronized (sharedQueue) {
                    tries++;
                    log(" Queue locked.");
                    while (sharedQueue.size() == SIZE){
                        if(tries==MAX_TRIES){
                            log("Max tries reached");
                            return;
                        }
                        log("Queue full.");
                        // blocking call
                        log("Queue released & waiting.\n");
                        sharedQueue.wait(1000);
                        tries++;
                        log(" Waiting done. Queue locked again");
                    }
                    log("Produced " + i);
                    sharedQueue.add(Thread.currentThread().getName()+"_"+i++);
                    log("Queue released.\n");
                    sharedQueue.notifyAll();
                    Thread.sleep(THINK_TIME);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class Consumer implements Runnable {

    private final Vector sharedQueue;
    private final int SIZE,MAX_TRIES,THINK_TIME;
    private int tries=0;

    public Consumer(Vector sharedQueue, int size, int max_tries, int think_time) {
        this.sharedQueue = sharedQueue;
        tries=0;
        this.SIZE = size;
        this.MAX_TRIES = max_tries;
        this.THINK_TIME = think_time;
    }
    public void log(String msg){
        System.out.println("("+tries+"/"+MAX_TRIES+") "+Thread.currentThread().getName()+": "+msg);
    }
    @Override
    public void run() {
        while(tries<MAX_TRIES){
            synchronized (sharedQueue) {// blocking 
                try {
                    tries++;
                    log(" Queue locked.");

                    while (sharedQueue.size() == 0){
                        if(tries==MAX_TRIES){
                            log("Max tries reached");
                            return;
                        }
                        log("Queue empty.");
                        // blocking call
                        log("Queue released & waiting.\n");
                        sharedQueue.wait(1000);
                        tries++;
                        log(" Waiting done. Queue locked again");
                    }
                    log("Consumed " + sharedQueue.remove(0));
                    Thread.sleep(THINK_TIME);
                    // notifies at end of this block
                    sharedQueue.notifyAll();
                    log("Queue released.\n");
                } catch (InterruptedException ex) {
                    Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
