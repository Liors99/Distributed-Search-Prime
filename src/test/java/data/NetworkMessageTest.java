package data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

public class NetworkMessageTest {
	
	@Test
	void testToConvert() {
		String test = "123nkjasndkjasd";
		
		byte[] test_b= test.getBytes();
		byte[] length = ByteBuffer.allocate(4).putInt(test.length()).array();
		
		byte[] res = NetworkMessage.toNetworkMessage(test);
		
		//Test length prefix
		for(int i=0; i<4 ;i++) {
			assertEquals(length[i],res[i]);
		}
		
		//Test text
		for(int i=0; i<test_b.length; i++) {
			assertEquals(test_b[i],res[i+4]);
		}
	}
	
	/*
	@Test
	void testFromConvert() {
		String msg= "plzworknjasduhjagsuyd";
		byte[] msg_b= NetworkMessage.toNetworkMessage(msg);
		
		String result = NetworkMessage.fromNetworkMessage(msg_b);
		
		assertEquals(msg,result);
		
	}
	*/
	
	/*
	@Test 
	void testInvalidFrom() {
		String msg = "ajsdjasd";
		byte[] fake_length = ByteBuffer.allocate(4).putInt(560).array();
		
		byte[] msg_b = new byte[4+msg.length()];
		
		System.arraycopy(fake_length, 0, msg_b, 0, 4);
		System.arraycopy(msg.getBytes(), 0, msg_b, 4, msg.length());
		
		String result = NetworkMessage.fromNetworkMessage(msg_b);
		
		assertEquals("",result);
	}
	*/

}
