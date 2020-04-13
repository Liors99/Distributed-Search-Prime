package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;

public class Coordinator {
	
    int id=-2;
	private ServerNetwork server;
    public static List<ServerNetwork> ServerNetworkConnections;
    static BigInteger lowerBound;
	static BigInteger upperBound;
	static int primeLimit;
    
    public Coordinator(int id, List<ServerNetwork> ServerNetworkConnections, ServerNetwork server) {
    	this.id=id;
    	Coordinator.ServerNetworkConnections=ServerNetworkConnections;
    	this.server=server;
    }
	/**
	 * Run as a coordinator 
	 */
	public void notMain() {
		//Get user input
		CoordConsole.console();
		lowerBound=CoordConsole.lowerBound;
		upperBound=CoordConsole.upperBound;
		primeLimit=CoordConsole.primeLimit;
		String task="type:goal upper:"+upperBound.toString()+" lower:"+lowerBound.toString()+" limit:"+primeLimit;
		// Send tasks to other servers
		try {
			server.sendServers(task, id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

	
}