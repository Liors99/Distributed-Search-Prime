package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import data.NetworkMessage;


public class ServerNetworkTest {


/*
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
	void testSend() throws IOException{
		int port = 9002;

		ServerNetwork server = new ServerNetwork("localhost", port);
		new Thread(server).start();

		Boolean isFailed= true;
		Socket client = new Socket("localhost", port);
		DataInputStream client_in = new DataInputStream(client.getInputStream());

		//Thread.sleep(4000);
		
		while(isFailed) {
			try {
				
				server.send("127.0.0.1", client.getLocalPort(), "Hello client!");
				
				System.out.println(NetworkMessage.receive(client_in));
				
				client.close();


		while(isFailed) {
			try {

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
	
	*/
	
	





	@Test
	void testConnect() throws Exception {

		int port = 9100;
		int port2= 9102;

		System.out.println("Started");
		ServerNetwork server = new ServerNetwork("localhost", port);
		new Thread(server).start();
		
		ServerNetwork server2= new ServerNetwork("localhost", port2);
		new Thread(server2).start();

		server2.startConnection("localhost", port, "localhost", port2+1);

		//Socket client = new Socket("localhost", port);

        server.printConnections();

        //client.close();
	}


	@Test
	void testCommunicate() throws Exception {

		int port = 9101;

		System.out.println("Started");
		ServerNetwork server = new ServerNetwork("127.0.0.1", port);
		ServerNetwork server2 = new ServerNetwork("127.0.0.1", port+1);
		new Thread(server).start();
		new Thread(server2).start();

		Socket out = server.startConnection("127.0.0.1", port+1, "127.0.0.1", port + 20);
		//Socket socket2 = new Socket(InetAddress.getByName("127.0.0.1"), port+1, InetAddress.getByName("127.0.0.1"), port+20);
		//Socket client = new Socket("localhost", port);

		//Thread.sleep(4000);
		//System.out.println("-----------------------");
        //server.printConnections();
		//System.out.println("-----------------------");

		boolean isFail = true;
		//while(isFail) {
			try {
				//System.out.println(out.getLocalPort());
				server.printConnections();
				//server2.printConnections();
				server.send("127.0.0.1", port+1, "test");
				//socket2.getOutputStream().write("test".getBytes());
				isFail = false;
			} catch(Exception e) {
				//e.printStackTrace();
			}
		//}
        System.out.println(server2.receiveNextMessage());


        server.stop();
        server2.stop();
        //client.close();
	}


}
