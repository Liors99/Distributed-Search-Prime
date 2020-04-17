
//either no arguments or mode + Coordinator IP
package server;
import java.io.*;
import java.math.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class CoordConsole {
	
	static boolean validRange = false;
	static BigInteger lowerBound;
	static BigInteger upperBound;
	static int primeLimit;
	static boolean quit=false;
 
	
	//Change values to defaults
	public static void resetVals() {
		lowerBound=new BigInteger("-1");
		upperBound=new BigInteger("-1");
		primeLimit=-1;
		validRange=false;
	}


	public static void console() {
		PrintStream console = new PrintStream(System.out);
		Scanner scan = new Scanner(System.in);
		
		while (!validRange) {
			lowerBound = getBound(console, scan, "lower");
			if(quit) {
				return;
			}
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
	
	static BigInteger getBound(PrintStream console, Scanner scan, String boundType) {
		BigInteger result = new BigInteger("0");
		boolean validInput = false;
		
		while (!validInput) {
			
			console.println("Please enter the "+boundType+" bound for the search: or q to quit");
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
	
	public static BigInteger parseInput(String str) throws NumberFormatException {
		BigInteger result = new BigInteger("0");
		if (str.matches("\\d*[eE]\\d*")) {
			String[] baseExp = str.split("[eE]");
			result = new BigInteger(baseExp[0]);
			result = result.multiply(new BigInteger("10").pow(Integer.parseInt(baseExp[1])));
		}
		else {
			if(str.equals("q")) {
				quit=true;
				result=new BigInteger("1");
			}
			else {
			result = new BigInteger(str);
			if (result.compareTo(new BigInteger("0")) <= 0) {
				throw new NumberFormatException();
			}
		}
		}
		return result;
		
	}
	
	static int getPrimeCount(PrintStream console, Scanner scan){
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