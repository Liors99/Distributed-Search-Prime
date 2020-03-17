package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
				String ip=ref.getRemoteSocketAddress().toString();
				if (CoordConsole.debug==true) {
					System.out.println("Recieved connection from "+ip);
				}
				//for (int i =0; i<3; i++) {
					//this will not work on one computer
					//if (CoordConsole.Ips[i]==ip.split(":")[0]){
						//CoordConsole.status[i]="active";
						Recieve r=new Recieve(ref, CoordConsole.send, CoordConsole.timeout);
						Thread thread = new Thread(r);
						thread.start();
						
					//}
			//	}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
