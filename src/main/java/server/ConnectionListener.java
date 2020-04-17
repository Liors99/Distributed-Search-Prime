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
	private boolean isCoordinator = false;
	
	
	public int sampleWID = 0;
	public boolean ready = false;

	private TaskScheduler ts;
	public ConnectionListener(WorkerDatabase wdb, int port, TaskScheduler ts, boolean isCoord) {
		this.wdb = wdb;
		this.port = port;
		this.ts=ts;
		
		isCoordinator = isCoord;
	}

	/*
	public ConnectionListener(WorkerDatabase wdb, int port, boolean isCoord) {
		this.wdb = wdb;
		this.port = port;
		
		this.ts=null;

		isCoordinator = isCoord;

	}
	*/

	

	public WorkerDatabase getWdb() {
		return wdb;
	}

	public void setWdb(WorkerDatabase wdb) {
		this.wdb = wdb;
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
				WorkerConnection con = new WorkerConnection(isCoordinator);
				int id = wdb.generateID();
				WorkerRecord rec = new WorkerRecord(sock.getInetAddress().toString(),sock.getPort(), id, 100, new Timestamp(System.currentTimeMillis()), con);
				wdb.addWorker(id, rec, con);
				
				//Schedule immedietly if you are the coordinator and recieve connections midway
				if(this.isCoordinator) {
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
			
				sampleWID = id;
				System.out.println("Sample id: "+sampleWID);
				ready = true;
				sock.close();
				serv.close();
			} catch (SocketException e) {
//				System.out.println("sock exception");

			} catch (IOException e) {
//				System.out.println("io exception");

			}
		}
	}
	

	public TaskScheduler getTs() {
		return ts;
	}

	public void setTs(TaskScheduler ts) {
		this.ts = ts;
	}
	
	public boolean isCoordinator() {
		return isCoordinator;
	}

	public void setCoordinator(boolean isCoordinator) {
		this.isCoordinator = isCoordinator;
	}

	public void sendWorkerMessage(int wid, String message) {
		WorkerConnection con = wdb.workerConnections.get(wid);
		while (true) {
			try {
				NetworkMessage.send(con.sockOut, message);
				break;
			}
			catch(Exception e) {
				
			}
		}
		
	}
	
	
	public String receiveWorkerMessage(int wid) {
		WorkerConnection con = wdb.workerConnections.get(wid);
		String message = null;
		while (true) {
			try {
				message = NetworkMessage.receive(con.sockIn);
				break;
			}
			catch(Exception e) {
				
			}
		}
		return message;
		
	}
	
	public boolean isReady() {
		return ready;
	}
	
	
	public void kill() {
		killswitch = true;
	}
	
}
