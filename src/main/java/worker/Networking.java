package worker;
import java.net.*;

public class Networking {
	public static final int NUMBER_OF_SERVERS = 1;
	
	public class ConnectionInfo{
		String hostname;
		int port;
		
		public ConnectionInfo() {
			hostname = "localhost";
			port = 8080;
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
	
	
	public void registerServers(String[] hostnames, int[] ports) {
		for (int i = 0; i<NUMBER_OF_SERVERS; i++) {
			connections[i] = new ConnectionInfo(hostnames[i], ports[i]);			
		}
	}
	
	public ConnectionInfo getConnectionInfo(int index) {
		return connections[index];
	}
	
	
	

}
