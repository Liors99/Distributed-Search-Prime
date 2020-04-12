package data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import data.HandShakeWorker;


class HandShakeWorkerTest {

	@Test
	void testAddSub() {
		int ttl=64;
		HandShakeWorker hsw = new HandShakeWorker(ttl);
		
		
		hsw.addSubscriber("123", "456");
		hsw.addSubscriber("678", "89");
		
		String[] serialized = hsw.serializeHandShake("testAddSub").split(" ");
		
		
		
		//Parse the serialization
		Assertions.assertEquals(Integer.toString(ttl), serialized[2]); //Test ttl value
		Assertions.assertEquals("123", serialized[3]); //test sub1, ip
		Assertions.assertEquals("456", serialized[4]); //test sub1, port
		Assertions.assertEquals("678", serialized[5]); //test sub2, ip
		Assertions.assertEquals("89", serialized[6]); //test sub2, port
	}
	
	@Test
	void testSubUnder() {
		int ttl=128;
		HandShakeWorker hsw = new HandShakeWorker(ttl);
		
		
		hsw.addSubscriber("123", "456");
		
		String[] serialized = hsw.serializeHandShake("testAddSub").split(" ");

		//Parse the serialization
		Assertions.assertEquals(Integer.toString(ttl), serialized[2]); //Test ttl value
		Assertions.assertEquals("123", serialized[3]); //test sub1, ip
		Assertions.assertEquals("456", serialized[4]); //test sub1, port
		Assertions.assertEquals("-1", serialized[5]); //test sub2, ip
		Assertions.assertEquals("-1", serialized[6]); //test sub2, port
	}
	
	@Test 
	void testSubOver() {
		int ttl=64;
		HandShakeWorker hsw = new HandShakeWorker(ttl);
		
		
		hsw.addSubscriber("123", "456");
		hsw.addSubscriber("678", "89");
		hsw.addSubscriber("over", "flow");
		
		String[] serialized = hsw.serializeHandShake("testAddSub").split(" ");
		
		
		
		//Parse the serialization
		Assertions.assertEquals(Integer.toString(ttl), serialized[2]); //Test ttl value
		Assertions.assertEquals("over", serialized[3]); //test sub1, ip
		Assertions.assertEquals("flow", serialized[4]); //test sub1, port
		Assertions.assertEquals("678", serialized[5]); //test sub2, ip
		Assertions.assertEquals("89", serialized[6]); //test sub2, port
	}
	
	@Test
	void testParsing() {
		
		String ser = "type:HSW testParse: 1024 123 456 678 89";
		HandShakeWorker hsw = new HandShakeWorker();
		hsw.parseHandShake(ser);
		
		String[] serialized = hsw.serializeHandShake("testParsing").split(" ");
		
		//Parse the serialization
		Assertions.assertEquals(Integer.toString(1024), serialized[2]); //Test ttl value
		Assertions.assertEquals("123", serialized[3]); //test sub1, ip
		Assertions.assertEquals("456", serialized[4]); //test sub1, port
		Assertions.assertEquals("678", serialized[5]); //test sub2, ip
		Assertions.assertEquals("89", serialized[6]); //test sub2, port
	}

}
