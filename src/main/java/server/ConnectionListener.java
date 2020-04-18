/**
* Thread class to listen for connections
*/

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
	
	
//	public int sampleWID = 0;
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

	
    /**
     * Worker database getter
     * @return database of current workers
     */
	public WorkerDatabase getWdb() {
		return wdb;
	}

    /**
     * Worker database setter
     * @param wdb the database to read the workers from 
     */
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

			

//				sampleWID = id;
//				System.out.println("Sample id: "+sampleWID);

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
	

    /**
     * Task scheduler getter
     */
	public TaskScheduler getTs() {
		return ts;
	}

    /**
     * Task scheduler setter
     * @param ts the task scehduler object 
     */
	public void setTs(TaskScheduler ts) {
		this.ts = ts;
	}

    /**
     * Check whether a given server is registered as a coordinator
     * @return true if given server is a coordinator, false otherwise 
     */	
	public boolean isCoordinator() {
		return isCoordinator;
	}

  	/**
     * Set the provided coordinator as coordinator 
     * @param isCoordinator - Whether the current server is a coordinator
     */
	public void setCoordinator(boolean isCoordinator) {
		this.isCoordinator = isCoordinator;
	}

    /**
     * Connect to specific worker registered in the database and send them the given message
     * @param wid - the worker's id
	 * @param message - the message to be sent to the specific worker  
     */	
	public void sendWorkerMessage(int wid, String message) {
		WorkerConnection con = wdb.workerConnections.get(wid);
		con.sendMessage(message);

	}

   	/**
     * Alert all workers of a coordinator takeover
     */	
	public void takeOverAsCoordinator() {
		for (int wid : wdb.workerConnections.keySet()) {
			sendWorkerMessage(wid, "type:CoordinatorTakeover");
		}
	}

  	/**
     * Announce that a given server has died 
     * @param id - The id of the server that has died 
     */	
	public void announceDisconnectedServer(int id) {
		System.out.println("Sending dead server signal");
		for (int wid : wdb.workerConnections.keySet()) {
			sendWorkerMessage(wid, "type:DeadServer DeadServerID:"+id);
		}
	}

  	/**
     * Recieve an incoming message from a worker
     * @param wid - The id of the worker that is communicated with
     */		
	public String receiveWorkerMessage(int wid) {
		WorkerConnection con = wdb.workerConnections.get(wid);
		return con.receiveMessage();
		
	}
	
	//check if ready 
	public boolean isReady() {
		return ready;
	}
	
	//kill switch method
	public void kill() {
		killswitch = true;
	}
	
}
