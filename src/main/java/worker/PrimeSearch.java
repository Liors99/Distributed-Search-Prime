package worker;

import java.math.*;
import java.util.*;

public class PrimeSearch extends Thread{

	BigInteger lower;
	BigInteger upper;
	BigInteger subject;
	
	public PrimeSearch(BigInteger lowerLimit, BigInteger upperLimit, BigInteger testedNumber) {
		lower = lowerLimit;
		upper = upperLimit;
		subject = testedNumber;		
	}
	
	public void run() {
		BigInteger i= lower;
		while(i.compareTo(upper) < 0) {
			System.out.println(i.toString());
			i = i.add(BigInteger.ONE);
		}
	}
	
	public BigInteger search() {
		BigInteger index = lower;
		if(lower.compareTo(BigInteger.TWO) <= 0) {
			if (subject.mod(BigInteger.TWO) == BigInteger.ZERO ) {
				return BigInteger.TWO;
			}
		}
		
		
		return BigInteger.ZERO;
	}
	
}
