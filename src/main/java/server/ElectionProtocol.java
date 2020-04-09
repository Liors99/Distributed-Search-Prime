package server;

import java.util.HashMap;

public class ElectionProtocol {
	
	/**
	 * Randomly generate a number to simulate the roll of a die. 
	 *
	 * @return      the randomly determined number
	 */
	public static double roll() {
		double roll = 0; 
		roll = Math.random();
		return roll; 
	}
	
	/**
	 * Comparison operator for dice rolls 
	 *
	 * @param  roll1 the random roll of object 1 
	 * @param  roll2 the random roll of object 2 
	 * @return      true if roll1 higher than roll2, false otherwise. 
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

}
