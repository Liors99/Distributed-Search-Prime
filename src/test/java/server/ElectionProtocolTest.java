package server;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import server.ElectionProtocol;

public class ElectionProtocolTest {
	
	@Test
	void testValid() {
		Assertions.assertTrue(ElectionProtocol.isHigher(2.0,1.0));	
	}

}
