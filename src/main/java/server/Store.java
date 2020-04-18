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
/**
 * 
 * Class to write to long term storage
 *
 */
public class Store {
	
	RandomAccessFile out;
	static File f;
	private final static Object lock = new Object();
	String Filename;
	
	/**
	 * 
	 * @param Filename unique filename
	 */
	//Pass unique filename
	public Store(String Filename) {
		try {
			this.Filename=Filename;
			//empty file
			new PrintWriter(Filename).close();
			f=new File(Filename);
			out = new RandomAccessFile(Filename, "rw");
		}
		 catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	/**
	 * Write result to file 
	 * @return - returns a random number
	 * @param result - the contents to be written to the file
	 */	
	public void writeResult(String result) {
			synchronized(lock) {
				try {
					result=result+System.lineSeparator();
					out.seek(out.length());
					out.write(result.getBytes());
				} catch (IOException e) {
					try {
						out.close();
					} catch (IOException e1) {
						
					}
					try {
						out = new RandomAccessFile(Filename, "rw");
					} catch (FileNotFoundException e1) {
						
					}
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} //unlock	
	}
		
	/**
	 * Write last string to file
	 * @param last - the contents to be written to the file
	 */	
	public void writeLast(String last) {
			synchronized(lock) {
				//last=last+System.lineSeparator();
				BufferedReader reader=null;
				try { 
					reader = new BufferedReader(new InputStreamReader( new FileInputStream(f), "UTF-8"));
					String line = reader.readLine();
					reader.close();
					//make sure current doesn't decrease
					if (line.length()>last.length()){
						throw new IllegalArgumentException("Last must not be a shorter line");
					}
					//write at start of file
					out.seek(0);
				    updateLine(line, last);
				    
				} catch (IOException e) {
					try {
						out.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						out = new RandomAccessFile(Filename, "rw");
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
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

	    
		/**
	 	* Update a specified string
	 	* @param toUpdate - specific string to be updated
		* @param updated - content to replace current string 
		*/	
	    private void updateLine(String toUpdate, String updated) throws IOException {
	        BufferedReader file = new BufferedReader(new FileReader(f));
	        File temp=new File(f+".out");
	        PrintWriter writer = new PrintWriter(temp, "UTF-8");
	        String line;
            //make sure line exists
	        while ((line = file.readLine()) != null)
	        {
	            line = line.replace(toUpdate, updated);
	            writer.println(line);
	        }
	        file.close();
	        writer.close();
	        out.close();
	        Files.move(temp.getAbsoluteFile().toPath(), f.getAbsoluteFile().toPath(), REPLACE_EXISTING);     
	        out = new RandomAccessFile(Filename, "rw");
	    }


		/**
	 	* Recieve the contents of a file as a byte array 
		* @return the file as a bytearray or null if unable to do so.
		*/	
	   public static String get() {
		   synchronized(lock) {
			      String head="type:file ";
			      byte [] header=head.getBytes();
		          byte [] mybytearray  = new byte [header.length+(int)f.length()];
		          FileInputStream fis;
				try {
					fis = new FileInputStream(f);
					
				    BufferedInputStream bis = new BufferedInputStream(fis);
					for (int i=0; i<header.length; i++) {
						mybytearray[i]=header[i];
					}
			        bis.read(mybytearray,head.length(),(int) f.length());
			        
			        if (fis != null) bis.close();
			        if (bis != null) bis.close();
			        return new String(mybytearray);
			        //if (os != null) os.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		   }
		return null;
	   }

		/**
	 	* Update a file by writing to a file
	 	* @param data - what to write to the file
		*/	  
	   public void update(String data) {
		   synchronized(lock) {
		   try {
			    PrintWriter out = new PrintWriter(Filename);
			    out.print(data);
			    out.close();
		     } catch (FileNotFoundException e) {
		 	// TODO Auto-generated catch block
			e.printStackTrace();
		     }
		}


	
	   }

		/**
	 	* Shutdown outputting processes 
		*/	 

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
