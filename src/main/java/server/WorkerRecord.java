package server;

import java.sql.Timestamp;

public class WorkerRecord extends Record{
    private int WID;
    private double score;
    static final double DEFAULTSCORE=100;

    WorkerRecord(){super();}

    // you can use new Timestamp(System.currentTimeMillis());
    WorkerRecord(String IP, int Port, int WID, double score, Timestamp timeout){
        super(IP, Port, timeout);
        this.setScore(score);
        this.setWID(WID);
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
}
