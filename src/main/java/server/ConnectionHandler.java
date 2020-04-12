package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import data.NetworkMessage;

public class ConnectionHandler implements Runnable{
	
	private Socket clientSocket = null;
	private DataInputStream in;
	private DataOutputStream out;
	private ServerNetwork server;
	private boolean isConnected;

    public ConnectionHandler(Socket clientSocket, ServerNetwork server) {
        this.clientSocket = clientSocket;
        this.server=server;
        isConnected=true;
        
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
    		receive();
    	}
    	
    }
    

	public void receive()  {
    	try {
    		if(in.available()>0) {
    			
    			String next_msg = NetworkMessage.receive(in);
        		this.server.addToMessageQueue(next_msg);
    		}
    		
			//return next_msg;
		} catch (IOException e) {
			isConnected=false; //We dropped a connection if we get an error recieving
			e.printStackTrace();
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
