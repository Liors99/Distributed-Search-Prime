package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import data.BigInt;
import data.MessageDecoder;
import data.NetworkMessage;

/**
 * A class for the coordinator
 *
 */
public class Coordinator {
	
    int id=-2;

	private ServerNetwork server;
    private BigInt lowerBound;
	private BigInt upperBound;
	private int primeLimit;
	static Store st;
	
	private ArrayList<BigInt> primes;
	
	private BigInt current_worked_on;
	
	private ConnectionListener listener;
	private TaskScheduler ts;
	
	
    
	/**
	 * Constructor for the Coordinator
	 * @param id - id of the server
	 * @param server - the server object that listens for connections
	 * @param listener - Listener for the workers
	 */
    public Coordinator(int id, ServerNetwork server, ConnectionListener listener) {
    	this.id=id;
    	this.server=server;
    	this.listener=listener;
		
		primes = new ArrayList<>();
		lowerBound= new BigInt(BigInt.ZERO);
		upperBound= new BigInt(BigInt.ZERO);
		primeLimit= 0;
		current_worked_on= new BigInt(BigInt.ZERO);
		
		ts = new TaskScheduler();
		
		addWorkersToTaskScheduler(); //Reschedule all the available workers
		
		//Set it to a coordinator on the listener
		this.listener.setTs(ts);
		this.listener.setCoordinator(true);
		listener.takeOverAsCoordinator();
		
		
    	
    }
    
    
    //Getters and setters
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
	 * Load all the parameters from the subscriber into this coordinator object
	 * @param s - The old subscriber from which we take the data from
	 */
	public void loadFromSubscriber(Subscriber s) {
		System.out.println("Loading from subscriber");
		this.lowerBound=s.getLowerBound();
		this.upperBound=s.getUpperBound();
		this.primeLimit=s.getPrimeLimit();
		
		this.primes=s.getPrimes();
		this.current_worked_on=s.getCurrent_worked_on();
		
		
		this.ts.setLower(this.lowerBound);
		this.ts.setUpper(this.upperBound);
		this.ts.setTarget(this.primeLimit);

		this.ts.setStore(st);

		
		this.ts.setPrimes(this.primes); //Add the list of primes to the new TS

	}
	
