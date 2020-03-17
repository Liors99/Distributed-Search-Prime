//Refer questions to Jocelyn
//Implement a keep alive protocol
//Reference: https://www.programcreek.com/java-api-examples/?class=java.net.Socket&method=setKeepAlive
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

public class SendAlive implements Runnable {
	DataOutputStream out;
	int frequency;
	
	public SendAlive(DataOutputStream out, int freq) throws SocketException {
		this.out=out;
		this.frequency=freq;
		
	}

	@Override
	public void run() {
		while (true){
			try {
				TimeUnit.MILLISECONDS.sleep(frequency);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    try {
				out.writeBytes("Type:A");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
	}

}
