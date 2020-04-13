package server;
import java.util.*;

import data.NetworkMessage;

import java.io.*;
import java.net.*;

public class WorkerConnection extends Thread {

	private int workerID;
	public ServerSocket servSock;
	public Socket sock;
	public DataOutputStream sockOut;
	public DataInputStream sockIn;
	private boolean killswitch = false;
	
	public WorkerConnection() throws IOException{
		
		servSock = new ServerSocket(0);

	}
	
	
	
	public void run() {
		try {
			sock = servSock.accept();
			sockOut = new DataOutputStream(sock.getOutputStream());
			sockIn = new DataInputStream(sock.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
		try {
			NetworkMessage.send(sockOut, message);
		} catch (IOException e) {

		}
	}
	
	public String receiveMessage() {
		String message=null;
		try {
			message = NetworkMessage.receive(sockIn);
		} catch (IOException e) {

		}
		return message;
	}
	
	
	public String createHandshakeResponse() {
		
		return "type:HandshakeResponse hostname:"+servSock.getInetAddress()+" port:"+servSock.getLocalPort();
	}
}
