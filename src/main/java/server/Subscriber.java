package server;

public class Subscriber {
	
	private int id;
	private int id_cor;
	private ServerNetwork server;
	private static Store st;
	
	private WorkerDatabase wdb;
	public Subscriber(int id, int id_cor, ServerNetwork server, WorkerDatabase wdb, Store st) {
		this.id=id;
		this.id_cor=id_cor;
		this.server=server;
		
		this.wdb=wdb;
		this.st=st;
	}
	
	public void notMain(int listenerPort, ConnectionListener listener) {
		listener.start();
		//WorkerDatabase wdb = new WorkerDatabase();
		
		/*
		WorkerDatabase wdb = new WorkerDatabase();
		ConnectionListener listener = new ConnectionListener(wdb, listenerPort, null ,false);
		listener.start();
		*/
	}
}
