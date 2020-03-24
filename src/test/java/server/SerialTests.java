package server;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import data.MessageDecoder;

public class SerialTests {
	@Test
	void testSerRecord() {
		Record r=new Record("192.48.0.1", 420, java.sql.Timestamp.valueOf("2007-09-23 10:10:10.0"));
		String sample=r.toString();
		assertTrue(sample.equals("Object:Record{IP:192.48.0.1 Port:420 timeout:2007-09-23 10:10:10.0}"));
	}
	
	@Test
	void testDeSerRecord() {
		Record r=new Record("192.48.0.1", 420, java.sql.Timestamp.valueOf("2007-09-23 10:10:10.0"));
		String sample=r.toString();
		//remove outer brackets
		String close=sample.split("\\{", 2)[1];
		int i = close.lastIndexOf("}");
		close=close.substring(0, i);
		Record r2=new Record(close);
		String sample2=r2.toString();
		assertTrue(sample.equals(sample2));
	}

}
