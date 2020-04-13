package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import data.NetworkMessage;
import exceptions.TimoutException;

public class ConnectionHandler implements Runnable{
	
	private Socket clientSocket = null;
	private DataInputStream in;
	private DataOutputStream out;
	private ServerNetwork server;
	private boolean isConnected;
	
	private boolean display = true;
	
	private int ka = 20;
	
	private long startTime;
    public ConnectionHandler(Socket clientSocket, ServerNetwork server) {
        this.clientSocket = clientSocket;
        this.server=server;
        isConnected=true;
        
        this.startTime = System.currentTimeMillis();
        
        
        try {
			in  = new DataInputStream (clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

    public void run() {
    	while(isConnected) {
    		try {
    			sendKA();
				receive();
			} catch (TimoutException | IOException e) {
				
				if(server.isServer(this.clientSocket.getInetAddress().toString(), this.clientSocket.getLocalPort())) {
					System.out.println("A server has disconnected");
				}
				this.server.removeFromMap(this.clientSocket.getLocalAddress(), this.clientSocket.getLocalPort());
				isConnected=false; //We dropped a connection if we get an error recieving
				
			}
    	}
    	
    }
    
    
    public void sendKA() throws IOException {
    	
    	int interval = 10;
    	
    	long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime)/1000;
		
		if ((int) duration> interval ) {
			NetworkMessage.send(out, "type:A");
			this.startTime = System.currentTimeMillis();
		}
    	
    }

	public void receive()  throws TimoutException{
    	try {
    		if(in.available()>0) {
    			
    			String next_msg = NetworkMessage.receive(in);
        		this.server.addToMessageQueue(next_msg);
        		
        		this.startTime = System.currentTimeMillis();
    		}
    		else {
    			long endTime = System.currentTimeMillis();
    			long duration = (endTime - startTime)/1000;
    			
    			if ((int) duration>ka && display) {
    				System.out.println("Got a timeout");
    				display=false;
    			}
    		}
		} catch (IOException e) {
			throw new TimoutException("A connection has dropped from " + this.clientSocket.getInetAddress() + "/ " + this.clientSocket.getLocalPort());
		}
    }
    
    
    public void close()  {
    	try {
			out.close();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

}
