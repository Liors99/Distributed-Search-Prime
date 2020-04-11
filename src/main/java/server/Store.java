package server;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.file.Files;

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
				BufferedReader reader=null;
				try { 
					reader = new BufferedReader(new InputStreamReader( new FileInputStream(f), "UTF-8"));
					String line = reader.readLine();
					reader.close();
					if (line.length()>last.length()){
						throw new IllegalArgumentException("Last must not be a shorter line");
					}
					out.seek(0);
				    updateLine(line, last);
				    
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  catch( NullPointerException e){
					  try {
							out.write(last.getBytes());
						} catch (IOException ee) {
							// TODO Auto-generated catch block
							ee.printStackTrace();
						}
					  
				  } finally {
				        if (reader != null)
							try {
								reader.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
							}
				    }

			}
	    }
	    
	    private void updateLine(String toUpdate, String updated) throws IOException {
	        BufferedReader file = new BufferedReader(new FileReader(f));
	        File temp=new File(f+".out");
	        PrintWriter writer = new PrintWriter(temp, "UTF-8");
	        String line;

	        while ((line = file.readLine()) != null)
	        {
	            line = line.replace(toUpdate, updated);
	            writer.println(line);
	        }
	        file.close();
	        writer.close();
	        out.close();
	        System.out.println("hello");
	        Files.move(temp.getAbsoluteFile().toPath(), f.getAbsoluteFile().toPath(), REPLACE_EXISTING);     
	        out = new RandomAccessFile("output.txt", "rw");
	        System.out.println("test");
	    }
	    
	   public void send(Socket s) {
		   synchronized(lock) {
		          byte [] mybytearray  = new byte [(int)f.length()];
		          FileInputStream fis;
				try {
					fis = new FileInputStream(f);
					BufferedInputStream bis = new BufferedInputStream(fis);
			        bis.read(mybytearray,0,mybytearray.length);
			        OutputStream os = s.getOutputStream();
			        System.out.println("Sending " + f + "(" + mybytearray.length + " bytes)");
			        os.write(mybytearray,0,mybytearray.length);
			        os.flush();
			        System.out.println(f+" sent");
			        if (fis != null) bis.close();
			        if (bis != null) bis.close();
			        if (os != null) os.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		   }
	   }
	    
	   public void shutdown() {
		   synchronized(lock) {
			 try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			   
		   }
		   
	   }
}
