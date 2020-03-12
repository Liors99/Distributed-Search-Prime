//A class for receiving messages
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Recieve implements Runnable {
	
	private Socket s;
	private int timeout;
	private int send;
	private SendAlive c;
	private DataOutputStream out;
	private DataInputStream in;

	public Recieve(Socket s, int send, int timeout) throws IOException {
		//Add any required variables here
		this.s=s;
		this.send=send;
		this.timeout=timeout;
		this.s.setSoTimeout(timeout);
		out = new DataOutputStream(s.getOutputStream());
		in = new DataInputStream(s.getInputStream());
		c=new SendAlive(out,send);
		
		
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		long estimatedTime=0;
		while (estimatedTime<timeout) {
		    try {
				if (in.readUTF().isEmpty()) {
				 //Read something
				//todo message parsing
					startTime = System.currentTimeMillis();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		estimatedTime = System.currentTimeMillis() - startTime;
		}
	}

}
