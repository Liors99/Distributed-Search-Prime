package worker;
import java.io.*;
import java.net.*;
import java.util.*;

import data.MessageDecoder;
import data.NetworkMessage;
import server.WorkerConnection;

/**
 * This class is used as a thread between each worker and each server instance.
 * This is the worker counterpart of {@link WorkerConnection}
 * @author Mark
 *
 */
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
	
	/**
	 * The normal workflow for the run method is this:
	 * A worker connects to a specific listener port on a server instance.
	 * The worker receives a handshake with the permanent port information.
	 * The worker reconnects to the received port and starts working.
	 */
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
				
			}
		}
		while(!killswitch) {
			if (!isCoordinator) {
				waitForSignal();

			}
			
			
		}
	}
	
	
	/**
	 * Connects the worker to the specified server instance
	 */
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
	
	
	/**
	 * The worker waits for a response from the listener socket on the server.
	 * The response contains the new port number dedicated to the worker, which the worker should reconnect to.
	 * In the initial handshake, the coordinator announces it to the worker.
	 */
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
	
	
	/**
	 * Used to wait for either one of two signal types:
	 * 1. A coordinator takeover signal sent by a coordinator that wins an election
	 * 2. An announcement that one of the servers died
	 */
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
	
	
	/**
	 * Used to engage the killswitch on blocking functions
	 */
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
