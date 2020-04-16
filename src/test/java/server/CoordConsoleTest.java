package server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import server.CoordConsole;

class CoordConsoleTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testParseInput() {
		
		//Testing non-integer input exception
		Assertions.assertThrows(NumberFormatException.class,() ->{
			CoordConsole.parseInput("test");
		});
		
		//Testing negative integer input exception
		Assertions.assertThrows(NumberFormatException.class,() ->{
			CoordConsole.parseInput("-12");
		});
		
		//Testing scientific notation result
		assertEquals(600, CoordConsole.parseInput("6e2").intValue());
		
		//Testing integer format result
		assertEquals(1234, CoordConsole.parseInput("1234").intValue());
		
	}


}
