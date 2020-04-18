package worker;
import java.net.*;

/**
 * Class to handle the connection information of servers
 * @author Mark
 *
 */
public class Networking {
	//This has only been tested with 3 servers, this is the number of assumed instances
	public static final int NUMBER_OF_SERVERS = 3;
	
	/**
	 * A simple class to aggregate hostname-port pairs
	 * @author Mark
	 *
	 */
	public class ConnectionInfo{
		String hostname;
		int port;
		
		public ConnectionInfo() {

		}
		
		public ConnectionInfo(String hostname, int port) {
			this.hostname = hostname;
			this.port = port;
		}
	}
	
	ConnectionInfo[] connections;
	
	public Networking() {
		connections = new ConnectionInfo[NUMBER_OF_SERVERS];
		for (int i = 0; i<NUMBER_OF_SERVERS; i++) {
			connections[i] = new ConnectionInfo();
		}
		
	}
	
	/**
	 * Translates the received hostnames and ports into instances of {@link ConnectionInfo}
	 * @param hostnames the list of hostnames
	 * @param ports the list of ports with indexes corresponding to the hostname array
	 */
	public void registerServers(String[] hostnames, int[] ports) {
		for (int i = 0; i<NUMBER_OF_SERVERS; i++) {
			connections[i] = new ConnectionInfo(hostnames[i], ports[i]);			
		}
	}
	
	/**
	 * Fetches the {@link ConnectionInfo} for the specified index
	 * @param index the requested index
	 * @return {@link ConnectionInfo} of the specified server ID
	 */
	public ConnectionInfo getConnectionInfo(int index) {
		return connections[index];
	}
	
	
	

}
