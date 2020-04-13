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
}
