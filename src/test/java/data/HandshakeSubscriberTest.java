package data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HandshakeSubscriberTest {
	
	@Test
	void testToParse() {
		HandShakeSubscriber hss = new HandShakeSubscriber();
		hss.setKA(60);
		hss.setToken(12.01);
		
		String expected = "type:HSS testToParse: 60 12.01";
		
		String actual = hss.serializeHandShake("testToParse");
		
		assertEquals(expected, actual);
	}
	
	@Test
	void testFromParse() {
		HandShakeSubscriber hss = new HandShakeSubscriber();
		
		String actual = "type:HSS testFromParse: 30 10";
		hss.parseHandShake(actual);
		
		assertEquals(30, hss.getKA());
		assertEquals(10, hss.getToken());
	}
	
	@Test
	void testKA() {
		HandShakeSubscriber hss = new HandShakeSubscriber(60);
		
		assertEquals(60, hss.getKA());
	}

}
