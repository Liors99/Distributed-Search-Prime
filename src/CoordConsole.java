import java.io.*;
import java.math.*;
import java.util.*;

public class CoordConsole {

	public static void main(String[] args) {
		PrintStream console = new PrintStream(System.out);
		Scanner scan = new Scanner(System.in);
		boolean validRange = false;
		BigInteger lowerBound;
		BigInteger upperBound;
		int primeLimit;
		
		while (!validRange) {
			lowerBound = getBound(console, scan, "lower");
			upperBound = getBound(console, scan, "upper");
			
			if (lowerBound.compareTo(upperBound) >= 0) {
				console.println("Error: The lower bound cannot be higher or equal to the upper bound");
			}
			else {
				primeLimit = getPrimeCount(console, scan);
				validRange = true;
			}
			
		}
		
		//TODO: add the the actual numbers received
		System.out.println("The system will search for primes in range, press ENTER to begin:");
		scan.nextLine();
		
		
		
	}
	
	private static BigInteger getBound(PrintStream console, Scanner scan, String boundType) {
		BigInteger result = new BigInteger("0");
		boolean validInput = false;
		
		while (!validInput) {
			
			console.println("Please enter the "+boundType+" bound for the search:");
			try {
				result = parseInput(scan.nextLine());
				validInput = true;
			}
			catch(NumberFormatException e) {
				console.println("Error: Please enter a number in integer or scientific format");
			}
		}
		return result;
	}
	
	private static BigInteger parseInput(String str) throws NumberFormatException {
		BigInteger result = new BigInteger("0");
		if (str.matches("\\d*[eE]\\d*")) {
			String[] baseExp = str.split("[eE]");
			result = new BigInteger(baseExp[0]);
			result = result.pow(Integer.parseInt(baseExp[1]));
		}
		else {
			result = new BigInteger(str);
		}
		return result;
		
	}
	
	private static int getPrimeCount(PrintStream console, Scanner scan){
		int result = 0;
		boolean validInput = false;
		String input;
		
		while (!validInput) {
			
			console.println("Please enter the desired number of primes:");
			input = scan.nextLine();
			
			try {
				result = Integer.parseInt(input);
				if (result <= 0) {
					throw new NumberFormatException();
				}
				validInput = true;
			}
			catch (NumberFormatException e) {
				console.println("Error: Please enter a positive integer");
			}
		}
		
		return result;
	}
}
