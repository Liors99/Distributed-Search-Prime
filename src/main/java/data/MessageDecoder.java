
package data;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import server.CoordConsole;
import server.Store;

//Message format assisting 
public class MessageDecoder {
	
	/**
	 * 
	 * @param message gotten message
	 * @return type of message 
	 */
	public static String findMessageType(String message) {
		String[] space=message.split(" ");
		//type is an identifier of a message
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

	/**
	 * 
	 * @param message, changes to hashmap
	 * @return key value pairs in message
	 */
	public static Map<String, String> createmap(String message) {
		//Split on space
		String[] space=message.split(" ");
		Map<String, String> map = new HashMap<String, String>();
		for (String i:space) {
			//Separate key vale pairs
			String [] parts=i.split(":");
			if (parts.length==1) {
				System.out.println("Didn't understand "+message);
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
