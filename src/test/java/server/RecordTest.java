package server;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;

class RecordTest {

	@Test
	void testConstructor() {
		long t = System.currentTimeMillis();
		Record R = new Record("1.2.3.4", 50,new Timestamp(t));
		assertEquals("1.2.3.4", R.getIP());
		assertEquals(50, R.getPort());
		assertEquals(new Timestamp(t), R.getTimeout());
		
	}
	
	
	@Test
	void testSerialize() {
		long t = System.currentTimeMillis();
		Record R = new Record("1.2.3.4", 50,new Timestamp(t));
		String s = R.toString();
		
		Record R1 = new Record(s);
		
		assertEquals(R.getIP(), R1.getIP());
		assertEquals(R.getPort(), R1.getPort());
		assertEquals(R.getTimeout(), R1.getTimeout());
	}

}
