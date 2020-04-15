package server;

public class Subscriber {
	
	private int id;
	private int id_cor;
	private ServerNetwork server;
	private static Store st;
	
	private WorkerDatabase wdb;

private ConnectionListener listener;
	public Subscriber(int id, int id_cor, ServerNetwork server, ConnectionListener listener, Store st) {

		this.id=id;
		this.id_cor=id_cor;
		this.server=server;
		

		this.listener=listener;


		this.st=st;

	}
	
	public void notMain(int listenerPort) {
		listener.start();
		//WorkerDatabase wdb = new WorkerDatabase();
		
		/*
		WorkerDatabase wdb = new WorkerDatabase();
		ConnectionListener listener = new ConnectionListener(wdb, listenerPort, null ,false);
		listener.start();
		*/
		
		//Read from coordinator
		
		while(true) {
			if(server.viewNextMessage()!=null) {
				System.out.println("MESSAGE RECIEVED BY SUBSCRIBER: "+server.receiveNextMessage());
			}
		}
	}
}
