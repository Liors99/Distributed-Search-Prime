package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;

public class Accept implements Runnable {
	
	ServerSocket s;
	
	public Accept(ServerSocket s) {
		this.s=s;
		
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket ref=s.accept();
				String separator = "/";
				String ip=ref.getInetAddress().toString().split(Pattern.quote(separator))[1];
				if (CoordConsole.debug==true) {
					System.out.println("Received connection from "+ip);
				}
				Receive r=new Receive(ref, CoordConsole.send, CoordConsole.timeout);
				Thread thread = new Thread(r);
				thread.start();
						

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
