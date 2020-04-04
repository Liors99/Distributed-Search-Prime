package server;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;

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
	
	@Test
	void testNullWorker() {
		WorkerRecord w=new WorkerRecord();
		String res=w.toString();
		assertTrue(res.equals("Object:WorkerRecord{WID:0 score:0 isDone:null super:{null}}"));
		WorkerRecord y=new WorkerRecord(res);
		assertTrue(y.toString().equals(res));
	}

	@Test
	void testValidWorker() {
		WorkerRecord w=new WorkerRecord("10.0.0.1", 4, 5, 5, Timestamp.valueOf("2020-03-31 13:28:32.335"));
		String res=w.toString();
		assertTrue(res.equals("Object:WorkerRecord{WID:5 score:5 isDone:true super:Object:Record{IP:10.0.0.1 Port:4 timeout:2020-03-31 13:28:32.335}}"));
		WorkerRecord y=new WorkerRecord(res);
		assertTrue(y.toString().equals(res));
	}
	
	@Test
	void testSubRecord() {
		SubscriberRecord r=new SubscriberRecord("192.48.0.1", 420, java.sql.Timestamp.valueOf("2007-09-23 10:10:10.0"));
		String sample=r.toString();
		assertTrue(sample.equals("Object:SubscriberRecord{super:Object:Record{IP:192.48.0.1 Port:420 timeout:2007-09-23 10:10:10.0}}"));
	}
	
	
	@Test
	void testDeSubRecord() {
		SubscriberRecord r=new SubscriberRecord("192.48.0.1", 420, java.sql.Timestamp.valueOf("2007-09-23 10:10:10.0"));
		String sample=r.toString();
		//remove outer brackets
		String close=sample.split("\\{", 2)[1];
		int i = close.lastIndexOf("}");
		close=close.substring(0, i);
		SubscriberRecord r2=new SubscriberRecord(close);
		String sample2=r2.toString();
		assertTrue(sample.equals(sample2));
	}
}
