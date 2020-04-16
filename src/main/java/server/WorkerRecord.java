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
    
    public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public long getworkerTimeout() {
		return workerTimeout;
	}

	public void setworkerTimeout(long timeout) {
		this.workerTimeout = timeout;
	}
	private WorkerConnection wc;
    private BigInt current;

    WorkerRecord(){super();}

    // you can use new Timestamp(System.currentTimeMillis());
    WorkerRecord(String IP, int Port, int WID, int score, Timestamp timeout, WorkerConnection wc, BigInt current){
        super(IP, Port, timeout);
        this.setScore(score);
        this.setWID(WID);
        this.isDone = true;
        this.wc=wc;
        this.current=current;
    }
    
    WorkerRecord(String IP, int Port, int WID, int score, Timestamp timeout, WorkerConnection wc){
        super(IP, Port, timeout);
        this.setScore(score);
        this.setWID(WID);
        this.isDone = true;
        this.wc=wc;
    }
    
    
   

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

    public int getWID() {
        return WID;
    }

    public void setWID(int WID) {
        this.WID = WID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public void stopWork(){
        this.isDone = true;
        Timestamp stop = new Timestamp(System.currentTimeMillis());
        long delta = stop.getTime() - startedWork.getTime();
        deriveScore(delta);
    }

    public Timestamp getStartedWork() {
        return startedWork;
    }

    public void setStartedWork(Timestamp startedWork) {
        this.startedWork = startedWork;
    }
    public void startWork(){
        this.isDone = false;
        startedWork = new Timestamp(System.currentTimeMillis());
    }

    public BigInt[] getWorkrange() {
        return workrange;
    }

    public void setWorkrange(BigInt[] workrange) {
        this.workrange = workrange;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }
    
    public WorkerConnection getWc() {
		return wc;
	}

	public void setWc(WorkerConnection wc) {
		this.wc = wc;
	}
	
	public BigInt getCurrent() {
		return current;
	}

	public void setCurrent(BigInt current) {
		this.current = current;
	}

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
