package server;

import data.HandShakeSubscriber;
import data.MessageDecoder;

import java.io.IOException;
import java.net.BindException;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    private static ConnectionListener listener;
    public static WorkerDatabase wdb;
    public static Store st;
    public static Boolean r=false;
    public static int recoverTimeout=5;
    private static Subscriber s;
    public static boolean reelectionStarted=false;
    
    /**
     * main function of the program.
     * Initializes server specific stuff as well as starts initial election.
     * @param args command line arguments
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        //Keep track of server connections
        id = 0;
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
        if (LeaderId==-2) {
        	recover();
        	//If all goes well recover() does not return
        }

        
		//Assign listener port to id
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
		listener = new ConnectionListener(wdb, listenerPort, null ,false);
		listener.start();
		s = new Subscriber(id, LeaderId, server, listener);
		
        assignRole(false); //Assign roles, but this is not a re-election
        
        while(true) {}

    }
    
    /**
     * announces who won the election. Also starts a coordinator instance if same id as leaderid
     * otherwise starts subscriber instance
     * 
     * in case of reelection has similar function
     * @param isReelection
     */
    public static void assignRole(boolean isReelection) {
    	System.out.println("Leader selected:"+LeaderId);
    	
    	
        if(LeaderId==id) {
        	
        	
        	Coordinator c = new Coordinator(id, server, listener);
        	if(isReelection) {
        		c.loadFromSubscriber(s);
        	}
        	c.notMain();
        }
        else {
        	//Subscriber s = new Subscriber(id, LeaderId, server, listener, st);
        	s.notMain();
        }

    	
    }

    /**
     * establishes and verifies connections to other instances in server cluster
     */
    public static void establishConnections() {
        //check all combos are in the hashmap
        for(int i =0; i < 3; i++) {
            if(i == id) {
                continue;
            }
            //check if key in hashmap

            //if not start a connection
            Socket Sk = null;
            int tries =0;
            //try 20 times (gotta be fast starting up)
            while(tries<20 && Sk == null && (!server.hasKey_client_to_socket(ips[i]+Integer.toString(ports[i]))) && (!server.hasKey_client_to_socket(ips[i]+Integer.toString(ports[i]+offset)))){ //check for +20
                tries++;
            	try{
                    Sk = server.startConnection(ips[i],ports[i], ips[id], ports[id]+(offset*(i+1)));
                    System.out.println("Initiated Connection to " + ips[i] + " "+ Integer.toString(ports[i]));
                    offsetted[i] = false; 
                    
                    server.addServer(ips[i], ports[i]);
                    server.addServer(ips[id], ports[id]+(offset*(i+1)));
                }catch(BindException e){

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
     * destroys existing server connections. Closes sockets then initializes a new hashmap for client to socket
     */
   public static void destroyConnections(){
	   HashMap<String, Socket> connections=server.getClient_to_socket();
       for(Socket i:connections.values()) {
    	  try {
			i.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
       }
       server.setClient_to_socket(new HashMap<String, Socket>());
   }
   
    /**
     * responsible for initial election
    * @return winner id or -2 if failed initial election. -2 is normal in case of subscriber/coordinator reconnecting after a crash
    */
    public static Integer initial_election() throws Exception{
        HandShakeSubscriber Hs = new HandShakeSubscriber(id,10000);
        String serializedToken = Hs.serializeHandShake();
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
            try {
                server.send(ips[i], p, serializedToken);
            }
            catch(Exception e) {
            	//Someone died
            }
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

    /**
     * identifies who is the winner provided everyones numbers
     * @param vals list of numbers for each server in cluster
     * @param max the max value which is the winning number
     * @return id of the winner
     */
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

    /**
     * allows to find a max value in double
     * @param vals a double array in which max value is to be found
     * @return max value in the array
     */
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
    
 
    

	/**
	 * reelection process is described here. Ran if coordinator has crashed and new leader is required. Ran after leader timeout.
	 */
    public static void reelection(){
    	
    	 HandShakeSubscriber Hs = new HandShakeSubscriber(id, 10, up_time);
    	 Hs.setReelection(true);
         String serializedToken = Hs.serializeHandShake();
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
             try {
            	 System.out.println("Tryign to send to "+p);
				server.send(ips[i], p, serializedToken);
				System.out.println("Send message to "+p);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Failed sending to "+p);
				continue;
			}
         }
         
         System.out.println("Finished reelection");
         reelectionStarted=true;
         System.out.println(isAlive[(id+1)%3]+", " + isAlive[(id+2)%3]);
        
         
        
    }
    
    /**
     * helps with reelection process by working with response provided by other servers in the cluster
     * @param response of the other server in a cluster.
     * @return leader id
     */
    public static int ElectReelectionLeader(String response) {
    	int id_recv = -1;
		 if(isAlive[(id+1)%3] || isAlive[(id+2)%3]) {
			 
	         HandShakeSubscriber HsDecoded1 = new HandShakeSubscriber();
	         if (response!=null) {		 
	        	 HsDecoded1.parseHandShake(response);
	        	 id_recv= HsDecoded1.getID();
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
		 
		
		 reelectionStarted=false;
	     assignRole(true);
	     return LeaderId;
    }
    
    /**
     * works with recovery in case of a crash.
     * @throws Exception
     */
   public static void recover() throws Exception {
	   //set up listener for workers
	   if (id == 0) {
			listenerPort = 8000;
		}
		else if(id == 1) {
			listenerPort = 8001;
		}
		else {
			listenerPort = 8002;
		}
	 //create role
	wdb= new WorkerDatabase();
    listener = new ConnectionListener(wdb, listenerPort, null ,false);
    listener.start();
   	Subscriber rs=new Subscriber(id, LeaderId, server, listener);
   	//Enter Recovery mode
   	destroyConnections();
   	establishConnections();
       //broadcast recovery
       for (int i=0;i<3;i++){
           if (i==id){
               continue;
           }
           //check if in hashtable or +20
           int p = (offsetted[i])?ports[i]+offset*i:ports[i];
           server.printConnections();
           try {
               server.send(ips[i], p, "type:recover id:"+id);
           }
           catch(Exception e) {
        	   //Not sure who is active
           }
       }
       boolean recovering=true;
       //Set up time out
       long startTime=System.currentTimeMillis();
       long duration=0;
       while(recovering && duration<recoverTimeout) {
    	   //check if message available 
       	if(server.viewNextMessage()!=null) {
   			String next_message = server.receiveNextMessage();
   			System.out.println("Recovery recieved:"+next_message);
   			Map<String, String> m;
   			//won't have normal format
   			if(next_message.contains(":Store")) {
   				m=new HashMap<String, String>();
   				m.put("type", "Store");
   			}
   			else {
   		       m=MessageDecoder.createmap(next_message);
   			}
   			//Task to solve
   		    if(m.get("type").equals("COR_Goal")) {
           	  rs.setGoal(m);	
           	}
   		    //Leader
   		    else if(m.get("type").equals("l")) {
           		LeaderId=Integer.parseInt(m.get("leader"));
           	}
   		    //Store file contains primes and current
   		    else if(m.get("type").equals("Store")) {
   		    	try {
   		    	rs.setStore(next_message.split("file ")[1]);
   		    	}
   		    	catch(Exception e){
   		    		
   		    	}
   		    }
   		    //Recovery finished
   		    else if(m.get("type").equals("RC-Done")) {
   		    	if(LeaderId==-2) {
   		    		LeaderId=Integer.parseInt(m.get("id"));
   		    	}
   		        System.out.println("Recovery Complete!");
   		        server.sendServers("type:Notification Note:Recovered ID:"+id, id);
   		        rs.notMain();
   		        recovering=false;
   		    }
       	}
       	    long endTime = System.currentTimeMillis();
		    duration = (endTime - startTime)/1000;
       }
       //no active
       if(LeaderId==-2) {
    	   System.out.println("The System has entered an unrecoverable state");
    	   System.out.println("Shutting Down");
    	   System.exit(-1);
       }
       //Repeat above with other server
       else {
    	   startTime=System.currentTimeMillis();
           duration=0;
           //Find the other active server
           int other=-1;
           for(int i=0; i<3; i++) {
        	   if (i!=LeaderId && i!=id) {
        		 other=i;  
        	   }
           }
           int p = (offsetted[other])?ports[other]+offset*other:ports[other];
           server.printConnections();
           try {
        	   //Coordinator is too slow can you help me?
               server.send(ips[other], p, "type:recoverS id:"+id);
           }
           catch(Exception e) {
        	   //Not sure who is active
           }
    	   //Talk to other subscriber, who will win election by default
    	   while(recovering && duration<recoverTimeout) {
    	       	if(server.viewNextMessage()!=null) {
    	   			String next_message = server.receiveNextMessage();
    	   			System.out.println("Recovery recieved:"+next_message);
    	   			Map<String, String> m;
    	   			if(next_message.contains(":Store")) {
    	   				m=new HashMap<String, String>();
    	   				m.put("type", "Store");
    	   			}
    	   			else {
    	   		       m=MessageDecoder.createmap(next_message);
    	   			}
    	   		    if(m.get("type").equals("COR_Goal")) {
    	           	  rs.setGoal(m);	
    	           	}
    	   		    else if(m.get("type").equals("l")) {
    	           		LeaderId=Integer.parseInt(m.get("leader"));
    	           	}
    	   		    else if(m.get("type").equals("store")) {
    	   		    	rs.setStore(next_message.split("file:")[0]);
    	   		    }
    	   		    else if(m.get("type").equals("RC-Done")) {
    	   		    	if(LeaderId==-2) {
    	   		    		LeaderId=Integer.parseInt(m.get("id"));
    	   		    	}
    	   		        System.out.println("Recovery Complete!");
    	   		        server.sendServers("type:Notification Note:Recovered ID:"+id, id);
    	   		        rs.notMain();
    	   		        recovering=false;
    	   		    }
                }
    	   }
       }
       
   }
   
   /**
    * announces that server was disconected. used to notify workers that coordinator has failed.
    * @param id
    */
   public static void sendDisconnectedServer(int id) {
	   listener.announceDisconnectedServer(id);
   }
    
}
