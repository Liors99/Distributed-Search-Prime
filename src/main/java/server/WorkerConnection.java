package server;
import java.util.*;

import data.NetworkMessage;
import worker.Connection;

import java.io.*;
import java.net.*;

/**
 * A class to handle a connection from a server instance to a worker.
 * This is the server counterpart of {@link Connection}
 * @author Mark
 *
 */
public class WorkerConnection extends Thread {

	private int workerID;
	public ServerSocket servSock;
	public Socket sock;
	public DataOutputStream sockOut;
	public DataInputStream sockIn;
	public boolean ready = false;
	private boolean killswitch = false;
	private boolean isCoordinator = false;
	
	public WorkerConnection(boolean isCoordinator) throws IOException{
		
		servSock = new ServerSocket(0);
		this.isCoordinator = isCoordinator;

	}
	
	
	
	public void run() {
		try {
			sock = servSock.accept();
			sockOut = new DataOutputStream(sock.getOutputStream());
			sockIn = new DataInputStream(sock.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		ready = true;
		while(!killswitch) {
			
		}
		
	}
	
	/**
	 * Send a kill signal to the current connection
	 */
	public void kill() {
		killswitch = true;
		try {
			sockOut.close();
			sockIn.close();
			sock.close();
			servSock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a message to the worker on the other side of this connection
	 * 
	 * @param message the message to be sent
	 */
	public void sendMessage(String message) {
	
		while (!killswitch) {
			try {
				NetworkMessage.send(sockOut, message);
				System.out.println("Successfully sent "+message);
				break;
			}
			catch(Exception e) {
				
			}
		}
	}
	
	/**
	 * Receive a message from the worker on the other side of this connection
	 * 
	 * @return the received message, in string format
	 */
	public String receiveMessage() {
		String message=null;
		try {
			sock.setSoTimeout(5000);
			message = NetworkMessage.receive(sockIn);
		} catch (IOException e) {

		}
		return message;
	}
	
	
	/**
	 * Create an intial handshake message to the worker, with its assigned port
	 * @return
	 */
	public String createHandshakeResponse() {
		
		String message = "type:HandshakeResponse hostname:"+servSock.getInetAddress()+" port:"+servSock.getLocalPort();
		//Add a signature for the type of server on the initial connection
		if (isCoordinator) {
			message += " ServerType:coord";
		}
		else {
			message += " ServerType:sub";
		}
		return message;
	}
}
