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
	int currentCoordinator = -1;
	private boolean killswitch = false;
	private boolean coordinatorChanged = false;
	
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
		findCoordinator();
		
		while(!killswitch) {
			if (coordinatorChanged) {
				Socket testSocket = null;
				while(testSocket == null) {
					try {
						System.out.println("trying to connect to the new coordinator: "+connections[currentCoordinator].sock.getPort());
						Thread.sleep(2000);
						testSocket = connections[currentCoordinator].sock;
					}catch (Exception e) {
						
					}
				}
				coordinatorChanged = false;
			}

			if (currentCoordinator != -1) {
				
				doWork();
			}
			
		}
		
		
	}
	
	
	public String getTask() {
		String task = null;

		Socket coordSocket = null;
		
	
		while(!coordinatorChanged && !killswitch && task==null) {
			while (!coordinatorChanged && coordSocket == null) {
				try {
					System.out.println("trying to connect to "+connections[currentCoordinator].sock.getPort());
					Thread.sleep(2000);
					coordSocket = connections[currentCoordinator].sock;
				}catch (Exception e) {
					
				}
			}
			
			try {
				coordSocket.setSoTimeout(5000);
				System.out.println("I am waiting for a task from server #"+currentCoordinator+": "+coordSocket.getPort());
				Thread.sleep(2000);
				task = NetworkMessage.receive(new DataInputStream(coordSocket.getInputStream()));
			} catch (Exception e) {
				findCoordinator();
			}
		}
		return task;
		
	}
	
	public void doWork() {
		String task = getTask();
		Map<String, String> messageMap =null;
		if (task != null) {
			messageMap = MessageDecoder.createmap(task);
			if (messageMap.get("type").equals("DeadServer")) {
				processDeadServerMessage(messageMap);
				return;
			}
		}
		if (coordinatorChanged) {
			return;
		}
		String lower = messageMap.get("lower");
		String upper = messageMap.get("upper");
		String tested = messageMap.get("tested");
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
	
	
	private void processDeadServerMessage(Map<String,String> map) {
		
		int id = Integer.parseInt(map.get("DeadServerID"));
		System.out.println("Server #"+ id+ " has disconnected");
	}
	
	
	public void findCoordinator() {
		System.out.println("current coordinator: "+currentCoordinator);
		try {
			Thread.sleep(1000);
		} catch (Exception e) {

		}
		for(int i = 0; i<Networking.NUMBER_OF_SERVERS; i++) {
				if (connections[i].isCoordinator()) {
					//System.out.println("wasCoordinator["+i+"] = " + wasCoordinator[i]);
					if (wasCoordinator[i] == false) {
						currentCoordinator = i;
						coordinatorChanged = true;
						System.out.println("Coordinator changed: "+i);
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
