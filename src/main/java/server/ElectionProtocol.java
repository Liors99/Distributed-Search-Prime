package server;

import java.util.HashMap;

public class ElectionProtocol {

	/**
	 * Randomly determine the coordinator on initial startup
	 *
	 * @param  numServers the number of servers in the system
	 * @return      the randomly determined coordinator
	 */
	public int initialCoord(int numServers) {
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
