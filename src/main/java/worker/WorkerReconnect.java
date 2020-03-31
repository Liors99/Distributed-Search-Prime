package worker;

import java.io.*; 
import java.net.*; 
  
class WorkerReconnect { 
  
    public static void main(String args[]) 
        throws Exception 
    { 
    	
    	long startDown = 0; 
    	long stopDown = 0; 
  
        // Create client socket 
        Socket s = new Socket("localhost", 888); 
  
        // to send data to the server 
        DataOutputStream dos 
            = new DataOutputStream( 
                s.getOutputStream()); 
  
        // to read data coming from the server 
        BufferedReader br 
            = new BufferedReader( 
                new InputStreamReader( 
                    s.getInputStream())); 
  
        // to read data from the keyboard 
        BufferedReader kb 
            = new BufferedReader( 
                new InputStreamReader(System.in)); 
        String str, str1 = null; 
  
        // repeat as long as exit 
        // is not typed at client 
        while (!(str = kb.readLine()).equals("exit")) { 
  
            // send to the server 
        	try {
            dos.writeBytes(str + "\n"); 
        	} catch (Exception e) {
        		
        		System.out.println("Server crashed: Rerouting");
                dos.close(); 
                br.close(); 
                s = new Socket("localhost", 880); 
                br 
                = new BufferedReader( 
                    new InputStreamReader( 
                        s.getInputStream())); 
                dos 
                = new DataOutputStream( 
                    s.getOutputStream()); 
                
        	}
  
            // receive from the server 
        	try {
        		str1 = br.readLine(); 
        	} catch (Exception e) {
        		System.out.println("Server crashed: Rerouting");
                dos.close(); 
                br.close(); 
                s.close(); 
                s = new Socket("localhost", 880); 
                br 
                = new BufferedReader( 
                    new InputStreamReader( 
                        s.getInputStream())); 
                dos 
                = new DataOutputStream( 
                    s.getOutputStream()); 
        	}
  
            System.out.println(str1); 
        } 
  
        // close connection. 
        dos.close(); 
        br.close(); 
        kb.close(); 
        s.close(); 
    } 
} 
