package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import data.NetworkMessage;


//Adapted from http://tutorials.jenkov.com/java-multithreaded-servers/multithreaded-server.html

public class ServerNetwork implements Runnable{
	private String ip; 
	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	private int port;
    private ServerSocket serverSocket;
    private boolean isStopped;
    private Thread runningThread;
    private Socket socket;

    private DataOutputStream out;

    private HashMap<String, Socket> client_to_socket;


    private BlockingQueue<String> MessageQueue = new LinkedBlockingDeque<String>(); //Message queue for all messages recieved from all threads

	public ServerNetwork(String ip, int port) {
		this.port=port;
		client_to_socket = new HashMap<>();
		this.ip = ip; 
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

	                String ip_port = removeSlash(in_ip.toString()+Integer.toString(in_port));
	                System.out.println("Got a connection from " + ip_port);
	                client_to_socket.put(ip_port,inSocket);

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
	 public synchronized void printConnections() {
		 for (String ip_port : client_to_socket.keySet()) {
				System.out.println(ip_port);
			}
	 }

	 private String removeSlash(String ip_port) {
		 StringBuilder s = new StringBuilder();
		 if(ip_port.charAt(0) == '/') {
			 for(int i = 1; i < ip_port.length(); i++) {
				 s.append(ip_port.charAt(i));
			 }
			 return s.toString();
		 } else {
			 return ip_port;
		 }
	 }

	 /**
	  * Sends a message to the specified IP and port number that corresponds to an entity, blocks until the message is sent
	  * @param IP
	  * @param port
	  * @param msg
	 * @throws Exception
	  */
	 public void send(String IP, int port, String msg) throws Exception {


		 String key = IP+Integer.toString(port);

		 //Check if IP/port combination is registered in this server
		 if(!client_to_socket.containsKey(key)) {
			 throw new Exception("The specified IP/port number combination is not registered in this server");
		 }

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
	  * Opens up a TCP connection
	  * @param IP
	  * @param port
	 * @throws Exception
	  */
	 public Socket startConnection(String IP, int port, String IP_from, int port_from) throws Exception {

		 Socket socket_server = new Socket(InetAddress.getByName(IP), port,InetAddress.getByName(IP_from), port_from);
		 if(socket_server.isConnected()) {
			 client_to_socket.put(IP.toString()+Integer.toString(port),socket_server);
			 return socket_server;
		 }
		 return null;

	 }

	 /**
	 * Gets the next message on the queue, blocks until a message appears on the queue
	 * @return
	 */
	public String receiveNextMessage() {
		while(MessageQueue.isEmpty()) {}

		return MessageQueue.poll();
	 }

	 /**
	 * Peek without pulling message to verify it is yours 
	 * @return
	 */
	public String peekNextMessage() {
		return MessageQueue.peek(); 
	}


	/**
	 * Adds the next message to queue
	 * @param msg - msg to be added
	 */
	public synchronized void addToMessageQueue(String msg) {
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

	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	public ServerSocket getServerSocket() {
		return serverSocket;
	}


	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}


	public Thread getRunningThread() {
		return runningThread;
	}


	public void setRunningThread(Thread runningThread) {
		this.runningThread = runningThread;
	}


	public Socket getSocket() {
		return socket;
	}


	public void setSocket(Socket socket) {
		this.socket = socket;
	}


	public DataOutputStream getOut() {
		return out;
	}


	public void setOut(DataOutputStream out) {
		this.out = out;
	}

  public boolean hasKey_client_to_socket(String s){
		return client_to_socket.containsKey(s);
	}

	public HashMap<String, Socket> getClient_to_socket() {
		return client_to_socket;
	}


	public void setClient_to_socket(HashMap<String, Socket> client_to_socket) {
		this.client_to_socket = client_to_socket;
	}


	public BlockingQueue<String> getMessageQueue() {
		return MessageQueue;
	}


	public void setMessageQueue(BlockingQueue<String> messageQueue) {
		MessageQueue = messageQueue;
	}


	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}



}
