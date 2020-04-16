package server;

import java.sql.Timestamp;

public class SubscriberRecord extends Record {
    SubscriberRecord(){super();}
    SubscriberRecord(String IP, int Port, Timestamp timeout){
        super(IP, Port, timeout);
    }
    SubscriberRecord(String IP, int Port){
        super(IP, Port, new Timestamp(System.currentTimeMillis()));
    }
    SubscriberRecord(String object){
    	super(object.split("super:")[1]);
    }
    
    public String toString() {
    	String value="";
    	try {
    	    value="Object:SubscriberRecord{super:"+super.toString()+"}";
    	}
    	catch (NullPointerException e){
			value="Object:SubscriberRecord{super:{null}}";
		}
		return value;
    	
    }

}
