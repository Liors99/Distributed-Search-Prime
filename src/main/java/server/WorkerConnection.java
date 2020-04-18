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
	
	public String receiveMessage() {
		String message=null;
		try {
			sock.setSoTimeout(5000);
			message = NetworkMessage.receive(sockIn);
		} catch (IOException e) {

		}
		return message;
	}
	
	
	public String createHandshakeResponse() {
		
		String message = "type:HandshakeResponse hostname:"+servSock.getInetAddress()+" port:"+servSock.getLocalPort();
		if (isCoordinator) {
			message += " ServerType:coord";
		}
		else {
			message += " ServerType:sub";
		}
		return message;
	}
}
