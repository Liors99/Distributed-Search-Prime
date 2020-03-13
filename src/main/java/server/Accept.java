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
				for (int i =0; i<3; i++) {
					if (CoordConsole.Ips[i]==ip){
						CoordConsole.sockets[i]=ref;
						CoordConsole.status[i]="active";
						Recieve r=new Recieve(ref, CoordConsole.send, CoordConsole.timeout);
						r.run();
						
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
