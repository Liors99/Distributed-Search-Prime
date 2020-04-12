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
			DataOutputStream d=new DataOutputStream(o);
		    d.writeUTF("type:goal upper:"+CoordConsole.upperBound.toString()+" lower:"+CoordConsole.lowerBound.toString()+" limit:"+CoordConsole.primeLimit);
			//send long term
			Store.send(s);
			//send workers
			//Not sure where worker records will currently be stored
			String workers="type:workers: ";
			for (WorkerRecord i:CoordConsole.wr) {
				workers=workers+i.toString();
			}
			d.writeUTF(workers);
			//need to know data struct
			//Inform completed recovery
			d.writeUTF("type:rc");
			o.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
