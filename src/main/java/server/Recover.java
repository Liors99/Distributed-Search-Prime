package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class  Recover implements Runnable {
	
	int Servernum=-1;
	
	public void setup(int id) {
		
		Servernum=id;
		
	}

	@Override
	public void run() {
		Socket s=CoordConsole.sockets[Servernum];
		OutputStream o;
		try {
			o = s.getOutputStream();
			//send task 
			new DataOutputStream(o).writeUTF("type:goal upper:"+CoordConsole.upperBound.toString()+" lower:"+CoordConsole.lowerBound.toString()+" limit:"+CoordConsole.primeLimit);
			//send long term
			Store.send(s);
			//send workers
			//need to know data struct
			o.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
