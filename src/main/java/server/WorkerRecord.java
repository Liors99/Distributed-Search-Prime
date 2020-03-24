package server;

import data.BigInt;

import java.math.BigInteger;
import java.sql.Timestamp;

public class WorkerRecord extends Record{
    private int WID;
    private int score; //1->10 10 2 5 4 + 21 0.47
    private Timestamp startedWork;
    private BigInt[] workrange;
    private Boolean isDone;
    static final double DEFAULTSCORE=100;

    WorkerRecord(){super();}

    // you can use new Timestamp(System.currentTimeMillis());
    WorkerRecord(String IP, int Port, int WID, int score, Timestamp timeout){
        super(IP, Port, timeout);
        this.setScore(score);
        this.setWID(WID);
        this.isDone = true;
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
}
