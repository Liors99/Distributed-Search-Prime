package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import data.BigInt;
import data.NetworkMessage;

public class Coordinator {
	
    int id=-2;
	private ServerNetwork server;
    public static List<ServerNetwork> ServerNetworkConnections;
    static BigInt lowerBound;
	static BigInt upperBound;
	static int primeLimit;
	static Store st;
	
	private WorkerDatabase wdb;
	private ConnectionListener listener;
    

    public Coordinator(int id, List<ServerNetwork> ServerNetworkConnections, ServerNetwork server, ConnectionListener listener, Store st) {


    	this.id=id;
    	Coordinator.ServerNetworkConnections=ServerNetworkConnections;
    	this.server=server;
    
    	this.listener=listener;
		this.st=st;
    	
    }
    
    
    public String getWorkerMessage(TaskScheduler ts) {

    	
    	return ts.getNextWorkerMessage();
    }
    
	/**
	 * Run as a coordinator 
	 */
	public void notMain(int listenerPort) {
		
		TaskScheduler ts = new TaskScheduler();
		listener.setTs(ts);
		listener.setCoordinator(true);
		listener.start();
		
		
		//Get user input
		CoordConsole.console();
		lowerBound=new BigInt(CoordConsole.lowerBound);
		upperBound=new BigInt(CoordConsole.upperBound);
		primeLimit= CoordConsole.primeLimit;
		String task="type:goal upper:"+upperBound.toString()+" lower:"+lowerBound.toString()+" limit:"+primeLimit;
		// Send tasks to other servers
		try {
			server.sendServers(task, id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		ts.setLower(lowerBound);
		ts.setUpper(upperBound);
		ts.setTarget(primeLimit);



		ts.setStore(st);
		//TaskScheduler ts = new TaskScheduler(lowerBound, upperBound, primeLimit);

		ts.start();
	
		
		WorkerDatabase wdb = new WorkerDatabase();

		

		while (!listener.isReady()) {}
		
		//Start getting messages
		
		
		while(true) {
			//Get message from workers
			String next_message=null;
			
			next_message = getWorkerMessage(ts);
			if(next_message!=null) {
				System.out.println("MESSAGE RECIEVED BY COORDINATOR : Got a message from worker: "+next_message);
				
				//Send the message to all subscribers
				
				try {
					server.sendServers(next_message, id);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			//Get message from subscribers
			if(server.viewNextMessage()!=null) {
				next_message = server.receiveNextMessage();
			}
		}
		
		
    }

	
}