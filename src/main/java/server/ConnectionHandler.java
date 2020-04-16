package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import data.MessageDecoder;
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
	private long startTime_ka;
	
    public ConnectionHandler(Socket clientSocket, ServerNetwork server) {
        this.clientSocket = clientSocket;
        this.server=server;
        isConnected=true;
        
        this.startTime = System.currentTimeMillis();
        this.startTime_ka = System.currentTimeMillis();
        
        try {
			in  = new DataInputStream (clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
        
    }

    public void run() {
    	while(isConnected) {
    		try {
    			//NetworkMessage.send(out, "test");
    			sendKA();
				receive();
			} catch (TimoutException e) {
				
				
				
				if(server.isServer(server.removeSlash(this.clientSocket.getInetAddress().toString()), this.clientSocket.getPort())) {
					System.out.println("Server "+ Integer.toString(this.clientSocket.getPort()) + "Has disconnected");
					
					//Check if coordinator or subscriber
					if(InitializeServerCluster.LeaderId!=-2) {
					   if(server.removeSlash(this.clientSocket.getInetAddress().toString()).equals(InitializeServerCluster.ips[InitializeServerCluster.LeaderId])) { //If the IP match
						for(int i=0; i< InitializeServerCluster.ports.length; i++) {
							if(this.clientSocket.getPort() == InitializeServerCluster.ports[InitializeServerCluster.LeaderId] + InitializeServerCluster.offset*(i+1) 
									|| this.clientSocket.getPort() == InitializeServerCluster.ports[InitializeServerCluster.LeaderId]) {
								System.out.println("A leader has crashed");
								InitializeServerCluster.isAlive[InitializeServerCluster.LeaderId]=false;
								try {
									InitializeServerCluster.reelection();
									InitializeServerCluster.reelectionStarted=true;
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								break;
							}
						}
					 }
					}
					else {
						for(int i=0; i<3 ;i++) {
							if(!(InitializeServerCluster.LeaderId == i) && !(InitializeServerCluster.id == i)) {
								InitializeServerCluster.isAlive[i]=false;
							}
						}
						System.out.println("A subscriber has crashed");
					}
				}
				
				isConnected=false; //We dropped a connection if we get an error recieving
				try {
					clientSocket.close();
				} catch (IOException e1) {
					
				}
				this.server.removeFromMap(this.clientSocket.getInetAddress().toString(), this.clientSocket.getLocalPort());
			
				
			}
    		
    		catch (Exception e) {
    			
    		}
    		
    	}
    	
    }
    
    
    public void sendKA() throws TimoutException {
    	
    	int interval = 10;
    	
    	long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime_ka)/1000;
		
		if ((int) duration> interval ) {
			//out.write("type:A".getBytes());
			
			int port = InitializeServerCluster.ports[0]+(clientSocket.getPort()%10);
			try {
				server.send(server.removeSlash(clientSocket.getInetAddress().toString()), port, "type:A");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new TimoutException("A connection has dropped from " + this.clientSocket.getInetAddress() + "/ " + this.clientSocket.getLocalPort());
			}
			this.startTime_ka = System.currentTimeMillis();
		}
    	
    }

	public void receive()  throws TimoutException{
    	try {
    		if(in.available()>0) {

    			String next_msg = NetworkMessage.receive(in);
        		
        		if(!MessageDecoder.findMessageType(next_msg).equals("A")) {
        			System.out.println("Got a message: "+ next_msg);
        			this.server.addToMessageQueue(next_msg);
        			
        		}
        		this.startTime = System.currentTimeMillis();
        		
        		
    		}
    		else {
    			long endTime = System.currentTimeMillis();
    			long duration = (endTime - startTime)/1000;
    			
    			if ((int) duration>ka && display) {
    				System.out.println("Got a timeout");
    				display=false;
    				throw new TimoutException("A connection has dropped from " + this.clientSocket.getInetAddress() + "/ " + this.clientSocket.getLocalPort());
    			}
    		}
		} catch (IOException e) {
			//throw new TimoutException("A connection has dropped from " + this.clientSocket.getInetAddress() + "/ " + this.clientSocket.getLocalPort());
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
