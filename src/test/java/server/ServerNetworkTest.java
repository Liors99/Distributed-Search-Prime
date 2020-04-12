package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import data.NetworkMessage;


public class ServerNetworkTest {
	
	
	@Test
	void testRecieve() throws UnknownHostException, IOException, InterruptedException {
		
		int port = 9001;
		
		ServerNetwork server = new ServerNetwork("localhost", port);
		new Thread(server).start();
		
		Socket client = new Socket("localhost", port);
		
		
		System.out.println("Just connected to " + client.getRemoteSocketAddress());
        OutputStream outToServer = client.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);
        
        NetworkMessage.send(out, "Hello");
        NetworkMessage.send(out, "Hello 2");
        NetworkMessage.send(out, "Hello 3");

        
        System.out.println(server.recieveNextMessage());
        System.out.println(server.recieveNextMessage());
        System.out.println(server.recieveNextMessage());
        server.printConnections();
        
        client.close();
	}
	
	
	
	
	@Test
	void testSend() throws InterruptedException{
		int port = 9002;
		
		ServerNetwork server = new ServerNetwork("localhost", port);
		new Thread(server).start();
		
		Boolean isFailed= true;
		
		//Thread.sleep(4000);
		
		while(isFailed) {
			try {
				Socket client = new Socket("localhost", port);
				
				DataInputStream client_in = new DataInputStream(client.getInputStream());
				server.send("127.0.0.1", client.getLocalPort(), "Hello client!");
				
				System.out.println(NetworkMessage.recieve(client_in));
				
				client.close();
				
				isFailed=false;
			}
			catch(Exception e){
				System.out.println("Failed sending");
			}
		}
		
		
		
		
	}
	
	@Test
	void testConnect() throws Exception {
		
		int port = 9001;
		
		System.out.println("Started");
		ServerNetwork server = new ServerNetwork("localhost", port);
		new Thread(server).start();
		
		server.startConnection("localhost", port);
		
		Socket client = new Socket("localhost", 9002);
		
        server.printConnections();
        
        client.close();
	}
	
	
	
}
