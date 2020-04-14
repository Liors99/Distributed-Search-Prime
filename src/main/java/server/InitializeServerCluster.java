package server;

import data.BigInt;
import data.HandShakeSubscriber;
import data.MessageDecoder;

import java.net.BindException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class InitializeServerCluster {
/*
    1. you get to choose whether it is a first start or a reconnection to an existing architecture
    2. based on that, there is a decision made whether or not an election or reelection is appropriate
    3. if you are reconnecting, you get the updates you need to get in order to come back alive
    4. after your role is determined, you run the appropriate procedure for your role
*/
    public static final int port = 11000;

    public static List<ServerNetwork> ServerNetworkConnections;
    public static final String[] ips = {"127.0.0.1","127.0.0.1","127.0.0.1"};
    public static final Integer[] ports = {port, port+1, port+2};
    public static long up_time = 0;
    public static ServerNetwork server;
    public static Integer id;
    public static Integer LeaderId;
    public static int timeout=20;
    public static final Integer offset = 20;
    public static boolean[] offsetted = {false, false, false};
    public static boolean[] isAlive= {true, true, true};
    private static int listenerPort;
    public static Store Storage = new Store("STORAGE");
    public static WorkerDatabase wdb;
    public static HashSet<BigInt> Primes;
    
    public static void main(String args[]) throws Exception {
        //Keep track of server connections
        id = 0;
        Primes = new HashSet<BigInt>();
        //ServerNetworkConnections = new LinkedList<ServerNetwork>();

        if (args.length > 0) {
            try {
                id = Integer.parseInt(args[0]);
                if(id < 0 || id > 2) {
                    System.err.println("Argument" + args[0] + " must be 0,1 or 2.");
                    System.exit(1);
                }
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an integer.");
                System.exit(1);
            }
        }
        
        up_time = System.currentTimeMillis();
        server = new ServerNetwork(ips[id], ports[id]);//?
        new Thread(server).start();
        //If not initialized, then start
        System.out.println("ServerNetwork started!");
        establishConnections();
        System.out.println("Connections Established!");

        System.out.println("Initial leader election initiated!");
        LeaderId = -1;
        while(LeaderId == -1){
            //DO initial election
            LeaderId = initial_election();
        }

        if(LeaderId == -2) {
        	//you are recovering!
        	//recoverData();
        }
		
		
		if (id == 0) {
			listenerPort = 8000;
		}
		else if(id == 1) {
			listenerPort = 8001;
		}
		else {
			listenerPort = 8002;
		}
		wdb= new WorkerDatabase();
        assignRole(listenerPort);
        
        while(true) {}

    }
    
    public static void recoverData() {
    	
    	ArrayList<String> As = Storage.getLines();
    	for(String s : As) {
    		System.out.println(s);
    	}
    }
    
    public static void assignRole(int listenerPort) {
    	System.out.println("Leader selected:"+LeaderId);
    	
    	
        if(LeaderId==id) {
        	Coordinator c = new Coordinator(id, ServerNetworkConnections, server, wdb);
        	c.notMain(listenerPort);
        }
        else {
        	Subscriber s = new Subscriber(id, LeaderId, server, wdb);
        	s.notMain(listenerPort);

        	
        }
    	
    }

    //Check hash table to verify all connections made
    public static void establishConnections() {
        //check all combos are in the hashmap
        for(int i =0; i < 3; i++) {
            if(i == id) {
                continue;
            }
            //check if key in hashmap

            //if not start a connection
            Socket Sk = null;
            //(!server.hasKey_client_to_socket(ips[i]+Integer.toString(ports[i]))) 
            /*!server.hasKey_client_to_socket(ips[i]+Integer.toString(ports[i])) ||*/
            while(Sk == null && (!server.hasKey_client_to_socket(ips[i]+Integer.toString(ports[i]))) && (!server.hasKey_client_to_socket(ips[i]+Integer.toString(ports[i]+offset)))){ //check for +20
                try{
                    Sk = server.startConnection(ips[i],ports[i], ips[id], ports[id]+(offset*(i+1)));
                    System.out.println("Initiated Connection to " + ips[i] + " "+ Integer.toString(ports[i]));
                    System.out.println((!server.hasKey_client_to_socket(ips[i]+Integer.toString(ports[i]))) && (!server.hasKey_client_to_socket(ips[i]+Integer.toString(ports[i]+offset))));
                    offsetted[i] = false; 
                    
                    server.addServer(ips[i], ports[i]);
                    server.addServer(ips[id], ports[id]+(offset*(i+1)));
                }catch(BindException e){
                    //System.out.println("got an exception");
                    
                    //offsetted[i] = true; 
                    //break;
                    //e.printStackTrace();
                }catch(Exception e) {
                    
                }
            }

        }
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
    * @return winner id or -1 if failed
    */
    public static Integer initial_election() throws Exception{
        HandShakeSubscriber Hs = new HandShakeSubscriber(10000);
        String serializedToken = Hs.serializeHandShake(Integer.toString(id));
        double this_token = Hs.getToken();
        System.out.println(id+ " "+ this_token);
        
        //notify everyone of our #
        for (int i=0;i<3;i++){
            if (i==id){
                continue;
            }
            //check if in hashtable or +20
            int p = (offsetted[i])?ports[i]+offset*i:ports[i];
            server.printConnections();
            System.out.println(p);
            server.send(ips[i], p, serializedToken);
        }

        String responses[] = new String[2];
        responses[0] = server.receiveNextMessageWithTimeout(timeout);
        responses[1] = server.receiveNextMessageWithTimeout(timeout);

        HandShakeSubscriber HsDecoded1 = new HandShakeSubscriber();
        HandShakeSubscriber HsDecoded2 = new HandShakeSubscriber();
        
        if (responses[0]!=null && responses[1]!=null) {
        	HsDecoded1.parseHandShake(responses[0]);
        	HsDecoded2.parseHandShake(responses[1]);

        	double[] vals = new double[3];
        	vals[id] = this_token;
        	vals[HsDecoded1.getID()] = HsDecoded1.getToken();
        	vals[HsDecoded2.getID()] = HsDecoded2.getToken();
        
        	System.out.println("------ Vals: -----");
        	for(double d: vals){
        		System.out.println(d);
        	}


        	double max = findMax(vals);
        	int winner = findWinnerID(vals, max);
        	return winner;
        }
        System.out.println("Timed out during election.");
        return -2;
        
    }

    public static int findWinnerID(double[] vals, double max){
        int counter = 0;
        int winner = -1;
        for(int i=0;i<3;i++){
            if(max == vals[i]){
                winner = i;
                counter++;
            }
        }
        if (counter > 1) {
            return -1;
        }
        return winner;
    }

    public static double findMax(double[] vals){
        double max = -1;
        //find max double token
        for(double v : vals){
            if (v > max){
                max = v;
            }
        }
        return max;
    }
    
 
    


    public static Integer reelection() throws Exception{
    		
    	 HandShakeSubscriber Hs = new HandShakeSubscriber(10, id, up_time);
         String serializedToken = Hs.serializeHandShake(Integer.toString(id));
         double this_token = Hs.getToken();
         System.out.println(" THIS SERVERS UP TIME: "+ this_token);
         
         //notify everyone of our #
         for (int i=0;i<3;i++){
             if (i==id || !isAlive[i]){
                 continue;
             }
             //check if in hashtable or +20
             int p = (offsetted[i])?ports[i]+offset*i:ports[i];
             server.printConnections();
             server.send(ips[i], p, serializedToken);
         }
         
         if(isAlive[(id+1)%3] || isAlive[(id+2)%3]) {
        	 String responses[] = new String[2];
        	 
        	 Thread.sleep(2000);
        	  
        	 while(!MessageDecoder.findMessageType(server.peekNextMessage()).contentEquals("HSS")) {}
        	 

             responses[0] = server.receiveNextMessage();
             

             HandShakeSubscriber HsDecoded1 = new HandShakeSubscriber();
             
             if (responses[0]!=null) {		 
            	 HsDecoded1.parseHandShake(responses[0]);
            	 if(HsDecoded1.getToken() > up_time) {
            		 System.out.println("Server " + id + ", I'm the leader");
            		 LeaderId=id;
            	 }
            	 else {
            		 System.out.println("Server " + HsDecoded1.getID() + ", is the leader");
            		 LeaderId=HsDecoded1.getID();
            	 }        
             }
         }
         else {
        	 LeaderId=id;
        	 System.out.println("Server " + id + ", I'm the leader");
         }
         
         assignRole(listenerPort);
         return LeaderId;
    }
    
    
    
}
