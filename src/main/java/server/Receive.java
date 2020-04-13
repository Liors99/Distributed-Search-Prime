//A class for receiving messages
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import data.MessageDecoder;

public class Receive implements Runnable {
	
	Socket s;
	private int timeout;
	private int send;
	private SendAlive c;
	private DataOutputStream out;
	private DataInputStream in;

	public Receive(Socket s, int send, int timeout) throws IOException {
		//Add any required variables here
		this.s=s;
		this.send=send;
		this.timeout=timeout;
		this.s.setSoTimeout(timeout);
		out = new DataOutputStream(s.getOutputStream());
		in = new DataInputStream(s.getInputStream());
		c=new SendAlive(out,send);
		out.writeUTF("type:id id:"+CoordConsole.id+" status:"+CoordConsole.status[CoordConsole.id]);
		
		
	}

	@Override
	public void run() {
		Thread thread = new Thread(c);
		thread.start();
		long startTime = System.currentTimeMillis();
		long estimatedTime=0;
		String temp;
		while (estimatedTime<timeout || !CoordConsole.status[CoordConsole.id].equals("active")) {
		    try {
		    	temp=in.readUTF();
				 //Read something
				 MessageDecoder.parse(temp);
				startTime = System.currentTimeMillis();
				
			} catch (IOException e) {
				
			}

		estimatedTime = System.currentTimeMillis() - startTime;
		} // out of loop
		//Someone timed out
		System.out.println("Timeout detected");
		//Shutdown sender
		c.run=false;
	}

}
