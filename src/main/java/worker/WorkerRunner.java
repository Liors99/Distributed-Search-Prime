package worker;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import worker.Networking.ConnectionInfo;


public class WorkerRunner extends Thread{
	static PrintStream console = new PrintStream(System.out);
	static Scanner input = new Scanner(System.in);
	
	String[] hostnames;
	int[] ports;
	Connection[] connections;
	
	Networking network;
	
	public void run() {
		runConsole(console, input);
		Networking network = new Networking();
		network.registerServers(hostnames, ports);
		for (int i = 0; i<Networking.NUMBER_OF_SERVERS;i++) {
			ConnectionInfo curConnection = network.getConnectionInfo(i);
			connections[i] = new Connection(curConnection.hostname, curConnection.port);
		}
		
		for (int i = 0; i<Networking.NUMBER_OF_SERVERS; i++) {
			connections[i].run();
		}
		
	}
	
	
	public void runConsole(PrintStream console, Scanner input) {
		String userIn;
		int choice;
		//TODO @mark: add input checking
		while (true) {
			console.println("Please choose from the following:\n" + "1.Start working\n" + "2.Exit");
			userIn = input.nextLine();
			try {
				choice = Integer.parseInt(userIn);
				if (choice == 2) {
					return;
				}
				else if (choice == 1) {
					hostnames = new String[Networking.NUMBER_OF_SERVERS];
					ports = new int[Networking.NUMBER_OF_SERVERS];
					
					for (int i = 0; i< Networking.NUMBER_OF_SERVERS; i++) {
						console.println("Please enter the hostname of server #"+i+": ");
						hostnames[i] = input.nextLine();
						console.println("Please enter the port of server #"+i+": ");
						ports[i] = Integer.parseInt(input.nextLine());
					}
					console.println("All server info has been received");
					break;
				}
				else {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				console.println("Error, please make a valid choice");
			}
				
		}
		
		PrimeSearch ps = new PrimeSearch(new BigInteger("3"), new BigInteger("1000000000"), 
				new BigInteger("46843439956249365837687076705518861850009996536661"));
		ps.start();

		
	}

}
