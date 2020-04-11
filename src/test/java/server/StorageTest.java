package server;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StorageTest {

	@Test
	void empty() {
		BufferedReader reader;
		try {
			Store s=new Store();
	        String w="Lastchecked:10";
			s.writeLast(w);
			File f=new File("output.txt");
			reader = new BufferedReader(new InputStreamReader( new FileInputStream(f), "UTF-8"));
			String line = reader.readLine();
			assertTrue(line.equals(w));
			reader.close();
			s.shutdown();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}  
	@Test
	void replaceLonger() {
		BufferedReader reader;
		try {
			Store s=new Store();
	        String one="Lastchecked:10";
	        String two="Lastchecked:1111";
			s.writeLast(one);
			s.writeLast(two);
			File f=new File("output.txt");
			reader = new BufferedReader(new InputStreamReader( new FileInputStream(f), "UTF-8"));
			String line = reader.readLine();
			assertTrue(line.equals(two));
			reader.close();
			s.shutdown();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Test
	void replaceShorter() {
		BufferedReader reader=null;
		try {
			File f=new File("output.txt");
			reader = new BufferedReader(new InputStreamReader( new FileInputStream(f), "UTF-8"));
			Store s=new Store();
	        String one="Lastchecked:10";
	        String two="Lastchecked:1111";
			s.writeLast(two);
			assertThrows(IllegalArgumentException.class, 
				 ()->s.writeLast(one));
			s.shutdown();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (reader !=null) {try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
		}
	}
	 
    
	@Test
	void addResults() {
		BufferedReader reader;
		try {
			File f=new File("output.txt");
			reader = new BufferedReader(new InputStreamReader( new FileInputStream(f), "UTF-8"));
			Store s=new Store();
	        String w="Lastchecked: 10";
			s.writeLast(w);
			s.writeResult("Prime:17");
			String line = reader.readLine();
			line=reader.readLine();
			assertTrue(line.equals("Prime:17"));
			reader.close();
			s.shutdown();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	@Test
	void sandwhich() {
		BufferedReader reader;
		try {
			
			Store s=new Store();
	        String w="Lastchecked:10";
			s.writeLast(w);
			s.writeResult("Prime:17");
			s.writeLast("Lastchecked:1111");
			File f=new File("output.txt");
			reader = new BufferedReader(new InputStreamReader( new FileInputStream(f), "UTF-8"));
			String line = reader.readLine();
			line=reader.readLine();
			assertTrue(line.equals("Prime:17"));
			reader.close();
			s.shutdown();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	 
	
	
}
