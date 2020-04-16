
package data;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import server.CoordConsole;
import server.Store;

//Message format assisting 
public class MessageDecoder {
	
	//static Store s=new Store();
	
	
	public static String findMessageType(String message) {
		String[] space=message.split(" ");
		String type;
		try {
		  type=space[0].split(":")[1];
		}
		catch (Exception e){
			System.out.println("Empty type");
			return null;
		}
		
		return type;
	}

	public static boolean parse(String message) {
		//split into key value pairs
		String[] space=message.split(" ");
		String type;
		try {
		  type=space[0].split(":")[1];
		}
		catch (Exception e){
			return false;
		}
			
		
		//Add your own methods here
		if (type.equals("A")) {
			//Message is a keepalive
			//no further action needed
			return true;
		}
		else if (type.equals("id")) {
			//Identify a server 
			Map<String, String> map=createmap(message);
			CoordConsole.updateConnection(map);
			if (CoordConsole.debug) {
				System.out.println("Received: "+message);
			}
			return true;
		}
		else if(type.equals("goal")) {
			CoordConsole.task(createmap(message));
			if (CoordConsole.debug) {
				System.out.println("Received: "+message);
			}
			return true;	
		}
		else if (type.equals("file")) {
			//s=new Store(); //empty old file
			//s.update(space[1]);
			return true;
		}
		else if(type.equals("WorkerHandshake")) {
		}
		else if(type.equals("initialElection")) {
			//TODO: Initial Election Messages 
			return true; 
		}
		else if(type.equals("reElection")) {
			//TODO: reElection Messages 
			return true; 
		}
		else if (type.equals("rc")) {
			//Server has recovered
			CoordConsole.status[CoordConsole.id]="active";
		}
		else if (type.equals("workers")) {
			//TODO: figure out worker storage
		}
		else {
			//Message type unknown 
			if (CoordConsole.debug) {
				System.out.println("Didn't understand type "+type);
			}
			//do nothing message not right
			return false;
		}
		return false;
		
	}
	
	public static Map<String, String> createmap(String message) {
		String[] space=message.split(" ");
		Map<String, String> map = new HashMap<String, String>();
		for (String i:space) {
			String [] parts=i.split(":");
			if (parts.length==1) {
				if (CoordConsole.debug) {
					System.out.println("Didn't understand "+message);
				}
				//do nothing message not right
			}
			else {
				//store key value pair
				map.put(parts[0], parts[1]);
			}
		} //end for loop
		
		return map;

	}
}
