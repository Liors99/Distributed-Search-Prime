package server;

import data.BigInt;
import data.MessageDecoder;
import data.NetworkMessage;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;

public class TaskScheduler extends Thread {
    private PriorityBlockingQueue<WorkerRecord> WorkerQueue;
    private LinkedList<WorkerRecord> WorkingWorkers;
    
    private HashMap<BigInt, BigInt> num_to_divisors;
    private HashMap<BigInt, BigInt> num_to_total;
    private ArrayList<BigInt> primes;
    

	private HashMap<Integer, Boolean> ActiveWorkers; //checks if worker with wid is working or not. Working only if true. If not on list/or set to false not working.
    private BigInt totalScore;
    private int doneWorkers;
    
    private BigInt max_work; 
    private int target;
    private BigInt lower, upper;
    private final long TIMEOUT = 20000; //worker will be expected to work for half of it. so if 20s timeout worker should report back every 10s
    private final long defaultAssignment = 10;
    
    private BlockingQueue<String> workerMessages;
    private BigInt current;
    //is the current task complete
    private boolean done=false;
    

    


	private Store st;

	/**
	 * creates an object of task scheduler
	 * Initializes important values used for scheduling tasks
	 * @param lower bound of a viable prime number
	 * @param upper bound of a viable prime number to be considered
	 * @param target number of primes we are looking for
	 */
    TaskScheduler(BigInt lower, BigInt upper, int target) {
    	
    	//max_work = new BigInt(upper.subtract(lower).divide(new BigInteger("10")));
    	
        WorkerQueue = new PriorityBlockingQueue<WorkerRecord>();
        ActiveWorkers = new HashMap<Integer, Boolean>();
        this.doneWorkers = 0;
        this.target = target;
        this.primes = new ArrayList<BigInt>();
        this.lower=lower;
        this.upper=upper;
        this.WorkingWorkers = new LinkedList<WorkerRecord>();
        totalScore= new BigInt("0");
        
        workerMessages= new LinkedBlockingDeque<String>();
        num_to_divisors= new HashMap<BigInt, BigInt>();
        num_to_total= new HashMap<BigInt, BigInt>(); 
        

    }
    
    /**
     * get target number of primes
     * @return int value representing target value of primes
     */
    public int getTarget() {
		return target;
	}


    /**
     * set the target number of primes
     * @param target number of primes to be reached
     */
	public void setTarget(int target) {
		this.target = target;
	}


	/**
	 * get bigInt representing the target low bound
	 * @return bigInt representing low bound
	 */
	public BigInt getLower() {
		return lower;
	}

/**
 * set the lower bound of viable primes to search
 * @param lower value of the interval in which to look for primes
 */
	public void setLower(BigInt lower) {
		this.lower = lower;
	}

/**
 * get upper bound of the range in which we are searching for primes
 * @return bigint value of the upperbound of viable primes
 */
	public BigInt getUpper() {
		return upper;
	}

/**
 * set upper bound bigInt no number above it will be considered
 * @param upper bigint value to set upper bound to
 */
	public void setUpper(BigInt upper) {
		this.upper = upper;
	}

/**
 * an empty constructor for task scheduler. Initializes a lot of data structures used in scheduling.
 */
	TaskScheduler() {    	
    	//max_work = new BigInt(upper.subtract(lower).divide(new BigInteger("10")));
        WorkerQueue = new PriorityBlockingQueue<WorkerRecord>();
        ActiveWorkers = new HashMap<Integer, Boolean>();
        this.doneWorkers = 0;
        this.primes = new ArrayList<BigInt>();
        this.WorkingWorkers = new LinkedList<WorkerRecord>();
        totalScore= new BigInt("0");
        
        workerMessages= new LinkedBlockingDeque<String>();
        this.current= new BigInt(BigInt.ZERO);
        
        num_to_divisors = new HashMap<BigInt, BigInt>();
        num_to_total= new HashMap<BigInt, BigInt>(); 
        
	}
	
	/**
	 * used to get worker messages for scheduler worker communication
	 * @return string representing the worker message
	 */
	public String getNextWorkerMessage() {
		if(!this.workerMessages.isEmpty()) {
			return this.workerMessages.poll();
		}
		
		return null;
	}
	
