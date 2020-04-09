
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
import java.util.concurrent.TimeUnit;


public class CoordConsole {
	public static boolean debug=true; 
	 //going to have to be updated on each machine
	static String [] Ips= {"192.168.56.1", "192.168.56.1", "192.168.56.1"};
	//pick fav port number
	static int [] ServerPorts = {4444, 5555, 6666};
	static String[] status= {"dead", "dead", "dead"};
	//by default run as 
	private static int mode=0;
	public static Socket [] sockets =new Socket [3];
	
	private static boolean validRange = false;
	private static BigInteger lowerBound;
	private static BigInteger upperBound;
	private static int primeLimit;
	private static ServerSocket s;
    static int send =100000;
	static int timeout=20000000;
	static int id; 
	
	public static void main(String[] args) {
	//public static void notMain(String[] args){
	 Socket [] sockets =new Socket [3];
	 id=Integer.parseInt(args[0]);
	 status[id]="setup";
	 //Create a server socket and two other sockets
	 setup();
	 //listen
	 Accept a=new Accept(s);
	 Thread thread = new Thread(a);
	 thread.start();
	//might recommend sleeping for a bit to activate servers, feel free to increase
		 try {
			TimeUnit.SECONDS.sleep(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 //Attempt to connect to other servers
	 for (int i=0; i<3; i++) {
		if (i!=id) {
			try {
				sockets[i] = new Socket(Ips[i], ServerPorts[i]);
				if (debug) {
					System.out.println("Connected to "+sockets[i]);
				}
				//May  need to actually ask but good enough for now
				if (status[i].equals("dead")) {
					status[i]="setup";
					Recieve r=new Recieve(sockets[i], send, timeout);
					Thread thread1 = new Thread(r);
					thread1.start();
				}
			} catch (Exception e) {
				//Assume still inactive, they will contact us
				System.out.println(i+" suspected inactive");
			}
		}
	 } //end loop
	 //need to give everyone time to start up 
	 //then need to host an election
	 
	 //for now just going to keep alive forever :)
	 
	 //Not currently using election
	 if (id==0){
		 mode=1;
	 }
	 //get input and send to other servers
	 if (mode==1) {
		 console();
		 for (int i=0; i<3; i++) {
				if (i!=id) {
					if (!status[i].equals("dead")&sockets[1]!=null){
						try {
							OutputStream s=sockets[i].getOutputStream();
							new DataOutputStream(s).writeUTF("type:goal upper:"+upperBound.toString()+" lower:"+lowerBound.toString()+" limit:"+primeLimit);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
		 } //end for loop
	 }
	 
	 
	while(true){}
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
				s=new ServerSocket(ServerPorts[id]);
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

public static void updateConnection(Map<String, String> map) {
	int id=Integer.parseInt(map.get("id"));
	try {
		sockets[id] = new Socket(Ips[id], ServerPorts[id]);
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		if (map.containsKey("status")) {
			status[id]=map.get("status");
		}

  }
	
	public static void task(Map<String, String> map) {
		if (map.containsKey("upper")) {
			upperBound=new BigInteger(map.get("upper"));
		}
		if (map.containsKey("lower")) {
			upperBound=new BigInteger(map.get("lower"));
		}
		if (map.containsKey("limit")) {
			primeLimit=Integer.parseInt((map.get("limit")));
		}		
}
	
}
