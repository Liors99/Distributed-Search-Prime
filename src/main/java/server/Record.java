package server;

import java.sql.Timestamp;

public class Record {
    private String IP;
    private int Port;
    private Timestamp timeout;

    Record(){}

    Record(String IP, int Port, Timestamp timeout){
        this.IP = IP;
        this.Port = Port;
        this.timeout = timeout;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return Port;
    }

    public void setPort(int port) {
        Port = port;
    }

    public Timestamp getTimeout() {
        return timeout;
    }

    public void setTimeout(Timestamp timeout) {
        this.timeout = timeout;
    }
}
