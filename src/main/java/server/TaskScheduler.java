package server;

import data.BigInt;

import java.math.BigInteger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class TaskScheduler extends Thread {
    private PriorityBlockingQueue<WorkerRecord> WorkerQueue;
    private HashMap<Integer, Boolean> ActiveWorkers; //checks if worker with wid is working or not. Working only if true. If not on list/or set to false not working.
    private BigInt totalScore;
    private int doneWorkers;

    private BigInt lower, upper;
    TaskScheduler(BigInt lower, BigInt upper) {

        WorkerQueue = new PriorityBlockingQueue<WorkerRecord>();
        ActiveWorkers = new HashMap<Integer, Boolean>();
        this.doneWorkers = 0;
        
        this.lower=lower;
        this.upper=upper;
    }

    /**
     * Derives the range for a given worker
     * @param wR - the worker's record
     * @param curNum - the current number being worked on, i.e. the global upper bound
     * @param currentLower - the current lower bound for the range
     * @return - returns a tuple, where bound[0] = lower bound and bound[1] = upper bound
     */
    public BigInt[] deriveRange(WorkerRecord wR, BigInt curNum, BigInt currentLower){
        BigInt[] bound = new BigInt[2];
        
        //score multiplier -> calculation : range[i] = lower + totalNums*(score[i]/totalScore)
        BigInt size = new BigInt(curNum.subtract(new BigInt("3")).add(new BigInteger("1"))); //Get total numbers in the range
        BigInteger fraction = size.divide(totalScore); //get totalNums/totalScore
       
        //get totalNums*(score[i]/totalScore)
        BigInteger delta = fraction.multiply(new BigInt(Integer.toString(wR.getScore()))); 
        BigInt dt = new BigInt(delta);
        
        //Set bounds
        bound[0] = currentLower;
        bound[1] = new BigInt(currentLower.add(dt));
        if (bound[1].gt(curNum)){
            bound[1] = curNum;
        }
        return bound;
    }

    private boolean sendRange(WorkerRecord wR, BigInt[] range){
        wR.setWorkrange(range);
        wR.startWork();
        //send range
        ActiveWorkers.put(wR.getWID(),true);
        return false;
    }

    /**
     * to be run in a separate thread
     *  called once finalized range; assigns work to workers while they are in queue
     * @return if done
     */
    public boolean scheduleTask(){
        BigInt current = lower;
        BigInt currentLower = new BigInt("3");
        if(current.mod(new BigInteger("2")).equals(BigInteger.ZERO)){
            current = new BigInt(current.add(new BigInt(("1"))).toString(10));
        }
        while(current.le(upper)){ //less or equal to upperbound
            //we will need to do something so it does not loop in idle
            int workerPoolSize = getWorkerQueue().size();
            while(getWorkerQueue().size() != 0 && doneWorkers < workerPoolSize){
                System.out.println(currentLower.toString(10));
                WorkerRecord wR = getWorkerQueue().poll();
                BigInt[] range = deriveRange(wR, current, currentLower);
                wR.setWorkrange(range);
                sendRange(wR, range); //send range to worker
                currentLower = new BigInt (range[1].add(new BigInt("1")).toString(10)); //get max value of the range
                System.out.println(range[1].toString(10));
            }
            current = new BigInt(current.add(new BigInt("2")).toString(10));

        }
        return true;
    }

    /**
     * should be called as part of handling worker timeout in a separate thread
     * reschedule work uncompleted due to worker disconnecting
     * @param oldWR record of worker disconnected
     * @param wR the worker to reschedule to
     * @return true on success
     */
    public boolean reschedule(WorkerRecord oldWR, WorkerRecord wR){
        int i=0;
        while(getWorkerQueue().size() == 0){i++;}
        wR = getWorkerQueue().poll();
        BigInt[] range = oldWR.getWorkrange();
        ActiveWorkers.put(oldWR.getWID(),false);
        ActiveWorkers.put(wR.getWID(),true);
        sendRange(wR, range); //send range to worker
        wR.setWorkrange(range);
        return true;
    }


    public boolean processResults(WorkerRecord wR, BigInt[] factors) {
        wR.stopWork();
        ActiveWorkers.put(wR.getWID(),false);
        getWorkerQueue().add(wR);
        doneWorkers++;
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
    public boolean validateResults(WorkerRecord wR, BigInt[] factors){
        BigInt iterable = wR.getWorkrange()[0];
        for(int i=0; i<factors.length;i++){
            if(iterable.mod(factors[i]).compareTo(BigInteger.ZERO) != 0){
                return false;
            }else{
                iterable =(BigInt) iterable.add(new BigInteger("2"));
            }
        }
        return true;
    }

    public PriorityBlockingQueue<WorkerRecord> getWorkerQueue() {
        return WorkerQueue;
    }

    public void setWorkerQueue(PriorityBlockingQueue<WorkerRecord> workerQueue) {
        WorkerQueue = workerQueue;
    }

    public void addToWorkerQueue(WorkerRecord wR){
        this.WorkerQueue.add(wR);
    }


    public BigInt getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigInt totalScore) {
        this.totalScore = totalScore;
    }

    public HashMap<Integer, Boolean> getActiveWorkers() {
        return ActiveWorkers;
    }

    public boolean putActiveWorker(WorkerRecord wR){
        this.ActiveWorkers.put(wR.getWID(), true);
        return true;
    }

    public boolean deactivateActiveWorker(WorkerRecord wR){
        this.ActiveWorkers.put(wR.getWID(), false);
        return true;
    }

    public void setActiveWorkers(HashMap<Integer, Boolean> activeWorkers) {
        ActiveWorkers = activeWorkers;
    }

	@Override
	public void run() {
		scheduleTask();
		
	}
}