	/**
	 * add worker message to a blocking queue
	 * @param s a string message to be added to the queue
	 */
	public void addWorkerMessage(String s) {
		this.workerMessages.add(s);
	}


	/**
     * Derives the range for a given worker
     * @param wR - the worker's record
     * @param curNum - the current number being worked on, i.e. the global upper bound
     * @param currentLower - the current lower bound for the range
     * @return - returns a tuple, where bound[0] = lower bound and bound[1] = upper bound
     */
    /*
    public BigInt[] deriveRange(WorkerRecord wR, BigInt curNum, BigInt currentLower){
        BigInt[] bound = new BigInt[2];
        
        //score multiplier -> calculation : range[i] = lower + totalNums*(score[i]/totalScore)
        BigInt size = new BigInt(curNum.subtract(new BigInt("3")).add(new BigInteger("1"))); //Get total numbers in the range
        max_work = new BigInt(curNum.subtract(currentLower));
        
        if(size.gt(totalScore)) {
        	BigInteger fraction = size.divide(totalScore); //get totalNums/totalScore
            
            System.out.println("--Fraction=" +fraction);
           
            //get totalNums*(score[i]/totalScore)
            BigInteger delta = fraction.multiply(new BigInt(Integer.toString(wR.getScore()))); 
            BigInt dt = new BigInt(delta);
            
            //Set bounds
            bound[0] = currentLower;
            bound[1] = new BigInt(currentLower.add(dt));
            if (bound[1].gt(curNum)){
                bound[1] = curNum;
            }
            
           
            if(dt.gt(max_work)) {
            	bound[1] = new BigInt(bound[0].add(max_work));
            }
        }
        else {
        	bound[0] = currentLower;
            bound[1] = new BigInt(curNum.subtract(new BigInteger("2")));
        }
        
        
        System.out.println("--- max_work="+max_work);
        System.out.println("--- bound[0]="+bound[0]);
        System.out.println("--- bound[1]="+bound[0]);
        return bound;
    }
*/
	/**
	 * used to send the range assigned to the worker, to the actual worker. Makes use of worker records and related data structs
	 * @param wR workerRecord to whom to push the data
	 * @param range the range worker needs to check if its divides or not
	 * @param current the number which is being considered if its prime or not
	 * @return true on success
	 */
    private boolean sendRange(WorkerRecord wR, BigInt[] range, BigInt current){
    	
    	
    	//Add to the hashset
    	if(!num_to_total.containsKey(current)) {
    		num_to_total.put(current, new BigInt(range[1].subtract(range[0])));
    	}
    	else {
    		num_to_total.put(current, new BigInt(num_to_total.get(current).add(range[1].subtract(range[0]))));
    	}
    	
    	
    	
        wR.setWorkrange(range);
        wR.startWork();
        //send range
        
        ActiveWorkers.put(wR.getWID(),true);
        System.out.println("Sending "+ serializeWorkRange(range, current)+ " to worker");
        while(true) {
        	try {
        		wR.getWc().sendMessage(serializeWorkRange(range, current));
        		wR.setworkerTimeout(System.currentTimeMillis());
        		WorkingWorkers.add(wR);
        		
        		break;
        	}
        	catch(Exception e) {
        		
        	}
        }
        
        return false;
    }
    
	/**
	 * this function can be used when iterating over list of working workers
	 * used to send the range assigned to the worker, to the actual worker. Makes use of worker records and related data structs
	 * @param wR workerRecord to whom to push the data
	 * @param range the range worker needs to check if its divides or not
	 * @param concur if ran inside of iterating over working workers list
	 * @param current the number which is being considered if its prime or not
	 * @return true on success
	 */
    private boolean sendRange(WorkerRecord wR, BigInt[] range, BigInt current, boolean concur){
    	
        wR.setWorkrange(range);
        wR.startWork();
        //send range
        
        ActiveWorkers.put(wR.getWID(),true);
        System.out.println("Sending "+ serializeWorkRange(range, current)+ " to worker " + wR.getWID());
        while(true) {
        	try {
        		wR.getWc().sendMessage(serializeWorkRange(range, current));
        		wR.setworkerTimeout(System.currentTimeMillis());
        		if(!concur) {
        			WorkingWorkers.add(wR);
        		}
        		
        		break;
        	}
        	catch(Exception e) {
        		
        	}
        }
        
        return false;
    }
    
