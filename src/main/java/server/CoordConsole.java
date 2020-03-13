//either no arguments or mode + Coordinator IP
package server;
import java.io.*;
import java.math.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.net.UnknownHostException;
import java.util.*;


public class CoordConsole {
	 //going to have to be updated on each machine
	static String [] Ips= {"192.168.56.1", "192.168.56.1", "192.168.56.1"};
	//pick fav port number
	private static int [] ServerPorts = {4444, 5555, 6666};
	static String[] status= {"dead", "dead", "dead"};
	//by default run as 
	private static int mode=0;
	
	private static boolean validRange = false;
	private static BigInteger lowerBound;
	private static BigInteger upperBound;
	private static int primeLimit;
	private static ServerSocket s;
	static Socket [] sockets =new Socket [3];
    static int send =3000;
	static int timeout=5000;
	private static int id; 
	
	public static void main(String[] args) {
	 id=Integer.parseInt(args[0]);
	 status[id]="setup";
	 //Create a server socket and two other sockets
	 createServer();
	 Accept a=new Accept(s);
	 //Attempt to connect to other servers
	 for (int i=0; i<3; i++) {
		if (i!=id) {
			try {
				sockets[i] = new Socket(Ips[i], ServerPorts[i]);
				//May  need to actually ask but good enough for now
				if (status[i]=="dead") {
					status[i]="active";
					Recieve r=new Recieve(sockets[i], send, timeout);
					r.run();
				}
			} catch (Exception e) {
				//Assume still inactive, they will contact us
			}
		}
	 } //end loop
	 
	 
	}


	public static void console() {
		PrintStream console = new PrintStream(System.out);
		Scanner scan = new Scanner(System.in);
		
		while (!validRange) {
			lowerBound = getBound(console, scan, "lower");
			upperBound = getBound(console, scan, "upper");
			
			if (lowerBound.compareTo(upperBound) >= 0) {
				console.println("Error: The lower bound cannot be higher or equal to the upper bound");
			}
			else {
				primeLimit = getPrimeCount(console, scan);
				validRange = true;
			}
			
		}
		
		//TODO: add the the actual numbers received
		System.out.println("The system will search for primes in range, press ENTER to begin:");
		scan.nextLine();
		
		
		
	}
	
	static BigInteger getBound(PrintStream console, Scanner scan, String boundType) {
		BigInteger result = new BigInteger("0");
		boolean validInput = false;
		
		while (!validInput) {
			
			console.println("Please enter the "+boundType+" bound for the search:");
			try {
				result = parseInput(scan.nextLine());
				validInput = true;
			}
			catch(NumberFormatException e) {
				console.println("Error: Please enter a number in integer or scientific format");
			}
		}
		return result;
	}
	
	public static BigInteger parseInput(String str) throws NumberFormatException {
		BigInteger result = new BigInteger("0");
		if (str.matches("\\d*[eE]\\d*")) {
			String[] baseExp = str.split("[eE]");
			result = new BigInteger(baseExp[0]);
			result = result.multiply(new BigInteger("10").pow(Integer.parseInt(baseExp[1])));
		}
		else {
			result = new BigInteger(str);
			if (result.compareTo(new BigInteger("0")) <= 0) {
				throw new NumberFormatException();
			}
		}
		return result;
		
	}
	
	static int getPrimeCount(PrintStream console, Scanner scan){
		int result = 0;
		boolean validInput = false;
		String input;
		
		while (!validInput) {
			
			console.println("Please enter the desired number of primes:");
			input = scan.nextLine();
			
			try {
				result = Integer.parseInt(input);
				if (result <= 0) {
					throw new NumberFormatException();
				}
				validInput = true;
			}
			catch (NumberFormatException e) {
				console.println("Error: Please enter a positive integer");
			}
		}
		
		return result;
		
		
	}
	
public static void setup() {
	//Get Network info for testing
			try {
				s=new ServerSocket();
				InetAddress ip = InetAddress.getLocalHost();
	            String hostname = ip.getHostName();
	            System.out.println("Your current IP address : " + ip);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
}

public static void createServer() {
	try {
		s = new ServerSocket(ServerPorts[id]);
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
	
}
