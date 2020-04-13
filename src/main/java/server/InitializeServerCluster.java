package server;

import data.HandShakeSubscriber;

import java.net.BindException;
import java.net.Socket;
import java.util.HashMap;
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
    public static ServerNetwork server;
    public static Integer id;
    public static Integer LeaderId;
    public static int timeout=20;
    public static final Integer offset = 20;
    public static boolean[] offsetted = {false, false, false};

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

        System.out.println("Leader selected:"+LeaderId);
        if(LeaderId==id) {
        	Coordinator c = new Coordinator(id, ServerNetworkConnections, server);
        	c.notMain();
        }
        else {
        	Subscriber s;
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
                }catch(BindException e){
                    System.out.println("got an exception");
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


    public static Integer reelection(){
        return 1;
    }
}
