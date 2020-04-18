package worker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

import data.BigInt;
import data.MessageDecoder;
import data.NetworkMessage;
import worker.Networking.ConnectionInfo;


/**
 * This class is the main logic of the worker class.
 * An instance of this class should be created and ran without additional modification
 * @author Mark
 *
 */
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
	
	/**
	 * Default constructor initiates the arrays
	 */
	public WorkerRunner() {
		hostnames = new String[Networking.NUMBER_OF_SERVERS];
		ports = new int[Networking.NUMBER_OF_SERVERS];
		connections = new Connection[Networking.NUMBER_OF_SERVERS];
		wasCoordinator = new boolean[Networking.NUMBER_OF_SERVERS];
	}
	
	
	public void run() {
		//Get input from the user
		runConsole(console, input);
		
		//Set up the server network structure as specified
		Networking network = new Networking();
		network.registerServers(hostnames, ports);
		for (int i = 0; i<Networking.NUMBER_OF_SERVERS;i++) {
			ConnectionInfo curConnection = network.getConnectionInfo(i);
			connections[i] = new Connection(curConnection.hostname, curConnection.port);
		}
		
		//Start all connection instances
		for (int i = 0; i<Networking.NUMBER_OF_SERVERS; i++) {
			connections[i].start();
		}

		//First run of find coordinator to avoid bugs on the very first coordinator determined by a handshake
		findCoordinator();
		
		while(!killswitch) {
			//If we signaled that the coordinator has changed, we need connect to the new coordinator
			if (coordinatorChanged) {
				Socket testSocket = null;
				//The loop continues until the socket is on and does not throw an exception trying to connect to it
				while(testSocket == null) {
					try {
						System.out.println("trying to connect to the new coordinator: "+connections[currentCoordinator].sock.getPort());
						Thread.sleep(2000); //Sleep to avoid blocking by a thrashing process
						testSocket = connections[currentCoordinator].sock; 
					}catch (Exception e) {
						
					}
				}
				//Reset boolean
				coordinatorChanged = false;
			}

			//A logical guard for the first run, until the initial coordinator is successfully connected
			if (currentCoordinator != -1) {
				
				doWork();
			}
			
		}
		
		
	}
	
	/**
	 * Used to receive a task from the current coordinator
	 * 
	 * @return the assigned task, in string format
	 */
	public String getTask() {
		String task = null;

		Socket coordSocket = null;
		
	
		//Continue trying until either the coordinator has changed, a killswitch is engaged, or a task is received
		while(!coordinatorChanged && !killswitch && task==null) {
			//Keep trying until coordinator changes or you manage to connect to the current one
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
				//If an exception has been caught at this point, it is very likely the coordinator has changed
				findCoordinator();
			}
		}
		return task;
		
	}
	
	
	/**
	 * Runs in a loop in the main method until it is killed. Is responsible for receiving tasks, completing them, and returning the results
	 */
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
		//Initialize the search thread and run it
		PrimeSearch ps = new PrimeSearch(new BigInteger(lower), new BigInteger(upper), new BigInteger(tested));
		ps.run();
		//Block until the search is finished
		while (ps.isAlive()) {
			
		}
		BigInt result = new BigInt(ps.result);
		String taskReport = "type:SearchResult tested:"+ps.subject+" divisor:"+result;
		//Send the result back to the coordinator
		sendResult(taskReport);
	}

	
	/**
	 * Sends the result of a search to the current coordinator
	 * 
	 * @param result the result of the search, in string format
	 */
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
	

	/**
	 * Finds the current coordinator
	 */
	public void findCoordinator() {
		System.out.println("current coordinator: "+currentCoordinator);
		try {
			Thread.sleep(1000);
		} catch (Exception e) {

		}
		for(int i = 0; i<Networking.NUMBER_OF_SERVERS; i++) {
				//If the index points to an instance that is marked as a coordinator but wasn't before, it's the new one
				if (connections[i].isCoordinator()) {
					if (wasCoordinator[i] == false) {
						//Notify that the coordinator changed and point to the new one
						currentCoordinator = i;
						coordinatorChanged = true;
						System.out.println("Coordinator changed: "+i);
						//Remove references to old coordinators
						for (int j = 0; j<Networking.NUMBER_OF_SERVERS; j++) {
							if (i != j) {
								connections[j].removeCoordinator();
								wasCoordinator[j] = false;
							}
						}
						//To differentiate from the next coordinator assigned
						wasCoordinator[i] = true;
					}
					
				}
		}
	}
		
	/**
	 * Runs the UI for the worker
	 * 
	 * @param console the output console
	 * @param input the input stream
	 */
	public void runConsole(PrintStream console, Scanner input) {
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
					hostnames = new String[Networking.NUMBER_OF_SERVERS];
					ports = new int[Networking.NUMBER_OF_SERVERS];
					
					for (int i = 0; i< Networking.NUMBER_OF_SERVERS; i++) {
						
						console.println("Please enter the hostname of server #"+i+": ");
//						hostnames[i] = input.nextLine();
						//Uncomment the above and comment below to go from automatic to manual entry
						hostnames[i] = "localhost";
						console.println("Please enter the port of server #"+i+": ");
//						ports[i] = Integer.parseInt(input.nextLine());
						//Uncomment the above and comment below to go from automatic to manual entry
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
