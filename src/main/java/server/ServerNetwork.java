package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import data.NetworkMessage;


//Adapted from http://tutorials.jenkov.com/java-multithreaded-servers/multithreaded-server.html

public class ServerNetwork implements Runnable{
	
 	private int port;
    private ServerSocket serverSocket;
    private boolean isStopped;
    private Thread runningThread;
    
    private DataOutputStream out;
    
    private HashMap<String, Socket> client_to_socket;
    
    private BlockingQueue<String> MessageQueue = new LinkedBlockingDeque<String>(); //Message queue for all messages recieved from all threads
	
	public ServerNetwork(String ip, int port) {
		this.port=port;
		client_to_socket = new HashMap<>();
	}
	
	
	 public void run(){
	        synchronized(this){
	            this.runningThread = Thread.currentThread();
	        }
	        openServerSocket();
	        while(!isStopped()){
	            Socket inSocket = null;
	            try {
	            	inSocket = this.serverSocket.accept();
	            	
	            	InetAddress in_ip=inSocket.getInetAddress();
	                int in_port=inSocket.getPort();
	                
	                System.out.println("Got a connection from " +in_ip.toString()+Integer.toString(in_port));
	                client_to_socket.put(in_ip.toString()+Integer.toString(in_port),inSocket);

	            } catch (IOException e) {
	                if(isStopped()) {
	                    System.out.println("Server Stopped.") ;
	                    return;
	                }
	                throw new RuntimeException("Error accepting client connection", e);
	            }
	            

	            new Thread(new ConnectionHandler(inSocket, this)).start();
	            
	            
	            
	        }
	        System.out.println("Server Stopped.") ;
	    }


	 /**
	  * Prints all the connections that this server has (IP + port number)
	  */
	 public void printConnections() {
		 for (String ip_port : client_to_socket.keySet()) {
				System.out.println(ip_port);
			}
	 }
	 
	 
	 /**
	  * Sends a message to the specified IP and port number that corresponds to an entity, blocks until the message is sent
	  * @param IP
	  * @param port
	  * @param msg
	  */
	 public void send(String IP, int port, String msg) {
		 
		 
		 String key = "/"+IP+Integer.toString(port);
		 while(!client_to_socket.containsKey(key)) {}
		 
		 //The key has been found
		 Socket target = client_to_socket.get(key);
		 try {
			out = new DataOutputStream(target.getOutputStream());
			NetworkMessage.send(out, msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
			 
		 
		 
	 }
	 
	 /**
	 * Gets the next message on the queue, blocks until a message appears on the queue
	 * @return
	 */
	public String recieveNextMessage() {
		while(MessageQueue.isEmpty()) {}
		
		return MessageQueue.poll();
	 }
	 
	 
	public void addToMessageQueue(String msg) {
		MessageQueue.add(msg);
	 }
	 
	
	 
	private synchronized boolean isStopped() {
	    return this.isStopped;
	}
	
	public synchronized void stop(){
	    this.isStopped = true;
	    try {
	        this.serverSocket.close();
	    } catch (IOException e) {
	        throw new RuntimeException("Error closing server", e);
	    }
	}
	
	private void openServerSocket() {
	    try {
	        this.serverSocket = new ServerSocket(this.port);
	    } catch (IOException e) {
	        throw new RuntimeException("Cannot open port 8080", e);
	    }
	}
	    
	    
	
}