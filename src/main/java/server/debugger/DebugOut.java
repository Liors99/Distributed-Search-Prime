package server.debugger;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class DebugOut extends Thread{
    public static Boolean debugMode = true;
    private static BlockingQueue<DebugMessage>  MessageQueue = new LinkedBlockingDeque<DebugMessage>();
    private String logFileName;

    /**
     * an empty constructor
     * initializes class variables used for storing debug output
     */
    public DebugOut(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        LocalDateTime now = LocalDateTime.now();
        this.logFileName = dtf.format(now);
        System.out.println("log file created: " + this.logFileName);
    }

    /**
     * adds a message to queue to be displayed
     * @param dM debugmessage to be added
     * @return true on success
     */
    public synchronized boolean addMessageToQueue(DebugMessage dM){
        MessageQueue.add(dM);
        return true;
    }

    /**
     * get a message to queue to be displayed
     * @return dM debugmessage to be removed
     *  
     */
    public synchronized DebugMessage getMessageFromQueue() throws NullPointerException{
        if(MessageQueue.size()>0){
            return MessageQueue.poll();
        }else{
            throw new NullPointerException();
        }
    }

    /**
     * starts a thread dedicated to displaying output debug info. Should be run in unison with debug in. Neither feature were added as not core of the project.
     * has to be started in CoordConsole
     */
    public void run(){
        long start = System.currentTimeMillis();
        while(true) {
            while (MessageQueue.size() == 0) {
                yield();
            }
            if (System.currentTimeMillis() - start <= 1500) {
                continue;
            }else{

                start = System.currentTimeMillis();
            }
            DebugMessage dm = getMessageFromQueue();
            if(debugMode) {
                System.out.println(dm.getMessage());
            }
            File f = new File(System.getProperty("user.dir")+"\\"+logFileName+".log");
            try{
                if(!f.exists()){
                    f.createNewFile();
                }
                FileWriter fileWriter = new FileWriter(f, true);
                fileWriter.write(dm.getMessage()+"\n");
                fileWriter.flush(); //manually flushes message to the log file
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
