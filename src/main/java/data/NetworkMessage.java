package data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class NetworkMessage {
	
	//function to get sub-array of a non-primitive array between specified indices (https://www.techiedelight.com/get-subarray-array-specified-indexes-java/)
	private static byte[] subArray(byte[] n_msg, int beg, int end) {
		return Arrays.copyOfRange(n_msg, beg, end + 1);
	}
	
	/**
	 * Converts a string to the appropriate message format for socket communication
	 * @param msg - message to be sent
	 * @return - returns a byte[] that is to be sent over the socket
	 */
	public static byte[] toNetworkMessage(String msg) {
		int length = msg.length();
		
		byte[] length_b = ByteBuffer.allocate(4).putInt(length).array();
		byte[] network_msg = new byte[4+length];
		
		System.arraycopy(length_b, 0, network_msg, 0, 4);
		
		System.arraycopy(msg.getBytes(), 0, network_msg, 4, length);
		return network_msg;
		
	}
	

	
	/**
	 * Sends the specified message msg, to the specified outputstream
	 * @param out - the output stream
	 * @param msg - the message to be sent
	 * @throws IOException
	 */
	public static void send(DataOutputStream out, String msg) throws IOException {
		out.write(NetworkMessage.toNetworkMessage(msg));
		out.flush();
	}
	
	/**
	 * Reads the next message on the specified inputstream
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String receive(DataInputStream in) throws IOException {
		
		//Read in the length of the input
		byte[] length_bytes = new byte[4]; // The length of the input
		in.read(length_bytes, 0, 4);
		
		int length = ByteBuffer.wrap(length_bytes).getInt();
		
		byte[] msg_bytes = new byte[length];
		in.read(msg_bytes,0,length);
		
		return new String(msg_bytes);
	}
}
