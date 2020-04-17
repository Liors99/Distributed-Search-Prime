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

		while (!killswitch) {
			try {
				connect();
				receiveInitialHandshake();				
				break;
			}
			catch (Exception e) {
				System.out.println("A");
			}
		}
		
		while (!killswitch) {
			try {
				connect();
				break;
			}
			catch (Exception e) {
				System.out.println("B");
				
			}
		}
		while(!killswitch) {
			if (!isCoordinator) {
				waitForSignal();

			}
			
			
		}
	}
	
	
	public void connect() {
		while (!killswitch) {
			try {
				sock = new Socket(hostname, port);
				sockIn = new DataInputStream(sock.getInputStream());
				sockOut = new DataOutputStream(sock.getOutputStream());
				WorkerRunner.console.println("Successfully connected to "+sock.getInetAddress()+":"+sock.getPort());
				break;
			}catch (Exception e) {
			}
		}
		
	}
	
	void receiveInitialHandshake() {
		while(!killswitch) {
			try {
				String response = NetworkMessage.receive(sockIn);
				System.out.println("Received:"+ response+" from "+ sock.getPort());
				Map<String,String> responseMap = MessageDecoder.createmap(response);
				sock.close();
				port = Integer.parseInt(responseMap.get("port"));
				if (responseMap.get("ServerType").equals("coord")) {
					isCoordinator = true;
				}
				break;
				
			} catch (Exception e) {
			}
		}
		
		
	}
	
	public boolean isCoordinator() {
		return isCoordinator;
	}
	
	public void removeCoordinator() {
		isCoordinator = false;
	}
	
	private void waitForSignal() {
		boolean signalReceived = false;
		
		while (!signalReceived && !killswitch) {
			try {
				Thread.sleep(5000);
				String signal = NetworkMessage.receive(sockIn);
				System.out.println("signal "+signal);
				Map<String, String> msgMap = MessageDecoder.createmap(signal);
				String sigType = msgMap.get("type");
				if (sigType.equals("CoordinatorTakeover")) {
					isCoordinator = true;
					signalReceived = true;
					System.out.println("Received new coordinator signal from "+ sock.getPort());
				}
				else if (sigType.equals("DeadServer")) {
					int id = Integer.parseInt(msgMap.get("DeadServerID"));
					System.out.println("Server #"+ id+ " has disconnected (con)");
					
				}
			}catch (Exception e) {
				
			}
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
