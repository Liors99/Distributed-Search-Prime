package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Timestamp;

import data.NetworkMessage;

public class ConnectionListener extends Thread{

	int port;
	ServerSocket serv;
	Socket sock;
	DataInputStream in;
	DataOutputStream out;
	WorkerDatabase wdb;
	private boolean killswitch = false;
	
	
	public ConnectionListener(WorkerDatabase wdb, int port) {
		this.wdb = wdb;
		this.port = port;
	}
	
	
	public void run() {
		while (!killswitch) {
			try {
				serv = new ServerSocket(port);
				serv.setReuseAddress(true);
				sock = serv.accept();
				in = new DataInputStream(sock.getInputStream());
				out = new DataOutputStream(sock.getOutputStream());
				System.out.println("initiated connection with:" + sock.getInetAddress() + ":" + sock.getPort());
				WorkerConnection con = new WorkerConnection();
				int id = wdb.generateID();
				WorkerRecord rec = new WorkerRecord(sock.getInetAddress().toString(),sock.getPort(), id, 100, new Timestamp(System.currentTimeMillis()));
				wdb.addWorker(id, rec, con);
				
				con.start();
				
				NetworkMessage.send(out, con.createHandshakeResponse());

				while(true) {
					try {
						sendTask(con);
						break;
					} catch (Exception e) {
					}
				}
			
				
				sock.close();
				serv.close();
			} catch (SocketException e) {

			} catch (IOException e) {

			}
		}
	}
	
	public void sendTask(WorkerConnection con) {
		con.sendMessage("type:handshake");
		con.sendMessage("lower:101 upper:201 tested:1098");
	}
	
	
	public void kill() {
		killswitch = true;
	}
	
}
