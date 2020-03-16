package server;

import java.math.BigInteger;
import java.sql.Timestamp;

public class WorkerRecord extends Record{
    private int WID;
    private double score;
    private Timestamp startedWork;
    private BigInteger[] workrange;
    static final double DEFAULTSCORE=100;

    WorkerRecord(){super();}

    // you can use new Timestamp(System.currentTimeMillis());
    WorkerRecord(String IP, int Port, int WID, double score, Timestamp timeout){
        super(IP, Port, timeout);
        this.setScore(score);
        this.setWID(WID);
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void stopWork(){
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
        startedWork = new Timestamp(System.currentTimeMillis());
    }

    public BigInteger[] getWorkrange() {
        return workrange;
    }

    public void setWorkrange(BigInteger[] workrange) {
        this.workrange = workrange;
    }
}
