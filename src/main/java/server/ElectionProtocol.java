package server;

import java.util.HashMap;

public class ElectionProtocol {
	
	/**
	 * Randomly generate a number to simulate the roll of a die. 
	 *
	 * @return	the randomly determined number
	 */
	public static double roll() {
		double roll = 0; 
		roll = Math.random();
		return roll; 
	}
	
	/**
	 * Comparison operator for dice rolls 
	 *
	 * @param	roll1 the random roll of object 1 
	 * @param  	roll2 the random roll of object 2 
	 * @return 	true if roll1 higher than roll2, false otherwise. 
	 */
	public static boolean isHigher(double roll1, double roll2) {
		if(roll1 > roll2) {
			return true; 
		}else if(roll1 == roll2) {
			//re-roll if equal
			roll1 = roll(); 
			roll2 = roll(); 
			isHigher(roll1,roll2); 
		} else {
			return false; 
		} 
		return true; 
	}
	
	public static void main(String args[]) {
		//All servers do their initial roll on their individual systems 
		double server1 = roll(); 
		double server2 = roll(); 
		double server3 = roll(); 
		
		String coord = "Server0"; 
		//Usage example 
		//Need to translate to servers communicating with one another
		
		//Server 1 challenges server 2, server 2 receives "initial"(in decoder) message
		//server 2 sends back roll 
		//Server 1 runs isHigher() and compares 
		//If Server 1 is higher, then compare server 1 to server 3 
		//If Server 2 is higher, then compare server 2 to server 3 
		//Server x challenges server 3, server 3 receives "initial"(in decoder) message 
		//server 3 sends back roll 
		//Server x runs isHigher() and compares 
		//If Server x is higher, then server x is the coordinator 
		//If Server 3 is higher, then server 3 is the coordinator 
		if(isHigher(server1,server2)) {
			
			if(isHigher(server1, server3)) { 
				coord = "Server1"; 
			} else {
				coord = "Server3"; 
			}
		} else {
			if(isHigher(server2,server3)) {
				coord = "Server2"; 
			} else {
				coord = "Server3";
			}
		}
		
		System.out.println(coord); 
		
		
	}

}
