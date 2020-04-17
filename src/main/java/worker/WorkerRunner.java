package worker;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.*;

import data.BigInt;
import data.MessageDecoder;
import data.NetworkMessage;
import worker.Networking.ConnectionInfo;


public class WorkerRunner extends Thread{
	static PrintStream console = new PrintStream(System.out);
	static Scanner input = new Scanner(System.in);
	
	String[] hostnames;
	int[] ports;
	Connection[] connections;
	private boolean[] wasCoordinator;
	int currentCoordinator;
	private boolean killswitch = false;
	
	Networking network;
	
	public WorkerRunner() {
		hostnames = new String[Networking.NUMBER_OF_SERVERS];
		ports = new int[Networking.NUMBER_OF_SERVERS];
		connections = new Connection[Networking.NUMBER_OF_SERVERS];
		wasCoordinator = new boolean[Networking.NUMBER_OF_SERVERS];
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
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		
		while(!killswitch) {
			findCoordinator();
			doWork();
		}
		
		
	}
	
	
	public String getTask() {
		String task = null;
		Socket coordSocket = connections[currentCoordinator].sock;
		while (coordSocket == null) {
			try {
				Thread.sleep(2000);
				coordSocket = connections[currentCoordinator].sock;
			}catch (Exception e) {
				
			}
		}
		System.out.println("no longer null");
		while(!killswitch && task==null) {
			try {
				coordSocket.setSoTimeout(5000);
				task = NetworkMessage.receive(new DataInputStream(coordSocket.getInputStream()));
			} catch (Exception e) {
				System.out.println("timed out");
			}
		}
		return task;
		
	}
	
	public void doWork() {
		String task = getTask();
		Map<String, String> assignMap = MessageDecoder.createmap(task);
		String lower = assignMap.get("lower");
		String upper = assignMap.get("upper");
		String tested = assignMap.get("tested");
		System.out.println("I got assigned: lower:"+lower+" upper:"+upper+" tested number:"+ tested);
		PrimeSearch ps = new PrimeSearch(new BigInteger(lower), new BigInteger(upper), new BigInteger(tested));
		ps.run();
		while (ps.isAlive()) {
			
		}
		BigInt result = new BigInt(ps.result);
		String taskReport = "type:SearchResult tested:"+ps.subject+" divisor:"+result;
		sendResult(taskReport);
	}
	
	public void sendResult(String result) {
		Socket coordSocket = connections[currentCoordinator].sock;
		DataOutputStream coordDataStream = null;

		boolean resultSent = false;

		while(!killswitch && !resultSent) {
			try {
				coordDataStream = new DataOutputStream (connections[currentCoordinator].sock.getOutputStream());

				coordSocket.setSoTimeout(5000);
				NetworkMessage.send(coordDataStream, result);
				resultSent = true;
			} catch (Exception e) {

			}
		}
	}
	
	public void findCoordinator() {
		for(int i = 0; i<Networking.NUMBER_OF_SERVERS; i++) {
				if (connections[i].isCoordinator()) {
					if (wasCoordinator[i] == false) {
						currentCoordinator = i;
						for (int j = 0; j<Networking.NUMBER_OF_SERVERS; j++) {
							if (i != j) {
								connections[j].removeCoordinator();
								wasCoordinator[j] = false;
							}
						}
						wasCoordinator[i] = true;
					}
					
				}
		}
	}
	
	public void runConsole(PrintStream console, Scanner input) {
		String userIn;
		int choice;
		//TODO @mark: add input checking
		//TODO @mark: remove hardcoded servers
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
//						hostnames[i] = input.nextLine();
						hostnames[i] = "localhost";
						console.println("Please enter the port of server #"+i+": ");
//						ports[i] = Integer.parseInt(input.nextLine());
						ports[i] = 8000+i;
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
