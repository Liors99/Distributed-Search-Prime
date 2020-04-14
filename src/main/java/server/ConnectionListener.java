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
	
	private TaskScheduler ts;
	public ConnectionListener(WorkerDatabase wdb, int port, TaskScheduler ts) {
		this.wdb = wdb;
		this.port = port;
		this.ts=ts;
	}
	
	public ConnectionListener(WorkerDatabase wdb, int port) {
		this.wdb = wdb;
		this.port = port;
		
		this.ts=null;
	}

	public void run() {
		while (!killswitch) {
			try {
				serv = new ServerSocket(port);
				serv.setReuseAddress(true);
				System.out.println("Started listening on port "+port);
				sock = serv.accept();
				in = new DataInputStream(sock.getInputStream());
				out = new DataOutputStream(sock.getOutputStream());
				System.out.println("initiated connection with:" + sock.getInetAddress() + ":" + sock.getPort());
				WorkerConnection con = new WorkerConnection();
				int id = wdb.generateID();
				WorkerRecord rec = new WorkerRecord(sock.getInetAddress().toString(),sock.getPort(), id, 100, new Timestamp(System.currentTimeMillis()), con);
				wdb.addWorker(id, rec, con);
				
				if(ts!=null) {
					ts.addToWorkerQueue(rec);
				}
				
				
				//TODO: Replicate worker record to subscribers
				
				con.start();
				
				System.out.println("Sending: "+con.createHandshakeResponse());
				NetworkMessage.send(out, con.createHandshakeResponse());

				while(true) {
					try {
//						sendTask(con);
						break;
					} catch (Exception e) {
					}
				}
			
				
				sock.close();
				serv.close();
			} catch (SocketException e) {
				System.out.println("sock exception");

			} catch (IOException e) {
				System.out.println("io exception");

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
