package server;

import java.sql.Timestamp;

public class Record {
    private String IP;
    private int Port;
    private Timestamp timeout;
    public boolean flag=true;

    /**
     * empty record constructor. class used as a parent for worker record.
     */
    Record(){}
    /**
     * creates a new instance of a record
     * @param IP of the instance being recorded
     * @param Port of the connection with the instance
     * @param timeout a timestamp
     */
    Record(String IP, int Port, Timestamp timeout){
        this.IP = IP;
        this.Port = Port;
        this.timeout = timeout;
    }
    
    /**
     * used for parsing serialized strings
     * @param serial string message to be deserialized
     */
    Record(String serial){
    	if(serial.contains("Object:Record")) {
    		serial=serial.split("Object:Record\\{")[1].split("\\}")[0];
    	}
    	if (serial.equals("null")) {
    	  return;
    	}
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

    /*
     * get ip of the record
     */
    public String getIP() {
        return IP;
    }

    /**
     * set the record ip
     * @param IP of the instance being correlated to the record
     */
    public void setIP(String IP) {
        this.IP = IP;
    }

    /**
     * get port of the instance
     * @return int port number
     */
    public int getPort() {
        return Port;
    }

    /**
     * set port of the instance
     * @param port set port
     */
    public void setPort(int port) {
        Port = port;
    }

    /**
     * get the value of timeout
     * @return Timestamp timeout
     */
    public Timestamp getTimeout() {
        return timeout;
    }

    /**
     * set the value of the timeout
     * @param timeout the value to be set
     */
    public void setTimeout(Timestamp timeout) {
        this.timeout = timeout;
    }
    
    /**
     * serialize the object to string
     */
    public String toString() {
    	String value="Object:Record{IP:"+IP+" Port:"+Port+" timeout:"+timeout.toString()+"}";
		return value;
    	
    }
}
