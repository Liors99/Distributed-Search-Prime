package server;

import java.net.Socket;

public class Subscriber {
	
	private int id;
	private int id_cor;
	private ServerNetwork server;
	
	private WorkerDatabase wdb;
	public Subscriber(int id, int id_cor, ServerNetwork server, WorkerDatabase wdb) {
		this.id=id;
		this.id_cor=id_cor;
		this.server=server;
		
		this.wdb=wdb;
	}
	
	public void notMain(int listenerPort) {
		//WorkerDatabase wdb = new WorkerDatabase();

		WorkerDatabase wdb = new WorkerDatabase();
		ConnectionListener listener = new ConnectionListener(wdb, listenerPort, null ,false);
		listener.start();
		
		/* try to catch incoming messages forwarding results*/
			
		
	}
}
