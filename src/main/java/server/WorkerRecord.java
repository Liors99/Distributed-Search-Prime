package server;

import data.BigInt;

import java.math.BigInteger;
import java.sql.Timestamp;

public class WorkerRecord extends Record implements Comparable<WorkerRecord>{
    private int WID;
    private int score; //1->10 10 2 5 4 + 21 0.47
    private Timestamp startedWork;
    private BigInt[] workrange;
    private Boolean isDone;
    static final double DEFAULTSCORE=100;
    private long workerTimeout;
    private String result;
    
    /**
     * returns the last result
     * @return string result which is a bigint number last checked
     */
    public String getResult() {
		return result;
	}

    /**
     * set the value of result, related to the bigint last checked
     * @param result
     */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * long get timeout in millis
	 * @return timeout in milliseconds
	 */
	public long getworkerTimeout() {
		return workerTimeout;
	}

	/**
	 * set timeout in milliseconds
	 * @param timeout in milliseconds
	 */
	public void setworkerTimeout(long timeout) {
		this.workerTimeout = timeout;
	}
	private WorkerConnection wc;
    private BigInt current;

    /**
     * a constructor for worker record
     */
    WorkerRecord(){super();}

    /**
     * creates a new instance of worker record and sets specific value of selected parameters
     * @param IP of the worker
     * @param Port of the worker on the worker machine
     * @param WID unique worker id
     * @param score of the worker for the system
     * @param timeout set initial timeout
     * @param wc worker connection which contains data on tcp socket connection and has useful classes
     * @param current the current number on which worker is working on or last worked on
     */
    WorkerRecord(String IP, int Port, int WID, int score, Timestamp timeout, WorkerConnection wc, BigInt current){
        super(IP, Port, timeout);
        this.setScore(score);
        this.setWID(WID);
        this.isDone = true;
        this.wc=wc;
        this.current=current;
    }
    
    /**
     * creates a new instance of worker record and sets specific value of selected parameters 
    * @param IP of the worker
    * @param Port of the worker on the worker machine
    * @param WID unique worker id
    * @param score of the worker for the system
    * @param timeout set initial timeout
    * @param wc worker connection which contains data on tcp socket connection and has useful classes
    */
    WorkerRecord(String IP, int Port, int WID, int score, Timestamp timeout, WorkerConnection wc){
        super(IP, Port, timeout);
        this.setScore(score);
        this.setWID(WID);
        this.isDone = true;
        this.wc=wc;
    }
    
    
   
    /**
     * deserialize the worker record
     * @param serial serialized data about the worker
     */
	WorkerRecord(String serial){
    	super(serial.split(" super:")[1]);
    	String [] split=serial.split(" super:");
    	if (split[0].contains("Object:WorkerRecord{")) {
    		split=split[0].split("Object:WorkerRecord\\{");
    	}
    	String me=split[1];
    	String[] fields=me.split(" ");
    	for (int i=0; i<fields.length; i++) {
    		String [] keys=fields[i].split(":");
    		if(keys[0].equals("WID")) {
    			WID=Integer.valueOf(keys[1]);
    		}
    		else if (keys[0].equals("score")) {
    			score=Integer.valueOf(keys[1]);
    		}
    		else if (keys[0].equals("isDone")) {
    			if(!keys[1].equals("null")) {
    			   isDone=Boolean.valueOf(keys[1]);
    			}
    		}
    		else if (keys[0].equals("startedWork")) {
    			startedWork=Timestamp.valueOf(keys[1]+" "+split[i+1]);
    			i=i+1;
    		}
    		else if (keys[0].equals("workrange")) {
    			if (!keys[1].equals("null")){
    				String [] vals=keys[1].split(",");
    				workrange=new BigInt [vals.length];
    				for (int j=0; j<vals.length; j++) {
    					workrange[j]=new BigInt(vals[j]);
    				}
    							
    			}
    		}
    	}
    	
    }


