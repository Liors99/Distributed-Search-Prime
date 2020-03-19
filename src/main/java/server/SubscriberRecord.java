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

}
