package worker;
import java.io.*;
import java.net.*;
import java.util.*;

import data.MessageDecoder;
import data.NetworkMessage;

public class Connection extends Thread{

	public String hostname;
	public int port;
	public Socket sock;
	public DataInputStream sockIn;
	public DataOutputStream sockOut;
	private boolean killswitch = false;
	
	public Connection(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		
	}
	
	public void run() {
		connect();
		sendInitialHandshake();
		connect();
		while(!killswitch) {
			
		}
	}
	
	
	public void connect() {
		try {
			sock = new Socket(hostname, port);
			sockIn = new DataInputStream(sock.getInputStream());
			sockOut = new DataOutputStream(sock.getOutputStream());
			WorkerRunner.console.println("Successfully connected to "+sock.getInetAddress()+":"+sock.getPort());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void sendInitialHandshake() {
		try {
			Thread.sleep(2000);

			NetworkMessage.send(sockOut, "type:WorkerHandshake");
			String response = NetworkMessage.receive(sockIn);
			System.out.println("Received:"+ response);
			Map<String,String> responseMap = MessageDecoder.createmap(response);
			sock.close();
			port = Integer.parseInt(responseMap.get("port"));			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void kill() {
		try {
			sockIn.close();
			sockOut.close();
			sock.close();
			killswitch = true;
		}
		catch(Exception e) {
			
		}
	}
}
