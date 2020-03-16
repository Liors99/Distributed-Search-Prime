package server;

import java.math.BigInteger;
import java.util.PriorityQueue;

public class TaskScheduler {
    private PriorityQueue<WorkerRecord> WorkerQueue;

    TaskScheduler(){
        WorkerQueue = new PriorityQueue<>();
    }

    private BigInteger[] deriveRange(WorkerRecord wR, BigInteger lower, BigInteger upper){
        BigInteger[] bound = new BigInteger[2];
        return bound;
    }

    private boolean sendRange(WorkerRecord wR, BigInteger[] range){
        wR.setWorkrange(range);
        wR.startWork();
        //send range
        return false;
    }

    /**
     *  called once finalized range; assigns work to workers while they are in queue
     * @return if done
     */
    public boolean scheduleTask(BigInteger lower, BigInteger upper){
        BigInteger current = lower;
        if(current.mod(new BigInteger("2")).equals(BigInteger.ZERO)){
            current = current.add(new BigInteger(("1")));
        }
        while(current.compareTo(upper) < 1){ //less or equal to upperbound
            //we will need to do something so it does not loop in idle
            if(WorkerQueue.size() != 0){
                WorkerRecord wR = WorkerQueue.poll();
                BigInteger[] range = deriveRange(wR,current, upper);
                sendRange(wR, range); //send range to worker
                current = current.add(new BigInteger("2"));
            }

        }
        return true;
    }

    public boolean processResults(WorkerRecord wR, BigInteger[] factors) {
        wR.stopWork();
        WorkerQueue.add(wR);
        if (!validateResults(wR, factors)){
            return false;
        }
        // do result thing
        return true;
    }

    /**
     * checks results
     * @param factors factors provided by the worker
     * @return true if good; false if one of factors is bad
     */
    public boolean validateResults(WorkerRecord wR, BigInteger[] factors){
        BigInteger iterable = wR.getWorkrange()[0];
        for(int i=0; i<factors.length;i++){
            if(iterable.mod(factors[i]).compareTo(BigInteger.ZERO) != 0){
                return false;
            }else{
                iterable = iterable.add(new BigInteger("2"));
            }
        }
        return true;
    }

}
