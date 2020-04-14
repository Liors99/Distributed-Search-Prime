package server;

public class Subscriber {
	
	private int id;
	private int id_cor;
	private ServerNetwork server;
	
	public Subscriber(int id, int id_cor, ServerNetwork server) {
		this.id=id;
		this.id_cor=id_cor;
		this.server=server;
	}
	
	public void notMain(int listenerPort) {
		WorkerDatabase wdb = new WorkerDatabase();
		ConnectionListener listener = new ConnectionListener(wdb, listenerPort);
		listener.start();
	}
}
