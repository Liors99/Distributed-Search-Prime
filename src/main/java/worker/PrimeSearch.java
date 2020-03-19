package worker;

import java.math.*;
import java.util.*;

public class PrimeSearch extends Thread{

	BigInteger lower;
	BigInteger upper;
	BigInteger subject;
	
	public PrimeSearch(BigInteger lowerLimit, BigInteger upperLimit, BigInteger testedNumber) {
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
		BigInteger result = search();
		
		if (result.equals(BigInteger.ZERO)){
			System.out.println("The number "+subject+ " is not divisible by anything in the range");
		}
		else {
			System.out.println("The number "+subject+ " is divisible by "+result);
		}
	}
	
	public BigInteger search() {
		BigInteger index = lower;
		System.out.println(lower);
		System.out.println(upper);
		long start = System.currentTimeMillis();

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
			if (counter == 0) {
				counter=1000000;
				System.out.println(index);
			}
		}
		long stop = System.currentTimeMillis();
		System.out.println(stop-start);
		
		return BigInteger.ZERO;
		
	}
	
}
