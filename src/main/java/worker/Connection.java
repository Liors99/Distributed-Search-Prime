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
	private boolean isCoordinator = false;
	
	public Connection(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		
	}
	
	public void run() {

		while (true) {
			try {
				connect();
				sendInitialHandshake();				
				break;
			}
			catch (Exception e) {
				System.out.println("A");
			}
		}
		
		while (true) {
			try {
				connect();
				break;
			}
			catch (Exception e) {
				System.out.println("B");
				
			}
		}
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
		while(true) {
			try {
//				NetworkMessage.send(sockOut, "type:WorkerHandshake");
				String response = NetworkMessage.receive(sockIn);
				System.out.println("Received:"+ response);
				Map<String,String> responseMap = MessageDecoder.createmap(response);
				sock.close();
				port = Integer.parseInt(responseMap.get("port"));
				if (responseMap.get("ServerType").equals("coord")) {
					isCoordinator = true;
				}
				break;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	public boolean isCoordinator() {
		return isCoordinator;
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
