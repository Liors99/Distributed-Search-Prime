package data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class HandshakeSubscriberTest {
	
	@Test
	void testToParse() {
		HandShakeSubscriber hss = new HandShakeSubscriber(0, 60, 12.01);
		/*
		hss.setKA(60);
		hss.setToken(12.01);
		*/
		String expected = "type:HSS server:0 ka:60 token:12.01";
		
		String actual = hss.serializeHandShake();
		
		assertEquals(expected, actual);
	}
	
	@Test
	void testFromParse() {
		HandShakeSubscriber hss = new HandShakeSubscriber(1, 30);
		hss.setToken(10);
		
		String actual = "type:HSS server:1 ka:30 token:10";
		hss.parseHandShake(actual);
		
		assertEquals(30, hss.getKA());
		assertEquals(10, hss.getToken());
		assertEquals(1, hss.getID());
	}
	
	
	@Test
	void testType() {
		HandShakeSubscriber Hs = new HandShakeSubscriber(1, 10, 100000);
        String serializedToken = Hs.serializeHandShake();
        
        assertTrue(MessageDecoder.findMessageType(serializedToken).contentEquals("HSS"));
	}

}
