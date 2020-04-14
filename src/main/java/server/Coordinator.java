package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;

import data.BigInt;

public class Coordinator {
	
    int id=-2;
	private ServerNetwork server;
    public static List<ServerNetwork> ServerNetworkConnections;
    static BigInt lowerBound;
	static BigInt upperBound;
	static int primeLimit;
	
	private WorkerDatabase wdb;
    
    public Coordinator(int id, List<ServerNetwork> ServerNetworkConnections, ServerNetwork server, WorkerDatabase wdb) {
    	this.id=id;
    	Coordinator.ServerNetworkConnections=ServerNetworkConnections;
    	this.server=server;
    	
    	this.wdb=wdb;
    	
    	
    }
    
	/**
	 * Run as a coordinator 
	 */
	public void notMain(int listenerPort) {
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

		
		TaskScheduler ts = new TaskScheduler(lowerBound, upperBound);
		ts.start();

		WorkerDatabase wdb = new WorkerDatabase();
		ConnectionListener listener = new ConnectionListener(wdb, listenerPort, ts);
		listener.start();

		
		
    }

	
}