	/**
	 * Adds workers to queue to be scheduled by
	 */
	public void addWorkersToTaskScheduler() {
		WorkerDatabase wdb = this.listener.getWdb();
		
		for(int worker_id : wdb.workers.keySet()) {
			ts.addToWorkerQueue(wdb.workers.get(worker_id));
		}
	}
	
	
	/**
	 * Gets the user's input from the console
	 * @param ts - Task scheduler to put the tasks into
	 */
	public void getUserInput(TaskScheduler ts) {
		//Get user input
		CoordConsole.resetVals();
		CoordConsole.console();
		lowerBound=new BigInt(CoordConsole.lowerBound);
		upperBound=new BigInt(CoordConsole.upperBound);
		primeLimit= CoordConsole.primeLimit;
		String task="type:COR_GOAL upper:"+upperBound.toString()+" lower:"+lowerBound.toString()+" limit:"+primeLimit;
		this.current_worked_on=lowerBound;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM_dd_hh_mm");
	    String dateAsString = simpleDateFormat.format(new Date());
		this.st=new Store("Primes_"+lowerBound+"_to_"+upperBound+"_"+dateAsString+"_ID"+id+".txt");
		st.writeLast("Last checked: 0\n");
		ts.setLower(lowerBound);
		ts.setUpper(upperBound);
		ts.setTarget(primeLimit);
		
		this.ts.setStore(st);
		// Send tasks to other servers
		try {
			if(CoordConsole.quit) {
				server.sendServers("type:quit", id);
				System.exit(0);
			}
			else {
			   server.sendServers(task, id);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		
	}
	
	/**
	 * Run as a coordinator 
	 */
	public void notMain() {

		//If we haven't searched yet
		if(primeLimit == 0) {
			getUserInput(ts);
		}
		else {
			//Load from the store otherwise
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM_dd_hh_mm");
		    String dateAsString = simpleDateFormat.format(new Date());
			Store news=new Store("Primes_"+lowerBound+"_to_"+upperBound+"_"+dateAsString+"_ID"+id+".txt");
		    ts.setStore(news);
		}
		
		
		
		ts.setCurrent(this.current_worked_on); //Set the current number being worked on
		
		ts.start(); //Start doing work
	
		System.out.println("The system will try to find " + primeLimit +" primes in the range of " + lowerBound +" to "+ upperBound);
		
		//Start getting messages
		while(true) {
			
			//Get message from workers
			String next_message=null;
			
			
			//Poll the primes
			for(int i=0; i<ts.getPrimes().size(); i++) {
				BigInt p =ts.getPrimes().get(i);
				if(!this.primes.contains(p)) {
					this.primes.add(p);
					String send_msg= "type:COR_PRIME prime:"+p;
					try {
						server.sendServers(send_msg, id);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
				    	int tries=0;
				    	Socket Sk=null;
				    	while(tries<10 && Sk==null) {
						     Sk = server.startConnection(InitializeServerCluster.ips[sendto],InitializeServerCluster.ports[sendto], InitializeServerCluster.ips[id], InitializeServerCluster.ports[id]+(InitializeServerCluster.offset*(id+1)));
						     InitializeServerCluster.offsetted[sendto] = false;
			                 server.addServer(InitializeServerCluster.ips[sendto], InitializeServerCluster.ports[sendto]);
			                 server.addServer(InitializeServerCluster.ips[id], InitializeServerCluster.ports[id]+(InitializeServerCluster.offset*(sendto+1)));
				    	     tries++;
				    	}
				    }	
				     catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				    try {
				    	int p = (InitializeServerCluster.offsetted[sendto])?InitializeServerCluster.ports[sendto]+InitializeServerCluster.offset*sendto:InitializeServerCluster.ports[sendto];
				    	 //Send the goal
					      server.send(InitializeServerCluster.ips[sendto],p,"type:COR_GOAL upper:"+upperBound.toString()+" lower:"+lowerBound.toString()+" limit:"+primeLimit);
				        //Send the store
					      server.send(InitializeServerCluster.ips[sendto],InitializeServerCluster.ports[sendto],"type:Store "+st.get()); 
					    //Send the worker database (would prefer they reconnect)
				        //Let know recover is complete
					      server.send(InitializeServerCluster.ips[sendto],InitializeServerCluster.ports[sendto],"type:RC-Done id:"+id);
				    } catch (Exception e) {
				    	
					    // Disconnected, Connectionhandler will handle 
				     }
			    }
			}
			
			if(next_message!=null) {
				Map<String, String> m = MessageDecoder.createmap(next_message);
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
			
			
			
			//Send worker information
			//Check if task complete
			if(ts.isDone()) {
				//If task is done, ask the user for another goal
			    TaskScheduler newts=new TaskScheduler();
				CoordConsole.resetVals();
				CoordConsole.console();
				lowerBound=new BigInt(CoordConsole.lowerBound);
				upperBound=new BigInt(CoordConsole.upperBound);
				primeLimit= CoordConsole.primeLimit;
				String task="type:COR_GOAL upper:"+upperBound.toString()+" lower:"+lowerBound.toString()+" limit:"+primeLimit;
				
				this.current_worked_on=lowerBound;
				
				newts.setLower(lowerBound);
				newts.setUpper(upperBound);
				newts.setTarget(primeLimit);
				newts.setCurrent(lowerBound);
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM_dd_hh_mm");
			    String dateAsString = simpleDateFormat.format(new Date());
				st=new Store("Primes_"+lowerBound+"_to_"+upperBound+"_"+dateAsString+"_ID"+id+".txt");
				st.writeLast("Last checked 0\n");
				//Create a new store
				newts.setStore(st);
				newts.setWorkerQueue(ts.getWorkerQueue());
				newts.setActiveWorkers(ts.getActiveWorkers());
				
				System.out.println("The system will try to find " + primeLimit +" primes in the range of " + lowerBound +" to "+ upperBound);
				
				// Send tasks to other servers
				try {
					if(CoordConsole.quit) {
						server.sendServers("type:quit", id);
					    System.exit(0);
					}
					server.sendServers(task, id);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ts=newts;
				ts.start();
				
			}
			
		}
		
		
    }

	
}