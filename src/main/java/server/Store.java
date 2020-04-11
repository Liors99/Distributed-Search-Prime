package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

public class Store {
	
	RandomAccessFile out;
	File f;
	private final Object lock = new Object();
	
	
	//only initialize once 
	public Store() {
		try {
			//empty file
			new PrintWriter("output.txt").close();
			f=new File("output.txt");
			out = new RandomAccessFile("output.txt", "rw");
		}
		 catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
		
		public void writeResult(String result) {
			synchronized(lock) {
				try {
					result="\n"+result;
					out.seek(out.length());
					out.write(result.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
	}
		
	    public void writeLast(String last) {
			synchronized(lock) {
				try { 
					BufferedReader reader = new BufferedReader(new InputStreamReader( new FileInputStream(f), "UTF-8"));
					String line = reader.readLine();
					if (line.length()>last.length()){
						throw new IllegalArgumentException("Last must not be a shorter line");
					}
					out.seek(0);
				    out.write(last.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  catch( NullPointerException e){
					  try {
							out.seek(0);
							out.write(last.getBytes());
						} catch (IOException ee) {
							// TODO Auto-generated catch block
							ee.printStackTrace();
						}
					  
				  }

			}
	    }
}
