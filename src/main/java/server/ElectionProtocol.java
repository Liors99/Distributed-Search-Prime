package server;

import java.util.HashMap;

public class ElectionProtocol {
	public static void main (String args[]) {
		HashMap<String, Double> servers = new HashMap<String, Double>();
		
		servers.put("1", 0.0); 
		servers.put("2", 0.0); 
		servers.put("3", 0.0); 
		
		//Initial Roll 
		System.out.println(initialCoord(servers));
		
		//Roll using only number of servers and receive which is leader
		System.out.println(initialCoord(3));
		

		
	}
	
	
	private static int initialCoord(HashMap<String, Double> servers) {
		double roll = 0.0; 
		int coordinator = 0;
		
		for(int x = 1; x <= servers.size(); x++) {
			roll = Math.random();
			servers.put(Integer.toString(x),roll);
		}
		
		//Compare rolls
		double highestRoll = 0.0; 
		for(int x = 1; x <= servers.size(); x++) {
			if(servers.get(Integer.toString(x)) > highestRoll) {
				highestRoll = servers.get(Integer.toString(x)); 
			}
			else if (servers.get(Integer.toString(x)) == highestRoll) {
				x = 1; 
				roll = 0.0; 
				for(int x1 = 1; x1 <= servers.size(); x1++) {
					roll = Math.random();
					servers.put(Integer.toString(x1),roll);
				}
			}
		}
		
		for(int x = 1; x <= servers.size(); x++) {
			if(servers.get(Integer.toString(x)) == highestRoll) {
				coordinator = x;
			}
		}
		
		return coordinator; 
	}
	
	private static int initialCoord(int numServers) {
		double roll = 0.0; 
		int coordinator = 0;
		HashMap<String, Double> servers = new HashMap<String, Double>();
		
		for(int x = 1; x <= numServers; x++) {
			roll = Math.random();
			servers.put(Integer.toString(x),roll);
		}
		
		//Compare rolls
		double highestRoll = 0.0; 
		for(int x = 1; x <= numServers; x++) {
			if(servers.get(Integer.toString(x)) > highestRoll) {
				highestRoll = servers.get(Integer.toString(x)); 
			}
			else if (servers.get(Integer.toString(x)) == highestRoll) {
				x = 1; 
				roll = 0.0; 
				for(int x1 = 1; x1 <= servers.size(); x1++) {
					roll = Math.random();
					servers.put(Integer.toString(x1),roll);
				}
			}
		}
		
		for(int x = 1; x <= numServers; x++) {
			if(servers.get(Integer.toString(x)) == highestRoll) {
				coordinator = x;
			}
		}
		
		return coordinator; 
	}
}
