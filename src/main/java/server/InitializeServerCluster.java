package server;

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
	
	public static void main(String args[]) {
		//Keep track of server connections 
		int id = 0; 
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
		//DO initial election 
		
	}
	
	//Check hash table to verify all connections made 
	public static void establishConnections(int id, ServerNetwork server) {
		//check all combos are in the hashmap 
		HashMap<String, Socket> client_to_socket = server.getClient_to_socket();
		for(int i =0; i < 3; i++) {
			if(i == id) {
				continue; 
			} 
			//check if key in hashmap 
			//TODO: Implement function in ServerNetwork to verify hashmap 
			
			//if not establish connection and add to hashmap 
			
		}
	}
}
