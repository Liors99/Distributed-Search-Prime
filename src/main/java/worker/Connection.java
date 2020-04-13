package worker;
import java.io.*;
import java.net.*;
import java.util.*;

import data.MessageDecoder;
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
		sendInitialHandshake();
		connect();
	}
	
	
	public void connect() {
		try {
			sock = new Socket(hostname, port);
			sockIn = new DataInputStream(sock.getInputStream());
			sockOut = new DataOutputStream(sock.getOutputStream());
			WorkerRunner.console.print("Successfully connected to "+sock.getInetAddress()+":"+sock.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void sendInitialHandshake() {
		try {
			NetworkMessage.send(sockOut, "type:WorkerHandshake");
			String response = NetworkMessage.receive(sockIn);
			Map<String,String> responseMap = MessageDecoder.createmap(response);
			sock.close();
			sock = new Socket(responseMap.get("address"), Integer.parseInt(responseMap.get("port")));			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
