package worker;
import java.io.*;
import java.net.*;

import data.NetworkMessage;

public class Connection extends Thread{

	private String hostname;
	private int port;
	private Socket sock;
	private DataInputStream sockIn;
	private DataOutputStream sockOut;
	
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
			sockIn = new DataInputStream(sock.getInputStream());
			sockOut = new DataOutputStream(sock.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void sendInitialHandshake() {
		try {
			NetworkMessage.send(sockOut, "type:WorkerHandshake");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
