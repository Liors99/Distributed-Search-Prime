package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import data.BigInt;
import data.MessageDecoder;
import data.NetworkMessage;

public class Coordinator {
	
    int id=-2;

	private ServerNetwork server;
    public static List<ServerNetwork> ServerNetworkConnections;
    private BigInt lowerBound;
	private BigInt upperBound;
	private int primeLimit;
	static Store st;
	
	private HashSet<BigInt> primes;
	
	private BigInt current_worked_on;
	
	private ConnectionListener listener;
	
	
    

    public Coordinator(int id, List<ServerNetwork> ServerNetworkConnections, ServerNetwork server, ConnectionListener listener, Store st) {
    	this.id=id;
    	Coordinator.ServerNetworkConnections=ServerNetworkConnections;
    	this.server=server;
    	this.listener=listener;
		this.st=st;
		
		primes = new HashSet<>();
		current_worked_on= new BigInt(BigInt.ZERO);
    	
    }
    
    
    public String getWorkerMessage(TaskScheduler ts) {
    	return ts.getNextWorkerMessage();
    }
    
    
    public BigInt getLowerBound() {
		return lowerBound;
	}


	public void setLowerBound(BigInt lowerBound) {
		this.lowerBound = lowerBound;
	}


	public int getPrimeLimit() {
		return primeLimit;
	}


	public void setPrimeLimit(int primeLimit) {
		this.primeLimit = primeLimit;
	}


	public BigInt getCurrent_worked_on() {
		return current_worked_on;
	}


	public void setCurrent_worked_on(BigInt current_worked_on) {
		this.current_worked_on = current_worked_on;
	}
    
	/**
	 * Run as a coordinator 
	 */
	public void notMain(int listenerPort) {
		
		TaskScheduler ts = new TaskScheduler();
		listener.setTs(ts);
		listener.setCoordinator(true);
		listener.start();
		
		
		//Get user input
		CoordConsole.console();
		lowerBound=new BigInt(CoordConsole.lowerBound);
		upperBound=new BigInt(CoordConsole.upperBound);
		primeLimit= CoordConsole.primeLimit;
		String task="type:COR_GOAL upper:"+upperBound.toString()+" lower:"+lowerBound.toString()+" limit:"+primeLimit;
		// Send tasks to other servers
		try {
			server.sendServers(task, id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		ts.setLower(lowerBound);
		ts.setUpper(upperBound);
		ts.setTarget(primeLimit);



		ts.setStore(st);
		ts.start();
	

		//while (!listener.isReady()) {} //TODO: check what this does
		
		//Start getting messages
		
		
		while(true) {
			//Get message from workers
			String next_message=null;
			
			next_message = getWorkerMessage(ts);
			if(next_message!=null) {
				Map<String, String> m = MessageDecoder.createmap(next_message);
				System.out.println("MESSAGE RECIEVED BY COORDINATOR : "+next_message);
				
				//If message is of type result from worker
				if(m.get("type").equals("SearchResult")) {
					
					//Send the message to all subscribers
					String result = m.get("divisor");
					String tested = m.get("tested");
					if (result.equals("0")){ //Only send in prime numbers
						BigInt prime_add = new BigInt(tested);
						primes.add(prime_add);
						
						//Send to subscribers that this is a prime number
						String send_msg= "type:COR_PRIME prime:"+tested;
						try {
							server.sendServers(send_msg, id);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("Unable to send prime number to subscribers");
							//e.printStackTrace();
						}
						
						
						
					}
				}
				
				
				
			}
			//Get message from subscribers
			if(server.viewNextMessage()!=null) {
				next_message = server.receiveNextMessage();
			    System.out.println("Coordinator recieved: "+next_message);
				Map<String, String> m=MessageDecoder.createmap(next_message);
			    if(m.get("type").contentEquals("recover")) {
				    int sendto=Integer.parseInt(m.get("id"));
				    try {
				    	int p = (InitializeServerCluster.offsetted[id])?InitializeServerCluster.ports[sendto]+InitializeServerCluster.offset*sendto:InitializeServerCluster.ports[sendto];
				    	 //Send the goal
					      server.send(InitializeServerCluster.ips[sendto],p,"type:COR_GOAL upper:"+upperBound.toString()+" lower:"+lowerBound.toString()+" limit:"+primeLimit);
				        //Send the store
					      server.send(InitializeServerCluster.ips[sendto],InitializeServerCluster.ports[sendto],"type:COR_GOAL upper:"+upperBound.toString()+st.get()); 
					    //Send the worker database
					      //TODO
				    } catch (Exception e) {
				    	e.printStackTrace();
					    // Disconnected, Connectionhandler will handle 
				     }
			    }
		}
			
			//Send messages to subscribers for backup purposes
			
			
			//Poll the current number
			BigInt current= ts.getCurrent();
			
			//If they are no the same, we need to update
			if(!current.equals(current_worked_on)) {
				current_worked_on=current;
				
				try {
					server.sendServers("type:COR_CURRENT current:"+current_worked_on, id);
				} catch (Exception e) {
					System.out.println("Error sending current number being worked on to subscribers");
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
			}
			
		}
		
		
    }

	
}