    /**
     * change score based on time spent on previous task
     * @param delta
     */
    private void deriveScore(long delta){

    }

    /**
     * get unique worker id assigned to the worker
     * @return  unique int worker id
     */
    public int getWID() {
        return WID;
    }

    /**
     * set unique worker id 
     * @param WID int ide to be assigned to the worker
     */
    public void setWID(int WID) {
        this.WID = WID;
    }

    /**
     * get current worker score
     * @return int score
     */
    public int getScore() {
        return score;
    }

    /**
     * set worker score int
     * @param score to be set to
     */
    public void setScore(int score) {
        this.score = score;
    }


    /**
     * stops internal counters when worker is reported to have stopped working on its interval
     */
    public void stopWork(){
        this.isDone = true;
        Timestamp stop = new Timestamp(System.currentTimeMillis());
        long delta = stop.getTime() - startedWork.getTime();
        deriveScore(delta);
    }

    /**
     * get started work timestamp
     * @return timestamp of worker starting work
     */
    public Timestamp getStartedWork() {
        return startedWork;
    }

    /**
     * set startedwork timestamp to the value passed
     * @param startedWork set workers startedWork to the value passed
     */
    public void setStartedWork(Timestamp startedWork) {
        this.startedWork = startedWork;
    }
    
    /**
     * initialize internal counters when worker is assigned work 
     */
    public void startWork(){
        this.isDone = false;
        startedWork = new Timestamp(System.currentTimeMillis());
    }

    /**
     * get bigint worker is working on
     * @return bigint array representing the workrange of the worker
     */
    public BigInt[] getWorkrange() {
        return workrange;
    }

    /**
     * set the bigint workrange of the worker to the value passed
     * @param workrange the value to which set bigint workrange array
     */
    public void setWorkrange(BigInt[] workrange) {
        this.workrange = workrange;
    }

    /**
     * check if the worker is done
     * @return true if done
     */
    public Boolean getDone() {
        return isDone;
    }

    /**
     * set if worker is done or not
     * @param done boolean value; true if done
     */
    public void setDone(Boolean done) {
        isDone = done;
    }
    
    /**
     * get worker connection with socket information
     * @return get an instance of workerconnection class with relevant socket information to the worker
     */
    public WorkerConnection getWc() {
		return wc;
	}

    /**
     * set worker connection instance related to this class
     * @param wc pass a workerconnection instance with socket information related to this worker
     */
	public void setWc(WorkerConnection wc) {
		this.wc = wc;
	}
	/**
	 * get current number on which the worker either is working on or has last worked on ( if no job assignment at them moment)
	 * @return get current bigInt number
	 */
	public BigInt getCurrent() {
		return current;
	}

	/**
	 * set the number on which the worker is working on (trying to factor)
	 * @param current set the bigint on which the worker is working on
	 */
	public void setCurrent(BigInt current) {
		this.current = current;
	}

	/**
	 * override of compare method allowing workers to be compared via their score
	 * could be used in a priority queue
	 */
	@Override
	public int compareTo(WorkerRecord o) {
		if(this.getScore() > o.getScore()) {
			return -1;
		}
		else if (this.getScore() < o.getScore()) {
			return 1;
		}
		else {
			return 0;
		}
	}
	
	/**
	 * used to serialize the object to a string
	 */
	public String toString() {
		String ret="Object:WorkerRecord{WID:"+WID;
		ret=ret+" score:"+score;
		ret=ret+" isDone:"+isDone;
		if(startedWork!=null) {
		 ret=ret+" startedWork:"+startedWork.toString();
		}
		if (workrange!=null) {
		ret=ret+" workrange:";
		for (int i=0; i<workrange.length; i++) {
			ret=ret+workrange[i].toBigInteger().toString();
			if (i!=workrange.length-2) {
				ret=ret+",";
			}
		 }
		}
		try {
		ret=ret+" super:"+super.toString();
		}
		catch (NullPointerException e){
			ret=ret+" super:{null}";
		}
		return ret+"}";
		
	}
	

}
