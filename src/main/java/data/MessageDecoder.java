
package data;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import server.CoordConsole;

//Message format assisting 
public class MessageDecoder {

	public static boolean parse(String message) {
		//split into key value pairs
		String[] space=message.split(" ");
		String type=space[0].split(":")[1];
		
		
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
				System.out.println("Recieved: "+message);
			}
			return true;
		}
		else if(type.equals("goal")) {
			CoordConsole.task(createmap(message));
			if (CoordConsole.debug) {
				System.out.println("Recieved: "+message);
			}
			return true;	
		}
		else {
			//Message type unknown 
			if (CoordConsole.debug) {
				System.out.println("Didn't understand type "+type);
			}
			//do nothing message not right
			return false;
		}
		
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
