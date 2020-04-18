package worker;

import java.math.*;
import java.util.*;

/**
 * This class runs as a thread to try dividing the subject by all numbers in the range
 * @author Mark
 *
 */
public class PrimeSearch extends Thread{

	BigInteger lower;
	BigInteger upper;
	BigInteger subject;
	BigInteger result;
	
	/**
	 * The base constructor for the prime search algorithm
	 * 
	 * @param lowerLimit
	 * @param upperLimit
	 * @param testedNumber the number we are trying to divide by numbers in the range
	 */
	public PrimeSearch(BigInteger lowerLimit, BigInteger upperLimit, BigInteger testedNumber) {
		result = BigInteger.ZERO;
		if (lowerLimit.mod(BigInteger.TWO) == BigInteger.ZERO) {
			lower = lowerLimit.subtract(BigInteger.ONE);
		}
		else {
			lower = lowerLimit;
		}
		if (upperLimit.mod(BigInteger.TWO) == BigInteger.ZERO) {
			upper = upperLimit.add(BigInteger.ONE);
		}
		else {
			upper = upperLimit;
		}
		subject = testedNumber;		
	}
	
	
	public void run() {
		result = search();
		
		if (result.equals(BigInteger.ZERO)){
			System.out.println("The number "+subject+ " is not divisible by anything in the range");
		}
		else {
			System.out.println("The number "+subject+ " is divisible by "+result);
		}
	}
	
	/**
	 * After the prime search object is constructed, the search function is called from within {@link run}
	 * @return The divisor in range, if found, 0 otherwise
	 */
	public BigInteger search() {
		BigInteger index = lower;
		System.out.println(lower);
		System.out.println(upper);

		if(lower.compareTo(BigInteger.TWO) <= 0 && upper.compareTo(BigInteger.TWO) >= 0) {
			System.out.println("2 is in range, testing if number is even");
			if (subject.mod(BigInteger.TWO) == BigInteger.ZERO ) {
				return BigInteger.TWO;
			}
		}
		int counter = 1000000;
		
		while (index.compareTo(upper) <= 0) {
			
			if (subject.mod(index) == BigInteger.ZERO) {
				return index;
			} else {
				index = index.add(BigInteger.TWO);
			}
			counter--;
			//For extremely long searches, prints out every 1 million attempted factors, to show progress
			//Very unlikely to happen, as the coordinator scheduler is much more efficient than that
			if (counter == 0) {
				counter=1000000;
				System.out.println(index);
			}
		}
		
		return BigInteger.ZERO;
		
	}
	
}
