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
    
    Record(String serial){
    	String [] split=serial.split(" ");
    	for (int i=0; i<split.length; i++) {
    		String [] sub=split[i].split(":");
    		if (sub[0].equals("IP")) {
    			IP=sub[1];	
    		}
    		else if (sub[0].equals("Port")) {
    			Port=Integer.parseInt(sub[1]);
    		}
    		else if (sub[0].equals("timeout")) {
    			if (i==split.length-2) {
    			    timeout=Timestamp.valueOf(sub[1]+" "+split[i+1]);
    			    break;
    			}
    			else {
    				timeout=Timestamp.valueOf(sub[1]);
    			}
    		}
    	}
    	
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
    
    public String toString() {
    	String value="Object:Record{IP:"+IP+" Port:"+Port+" timeout:"+timeout.toString()+"}";
		return value;
    	
    }
}
