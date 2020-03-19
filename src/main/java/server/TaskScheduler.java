package server;

import data.BigInt;

import java.math.BigInteger;

import java.util.LinkedList;
import java.util.Queue;

public class TaskScheduler {
    private Queue<WorkerRecord> WorkerQueue;
    private BigInt totalScore;
    private int doneWorkers;

    TaskScheduler(){

        WorkerQueue = new LinkedList<WorkerRecord>();
        this.doneWorkers = 0;
    }

    public BigInt[] deriveRange(WorkerRecord wR, BigInt curNum, BigInt currentLower){
        BigInt[] bound = new BigInt[2];
        //score multiplier
        BigInteger size = curNum.subtract(new BigInt("3")).add(new BigInteger("1"));
        BigInteger fraction;
        if (size.mod(totalScore).compareTo(BigInteger.ZERO)==0){
            fraction = size.divide(totalScore);
        }else{
            fraction = size.divide(totalScore).add(new BigInteger("1"));
        }

        BigInteger delta = fraction.multiply(new BigInt(Integer.toString(wR.getScore())));
        int radix = 10;
        BigInt dt = new BigInt(delta.toString(radix));
        /*
        System.out.println(fraction.toString(10));
        System.out.println(totalScore.toString(10));
        System.out.println(dt.toString(10));
        */
        bound[0] = currentLower;
        bound[1] = new BigInt(currentLower.add(dt).toString(10));
        if (bound[1].gt(curNum)){
            bound[1] = curNum;
        }
        //lower + range = new lower
        //floor
        return bound;
    }

    private boolean sendRange(WorkerRecord wR, BigInt[] range){
        wR.setWorkrange(range);
        wR.startWork();
        //send range
        return false;
    }

    /**
     *  called once finalized range; assigns work to workers while they are in queue
     * @return if done
     */
    public boolean scheduleTask(BigInt lower, BigInt upper){
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
                sendRange(wR, range); //send range to worker
                currentLower = new BigInt (range[1].add(new BigInt("1")).toString(10)); //get max value of the range
                System.out.println(range[1].toString(10));
            }
            current = new BigInt(current.add(new BigInt("2")).toString(10));

        }
        return true;
    }

    public boolean processResults(WorkerRecord wR, BigInt[] factors) {
        wR.stopWork();
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

    public Queue<WorkerRecord> getWorkerQueue() {
        return WorkerQueue;
    }

    public void setWorkerQueue(Queue<WorkerRecord> workerQueue) {
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

}
