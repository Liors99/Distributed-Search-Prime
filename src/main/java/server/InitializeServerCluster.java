package server;

import data.HandShakeSubscriber;

import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InitializeServerCluster {

	public static final int port = 11000;

	public static List<ServerNetwork> ServerNetworkConnections;
	public static final String[] ips = {"127.0.0.1","127.0.0.1","127.0.0.1"};
	public static final Integer[] ports = {port, port+1, port+2};
	public static ServerNetwork server;
	public static Integer id;
	public static Integer LeaderId;

	public static void main(String args[]) throws Exception {
		//Keep track of server connections
		id = 0;
		ServerNetworkConnections = new LinkedList<ServerNetwork>();

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
		server = new ServerNetwork(ips[id], ports[id]);
		new Thread(server).start();
		//If not initialized, then start
		establishConnections();

		LeaderId = -1;
		while(LeaderId == -1){
			//DO initial election
			LeaderId = initial_election();
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
			if(!server.hasKey_client_to_socket(ips[i]+Integer.toString(ports[i]))){
				//if not start a connection
				Socket Sk = null;
				while(Sk == null){
					try{
						Sk = server.startConnection(ips[i],ports[i]);
					}catch(Exception e){
						//e.printStackTrace();
					}
			}
			}
		}
	}

	/**
	* @return winner id or -1 if failed
	*/
	public static Integer initial_election() throws Exception{
		HandShakeSubscriber Hs = new HandShakeSubscriber(10000);
		String serializedToken = Hs.serializeHandShake(Integer.toString(id));

		//notify everyone of our #
		for (int i=0;i<3;i++){
			if (i==id){
				continue;
			}
			server.send(ips[id],ports[id],serializedToken);
		}
		String responses[] = new String[2];
		responses[0] = server.recieveNextMessage();
		responses[1] = server.recieveNextMessage();

		HandShakeSubscriber HsDecoded1 = new HandShakeSubscriber();
		HandShakeSubscriber HsDecoded2 = new HandShakeSubscriber();

		HsDecoded1.parseHandShake(responses[0]);
		HsDecoded2.parseHandShake(responses[1]);

		double[] vals = new double[3];
		vals[0] = Hs.getToken();
		vals[1] = HsDecoded1.getToken();
		vals[2] = HsDecoded2.getToken();

		double max = -1;
		//find max double token
		for(double v : vals){
			if (v > max){
				max = v;
			}
		}


		int counter = 0;
		int winner = -1;
		for(int i=0;i<3;i++){
			if(max == vals[i]){
				winner = i;
				counter++;
			}
		}
		if (counter != 0) {
			return -1;
		}
		return winner;
	}

	public Integer reelection(){
		return 1;
	}
}
