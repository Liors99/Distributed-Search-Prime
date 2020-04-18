package server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.Timestamp;

import org.junit.jupiter.api.Test;

class workerRecordTest {

	@Test
	void testSerialize() {
		WorkerConnection wc;
		try {
			wc = new WorkerConnection(false);
			
			String IP = "1.2.3.4";
			int Port = 50;
			int WID = 1;
			int score = 5;
			Timestamp timeout = new Timestamp(System.currentTimeMillis());
			WorkerRecord wr = new WorkerRecord( IP, Port, WID, score, timeout, wc);
			
			String serial = wr.toString();
			WorkerRecord wr1 = new WorkerRecord(serial);
			
			assertEquals(wr1.getIP(), IP);
			assertEquals(wr1.getPort(), Port);
			assertEquals(wr1.getWID(), WID);
			assertEquals(wr1.getScore(), score);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
	}
	
	@Test
	void workStuff() {
		try {
			WorkerConnection wc = new WorkerConnection(false);
			String IP = "1.2.3.4";
			int Port = 50;
			int WID = 1;
			int score = 5;
			Timestamp timeout = new Timestamp(System.currentTimeMillis());
			WorkerRecord wr = new WorkerRecord( IP, Port, WID, score, timeout, wc);
			wr.startWork();
			String serial = wr.toString();
			WorkerRecord wr1 = new WorkerRecord(serial);
			Timestamp ts = wr.getTimeout();
			
			Thread.sleep(1000);
			wr.stopWork();
			assertNotEquals(wr.getTimeout(), ts);
			
			
		}catch(Exception e) {}
	}

}
