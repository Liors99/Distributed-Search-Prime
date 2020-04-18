package data;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BigIntTest {
	BigInt testSubject;

	@BeforeEach
	void setUp() throws Exception {
		//Initializing to zero before each test
		testSubject = new BigInt("0");
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testToBigInteger() {
		assertDoesNotThrow(() -> {
			testSubject.toBigInteger();
		});
		assertTrue(testSubject.toBigInteger().getClass().equals(BigInteger.class));
	}
	
	@Test
	void testIsEven() {
		//Zero is even
		assertTrue(testSubject.isEven());
		
		testSubject = new BigInt(Integer.toString(Integer.MAX_VALUE-1));
		assertTrue(testSubject.isEven());
		
		testSubject = new BigInt(Integer.toString(Integer.MAX_VALUE));
		assertFalse(testSubject.isEven());
		
		testSubject = new BigInt(Integer.toString(Integer.MIN_VALUE));
		assertTrue(testSubject.isEven());
		
		testSubject = new BigInt(Integer.toString(Integer.MIN_VALUE+1));
		assertFalse(testSubject.isEven());
	}
	

	@Test
	void testIsZero() {
		assertTrue(testSubject.isZero());
		testSubject = new BigInt("1");
		assertFalse(testSubject.isZero());
	}

	@Test
	void testGt() {
		BigInt comparison = new BigInt("10");
		assertTrue(comparison.gt(testSubject));
	}

	@Test
	void testGe() {
		BigInt comparison = new BigInt("0");
		assertTrue(comparison.ge(testSubject));
		
		comparison = new BigInt("10");
		assertTrue(comparison.ge(testSubject));
	}

	@Test
	void testLt() {
		BigInt comparison = new BigInt("-10");
		assertTrue(comparison.lt(testSubject));
	}

	@Test
	void testLe() {
		BigInt comparison = new BigInt("-10");
		assertTrue(comparison.le(testSubject));
		
		comparison = new BigInt("0");
		assertTrue(comparison.le(testSubject));
	}

	@Test
	void testEquals() {
		testSubject = new BigInt("1024");
		
		BigInt comparison = new BigInt("1024");
		
		assertTrue(testSubject.equals(comparison));
	}

	@Test
	void testDividesBy() {
		testSubject = new BigInt("99");
		assertTrue(testSubject.dividesBy(new BigInt("3")));
	}

}
