package worker;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class Connection extends Thread{

	private String hostname;
	private int port;
	private Socket sock;
	private InputStream sockIn;
	private OutputStream sockOut;
	
	public Connection(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		
	}
	
	public void run() {
		connect();
	}
	
	
	public void connect() {
		try {
			sock = new Socket(hostname, port);
			sockIn = sock.getInputStream();
			sockOut = sock.getOutputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
