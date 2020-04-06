package server.debugger;

import java.util.LinkedList;
import java.util.Queue;

public class DebugOut extends Thread{
    private static Queue<DebugMessage>  MessageQueue = new LinkedList<DebugMessage>();

    public DebugOut(){}

    public boolean addMessageToQueue(DebugMessage dM){
        MessageQueue.add(dM);
        return true;
    }

    public DebugMessage getMessageFromQueue() throws NullPointerException{
        if(MessageQueue.size()>0){
            return MessageQueue.poll();
        }else{
            throw new NullPointerException();
        }
    }

    public void run(){
        while(true) {
            while (MessageQueue.size() == 0) {
                yield();
            }
            System.out.println(getMessageFromQueue().getMessage());
        }
    }



}
