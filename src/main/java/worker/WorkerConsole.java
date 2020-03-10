package worker;

import java.io.*;
import java.math.BigInteger;
import java.util.*;


public class WorkerConsole {
	static PrintStream console = new PrintStream(System.out);
	static Scanner input = new Scanner(System.in);
	
	public static void main(String[] args) {
		runConsole(console, input);
	}
	
	public static void runConsole(PrintStream console, Scanner input) {
		String userIn;
		int choice;
		while (true) {
			console.println("Please choose from the following:\n" + "1.Start working\n" + "2.Exit");
			userIn = input.nextLine();
			try {
				choice = Integer.parseInt(userIn);
				if (choice == 2) {
					return;
				}
				else if (choice == 1) {
					break;
				}
				else {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				console.println("Error, please make a valid choice");
			}
				
		}
		
		PrimeSearch ps = new PrimeSearch(BigInteger.ZERO, BigInteger.TEN, BigInteger.TEN);
		ps.start();
		
		
	}

}
