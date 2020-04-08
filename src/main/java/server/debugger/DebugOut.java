package server.debugger;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class DebugOut extends Thread{
    private static BlockingQueue<DebugMessage>  MessageQueue = new LinkedBlockingDeque<DebugMessage>();

    public DebugOut(){}

    public synchronized boolean addMessageToQueue(DebugMessage dM){
        MessageQueue.add(dM);
        return true;
    }

    public synchronized DebugMessage getMessageFromQueue() throws NullPointerException{
        if(MessageQueue.size()>0){
            return MessageQueue.poll();
        }else{
            throw new NullPointerException();
        }
    }

    /**
     * has to be started in CoordConsole
     */
    public void run(){
        while(true) {
            while (MessageQueue.size() == 0) {
                yield();
            }
            System.out.println(getMessageFromQueue().getMessage());
        }
    }



}