    /**
     * serialize work range into a string
     * @param range on which the worker is going to work on
     * @param current number the worker is working on
     * @return serialized version of the workrange
     */
    private String serializeWorkRange(BigInt[] range, BigInt current) {
    	StringBuilder s = new StringBuilder();
    	s.append("type:sendTask upper:"+range[1]+" lower:"+range[0]+" tested:"+current);
    	
    	return s.toString();
    }

    /**
     * modifies worker score depending on how close the delta is to the desired timeout range
     * @param wR workerRecord who's score needs to be modified
     * @param delta the time it took for the worker to work on the last assignment
     */
    private void modifyWorkerScore(WorkerRecord wR, long delta ) {
    	if(delta>TIMEOUT/4 && delta<TIMEOUT*3/4) {
    		
    	}else if(delta>TIMEOUT*3/4) {
    		wR.setScore(wR.getScore()/2);
    	}else if (wR.getScore()<9001){
    		wR.setScore(wR.getScore()+100);
    	}

    	System.out.println("Score  of WID: "+wR.getWID() + " is: "+wR.getScore());
    }
    
    /**
     * iterate the working workers to see if any of them finished work or timed out.
     * if finished work pushed into a worker queue. timed out then reassign the work
     * @param WorkingWorkers a linked list of worker Records to iterate over.
     */
    public void iterateWorkingWorkers(LinkedList<WorkerRecord> WorkingWorkers) {
//    	System.out.println("Checking for incoming messages from workers...");
    	LinkedList<WorkerRecord> deadRecords = new LinkedList<WorkerRecord>();
    	LinkedList<WorkerRecord> newRecords = new LinkedList<WorkerRecord>();
//    	for (WorkerRecord wR : WorkingWorkers) {
    	for(int i=0;i<WorkingWorkers.size();i++) {
    		WorkerRecord wR = WorkingWorkers.get(i);
    		try {

    				DataInputStream dis = wR.getWc().sockIn;
//    				System.out.println("2");
    				if(dis.available() > 0) {
	    				String msg = NetworkMessage.receive(dis);
	    				
	    				addWorkerMessage(msg);
	    				//System.out.println("reading from worker"+ msg );
	//    				System.out.println("3");
	    				Map<String, String> m = MessageDecoder.createmap(msg);
	//    				System.out.println("4");
	    				String result = m.get("divisor");
	    				BigInt tested = new BigInt( m.get("tested"));					//worker finished work
	    				
	    				BigInt result_big = new BigInt(result);
	    				if (result.equals("0")){
	    					System.out.println(wR.getCurrent()+ " is a prime? Divisor recieved is " + result);
	    					//primes.add(wR.getCurrent());
	    				}else {
	    					System.out.println(wR.getCurrent()+" divided by "+ result + " reported by "+ wR.getWID());
	    				}
	    				
	    				//Save the divisors results
	    				if(!num_to_divisors.containsKey(tested)) {
	    					num_to_divisors.put(tested, result_big);
	    				}
	    				else {
	    					num_to_divisors.put(tested, new BigInt(num_to_divisors.get(tested).add(result_big)));
	    				}
	    				
	    				//BigInt[] work_range=wR.getWorkrange();
	    				//System.out.println("Work range: " + work_range[1].subtract(work_range[0]));
	    				
	    				//Add if and only if all the ranges have been covered and no divisors
	    				BigInt max_range = new BigInt((tested.sqrt()).subtract(new BigInt("3")));
	    				if(num_to_total.get(tested).equals(max_range) && num_to_divisors.get(tested).equals(BigInt.ZERO)) {
	    					System.out.println("All ranges have been tested for " + tested + ", "+ max_range);
	    					primes.add(tested);
	    					st.writeResult("Prime: "+tested.toString());
	    				}
	    				
	    				
	    				/*
	    				//substract from workers
	    				num_to_workers.put(tested,num_to_workers.get(tested)-1);
	    				
	    				
	    				
	    				
	    				//Check if all workers have finished work and add to primes if it is one
	    				if(num_to_divisors.get(tested).equals(BigInt.ZERO) && num_to_workers.get(tested)==0) {
	    					primes.add(tested);
	    					st.writeResult("Prime: "+tested.toString());
	    				}
	    				*/
	    				
	    				wR.setResult(result);
    					System.out.println("msg:" + msg);
    					long delta = System.currentTimeMillis() - wR.getworkerTimeout();
    					modifyWorkerScore(wR, delta);
    					
    					wR.setworkerTimeout(0);
						deadRecords.add(wR);
						addToWorkerQueue(wR);
				
    				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("hi this is iter over working workers");
			}
//    		System.out.print("workerTout: "+wR.getworkerTimeout());
    		if(wR.getworkerTimeout()!=0) {								// worker timed out. work needs to be reassigned
    			//check for timeout
    			long curTime = System.currentTimeMillis();
    			long delta = curTime - wR.getworkerTimeout();
    			//System.out.print(" delta: "+delta );
    			if (delta > TIMEOUT) {
    				//worker timed out
    				System.out.println("Worker with WID "+wR.getWID()+" Timed out!");
    				deadRecords.add(wR);
    				
    				while(getWorkerQueue().isEmpty()) {
    					LinkedList<WorkerRecord> WorkingWorkers_modified = new LinkedList<WorkerRecord>();
    					for(WorkerRecord wR_modified : WorkingWorkers) {
    						if(wR_modified != wR) {
    							WorkingWorkers_modified.add(wR_modified);
    						}
    					}
    					
    					iterateWorkingWorkers(WorkingWorkers_modified);
    				}
    				
    				WorkerRecord nWR = getWorkerQueue().poll();
    				System.out.println("ReScheduling "+Integer.toString((wR.getWID())));
    	        	
    	            nWR.setWorkrange(wR.getWorkrange());
    	            nWR.setCurrent(wR.getCurrent());
    	        	sendRange(nWR, nWR.getWorkrange(), nWR.getCurrent(), false);
    	        	st.writeLast("Last checked : "+current.toString());
    	            BigInt[] range = nWR.getWorkrange();
    	            
    	            //ADDED "FIX" FOR -2
    	        	if(new BigInt(range[1].subtract(range[0])).gt(BigInt.ZERO)) {
    	        		sendRange(nWR, nWR.getWorkrange(), nWR.getCurrent(), false);
    	        	}
    	        	
    	        	
    	        	newRecords.add(nWR);
    	        	//st.writeLast("Last checked:"+current.toString());
    			}
    		}
    		//System.out.println();
    	
    	}
    	for(WorkerRecord w : deadRecords) {
    		WorkingWorkers.remove(w);
    	}
    	for(WorkerRecord w : newRecords) {
    		WorkingWorkers.add(w);
    	}
    	//System.out.println("finished iterating");
    }
    
    /**
     * get list of currently working workers
     * @return
     */
    public LinkedList<WorkerRecord> getWorkingWorkers() {
		return WorkingWorkers;
	}


    /**
     * set list of working workers
     * @param workingWorkers a linked list to replace
     */
	public void setWorkingWorkers(LinkedList<WorkerRecord> workingWorkers) {
		WorkingWorkers = workingWorkers;
	}
	
	/**
	 * remove a worker from working workers list
	 * @param wR worker record to be assigned
	 */
	public void removeFromWorkingWorkers(WorkerRecord wR) {
		WorkingWorkers.remove(wR);
	}

	/**
	 * assign range to the worker record next in the priority queue
	 * @param range
	 */
	private void assignRange(BigInt[] range) {
		if(!getWorkerQueue().isEmpty()) { //Wait for worker
		        	
        	WorkerRecord wR = WorkerQueue.peek();
        	
        	
        	System.out.println("Scheduling "+Integer.toString((wR.getWID())));
        	
        	BigInt[] range_copy = new BigInt[2];
        	range_copy[0]=range[0];
        	range_copy[1]=range[1];
        	
        	wR = pollFromQueue();
            wR.setWorkrange(range_copy);
          
            wR.setCurrent(current);
        	sendRange(wR, range_copy, current);
        	/*
        	try {
        		st.writeLast("Last checked:"+current.toString());
        	}catch(Exception e) {}
        	*/
        }
	}

	/**
     * to be run in a separate thread
     *  called once finalized range; assigns work to workers while they are in queue
     * @return if done
     */
    public boolean scheduleTask(){
        this.current = lower;
        BigInt currentLower = new BigInt("3");
        if(current.mod(new BigInteger("2")).equals(BigInteger.ZERO)){
            current = new BigInt(current.add(new BigInt(("1"))).toString(10));
        }
        
        //LL workUnit
        //scheduler:
        //check workUnit LL if empty
        
        
        //upper= new BigInt(upper.sqrt().add(BigInt.ONE));
        while(current.le(upper) && (primes.size()<target)){ //less or equal to upperbound
            //assign the range to workers. if range too small split it
        	//we will need to do something so it does not loop in idle
        	BigInt[] range = new BigInt[] {new BigInt(BigInt.ZERO), new BigInt(BigInt.ZERO)};
        	
        	range[0] = new BigInt("3");
        	range[1] = new BigInt(current.sqrt());
        	BigInt topRangeTarget = new BigInt(current.sqrt());
        	if(!getWorkerQueue().isEmpty()) {
	        	BigInt rangeSize = new BigInt(range[1].subtract(range[0]));
	        	if(rangeSize.gt(new BigInt(Long.toString(defaultAssignment)))) {
	        		//if one worker cannot do this by himself partition so it can
	        		
	        		System.out.println("range too big splitting! ");
	        		/*
	        		System.out.print(" rSize "+rangeSize.toString(10));
	        		System.out.print(" r0 "+range[0].toString(10));
	        		System.out.print(" r9 "+range[1].toString(10));
	        		System.out.println();
	        		*/
	        		
	        		
	        		while(rangeSize.gt(new BigInt(Long.toString(defaultAssignment)))) {
	        			//if range is too big for one worker to handle split it between many workers
	        			if(!getWorkerQueue().isEmpty()) {
	        				WorkerRecord wR = getWorkerQueue().peek();
		        			BigInt curA = new BigInt(Long.toString(defaultAssignment));
		        			BigInt curMod = new BigInt(Long.toString(wR.getScore()));
		        			
		        			BigInt curRange = curMod;
		        			curA = curMod;
		        			BigInt estEnd = new BigInt(curRange.add(range[0]));
		        			/*
		        			System.out.println("current Assignment sz " + curA.toString(10));
		        			System.out.println("current Assignment mod " + curMod.toString(10));
		        			System.out.println("current Assignment range sz " + curRange.toString(10));
		        			System.out.println("current Assignment estend " + estEnd.toString(10));
		        			*/
		        			if(estEnd.gt(topRangeTarget)) {
		        				estEnd = topRangeTarget;
		        			}
		        			range[1] = new BigInt(estEnd);
		        			assignRange(range);
		        			range[0] = new BigInt(estEnd.add(BigInt.TWO));
		        			
		        			rangeSize = new BigInt(topRangeTarget.subtract(range[0]));
	        			
		        			
	        			}
	        			//range[1] = topRangeTarget;
	            		iterateWorkingWorkers(this.WorkingWorkers);
	        		}
	        	}
	        	
	        	//ADDED "FIX" FOR -2
	        	if(new BigInt(range[1].subtract(range[0])).ge(BigInt.ZERO)) {
	        		assignRange(range);
	        	}
        		
        		st.writeLast("Last checked: "+current);
        		
        		current = new BigInt(current.add(new BigInt("2")).toString(10));
        	
        	}
        	iterateWorkingWorkers(this.WorkingWorkers);
        }  
           // int workerPoolSize = getWorkerQueue().size();
//           while((doneWorkers < workerPoolSize) || range[1].lt(new BigInt(current.sqrt().subtract(BigInt.TWO)))){
                //System.out.println(currentLower.toString(10));
/*				System.out.println("before");
            	while(getWorkerQueue().isEmpty()) {} //Wait for worker
            	System.out.println("after");
                WorkerRecord wR = WorkerQueue.peek();
                range = deriveRange(wR, current, currentLower);
                wR = pollFromQueue();
                wR.setWorkrange(range);
                wR.setCurrent(current);
                sendRange(wR, range, current); //send range to worker
                currentLower = new BigInt (range[1].add(new BigInt("1")).toString(10)); //get max value of the range
                //System.out.println(range[1].toString(10));*/
//            }
          
            //currentLower = new BigInt("3");
          
        
        //once done print primes found  

        if(primes.size()>1) {
           System.out.println("Finished Scheduling; Primes Found:");
        }
        else {
        	System.out.println("Finished Scheduling; Prime Found:");	
        }
        int counter =0;
        for (BigInt prime : primes) {
        	if (counter == target) {break;
        	
        	}
        	counter++;
        	System.out.println(prime.toString(10));
        }
        done=true;
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
        wR = pollFromQueue();
        BigInt[] range = oldWR.getWorkrange();
        
        ActiveWorkers.put(oldWR.getWID(),false);
        ActiveWorkers.put(wR.getWID(),true);
        wR.setWorkrange(range);
        wR.setCurrent(oldWR.getCurrent());
        sendRange(wR, range, wR.getCurrent()); //send range to worker
        
        
        return true;
    }

/**
 * process results once worker has finished working
 * @param wR worker Record who did the work
 * @param factors factors found of the current prime
 * @return true on validated results
 */
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

    /**
     * get worker queue
     * @return worker queue containing workers awaiting assignment
     */
    public PriorityBlockingQueue<WorkerRecord> getWorkerQueue() {
        return WorkerQueue;
    }

    /**
     * set the worker queue instance
     * @param workerQueue to be used to have the jobless workers
     */
    public void setWorkerQueue(PriorityBlockingQueue<WorkerRecord> workerQueue) {
        WorkerQueue = workerQueue;
    }
    
    /**
     * get first worker record from the queue
     * @return first record on the queue
     */
    public  WorkerRecord pollFromQueue() {
    	WorkerRecord wR = this.WorkerQueue.poll();
//    	setTotalScore(new BigInt(getTotalScore().subtract(new BigInt(Integer.toString(wR.getScore())))));
    	
    	return wR;
    }
/**
 * add worker to the queue of jobless workers
 * @param wR workerRecord to be added to the jobless workers queue
 */
    public void addToWorkerQueue(WorkerRecord wR){
    	//setTotalScore(new BigInt(getTotalScore().add(new BigInt(Integer.toString(wR.getScore())))));
        this.WorkerQueue.add(wR);
        
    }

/**
 * get total score of all workers in the queue
 * likely deprecated
 * @return get total score of all workers in the queue
 */
    public BigInt getTotalScore() {
        return totalScore;
    }

    /**
     * set total score bigInt to a specific number
     * @param totalScore of all workers in the jobless queue
     */
    public void setTotalScore(BigInt totalScore) {
        this.totalScore = totalScore;
    }

    /**
     * get active workers, workers which are currently working
     * deprecated
     * @return get hashmap of worker ids to isworking
     */
    public HashMap<Integer, Boolean> getActiveWorkers() {
        return ActiveWorkers;
    }

    /**
     * add a worker record to active workers
     * @param wR worker record to be added
     * @return true on success
     */
    public boolean putActiveWorker(WorkerRecord wR){
        this.ActiveWorkers.put(wR.getWID(), true);
        return true;
    }

    /**
     * deactivate active worker if timed out
     * likely deprecated
     * @param wR worker record which timed out
     * @return true on successful removal
     */
    public boolean deactivateActiveWorker(WorkerRecord wR){
        this.ActiveWorkers.put(wR.getWID(), false);
        return true;
    }

    /**
     * set active workers hashmap to the one passed
     * @param hasmap of active workers 
     */
    public void setActiveWorkers(HashMap<Integer, Boolean> activeWorkers) {
        ActiveWorkers = activeWorkers;
    }
    
    /**
     * get current bigint system is working on
     * @return bigint being checked for being a prime
     */
    public BigInt getCurrent() {
		return current;
	}

/**
 * set current number on which system is working on
 * @param current bigint to which set the number the system is working on
 */
	public void setCurrent(BigInt current) {
		this.current = current;
	}
	
	/**
	 * get primes discovered by the system
	 * @return arraylist of bigints which are prime numbers
	 */
	public ArrayList<BigInt> getPrimes() {
		return primes;
	}

/**
 * set prime list to the one passed
 * @param primes pass arraylist with primes
 */
	public void setPrimes(ArrayList<BigInt> primes) {
		this.primes = primes;
	}

	/**
	 * ran when task scheduling thread is started
	 */
	@Override
	public void run() {
		done=false;
		scheduleTask();
		
	}


	public void setStore(Store st) {
		this.st=st;
		
	}
	
	public boolean isDone() {
		return done;
		
	}
}
