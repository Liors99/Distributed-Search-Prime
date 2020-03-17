package data;

import java.util.HashMap;
import java.util.Map;

import server.CoordConsole;

//Message format assisting 
public class MessageDecoder {

	public static boolean parse(String message) {
		Map<String, String> map = new HashMap<String, String>();
		
		//split into key value pairs
		String[] space=message.split(" ");
		for (String i:space) {
			String [] parts=i.split(":");
			if (parts.length==1) {
				if (CoordConsole.debug) {
					System.out.println("Didn't understand "+message);
				}
				//do nothing message not right
				return false;
			}
			else {
				//store key value pair
				map.put(parts[0], parts[1]);
			}
		} //end for loop
		
		//extract type
		if (!map.containsKey("type")) {
			if (CoordConsole.debug) {
				System.out.println("Didn't understand "+message);
			}
			//do nothing message not right
			return false;
		}
		
		String type=map.get("type");
		
		//Add your own methods here
		if (type.equals("A")) {
			//Message is a keepalive
			//no further action needed
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
}
