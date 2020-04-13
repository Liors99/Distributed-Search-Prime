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
	
	public Connection(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		
	}
	
	public void run() {
		connect();
		sendInitialHandshake();
		connect();
		try {
			String assignment = NetworkMessage.receive(sockIn);
			System.out.println(assignment);
		} catch (IOException e) {
			e.printStackTrace();
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
			NetworkMessage.send(sockOut, "type:WorkerHandshake");
			String response = NetworkMessage.receive(sockIn);
			System.out.println("Received:"+ response);
			Map<String,String> responseMap = MessageDecoder.createmap(response);
			sock.close();
			hostname = responseMap.get("address");
			port = Integer.parseInt(responseMap.get("port"));			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
