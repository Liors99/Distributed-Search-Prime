package worker;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.*;

import data.MessageDecoder;
import data.NetworkMessage;
import worker.Networking.ConnectionInfo;


public class WorkerRunner extends Thread{
	static PrintStream console = new PrintStream(System.out);
	static Scanner input = new Scanner(System.in);
	
	String[] hostnames;
	int[] ports;
	Connection[] connections;
	int currentCoordinator;
	private boolean killswitch = false;
	
	Networking network;
	
	public WorkerRunner() {
		hostnames = new String[Networking.NUMBER_OF_SERVERS];
		ports = new int[Networking.NUMBER_OF_SERVERS];
		connections = new Connection[Networking.NUMBER_OF_SERVERS];
	}
	
	public void run() {
		runConsole(console, input);
		Networking network = new Networking();
		network.registerServers(hostnames, ports);
		for (int i = 0; i<Networking.NUMBER_OF_SERVERS;i++) {
			ConnectionInfo curConnection = network.getConnectionInfo(i);
			connections[i] = new Connection(curConnection.hostname, curConnection.port);
		}
		
		
		for (int i = 0; i<Networking.NUMBER_OF_SERVERS; i++) {
			connections[i].start();
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		findCoordinator();
		String task = getTask();
		Map<String, String> assignMap = MessageDecoder.createmap(task);
		String lower = assignMap.get("lower");
		String upper = assignMap.get("upper");
		String tested = assignMap.get("tested");
		System.out.println("I got assigned: lower:"+lower+" upper:"+upper+" tested number:"+ tested);
		PrimeSearch ps = new PrimeSearch(new BigInteger(lower), new BigInteger(upper), new BigInteger(tested));
		ps.run();
	}
	
	
	public String getTask() {
		String task = null;
		Socket coordSocket = connections[currentCoordinator].sock;
		while(!killswitch && task==null) {
			try {
				coordSocket.setSoTimeout(5000);
				task = NetworkMessage.receive(new DataInputStream(coordSocket.getInputStream()));
			} catch (Exception e) {

			}
		}
		return task;
		
	}
	
	public void findCoordinator() {
		String handshake = null;
		for(int i = 0; i<Networking.NUMBER_OF_SERVERS; i++) {
			try {
//				connections[i].sock.setSoTimeout(5000);
				handshake = NetworkMessage.receive(connections[i].sockIn);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (handshake != null) {
				System.out.println("Received handshake:"+handshake);
				currentCoordinator = i;
				break;
			}
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
		
		
	}
	
	public void kill() {
		killswitch = true;
	}

}
