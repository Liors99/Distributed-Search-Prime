package server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import data.BigInt;
import data.MessageDecoder;

public class Subscriber {
	
	private int id;
	private int id_cor;
	private ServerNetwork server;
	private static Store st;
	private ArrayList<BigInt> primes;
	
	private BigInt lowerBound;
	private BigInt upperBound;
	private int primeLimit;
	
	private BigInt current_worked_on;
	

	private ConnectionListener listener;
	
	public Subscriber(int id, int id_cor, ServerNetwork server, ConnectionListener listener, Store st) {

		this.id=id;
		this.id_cor=id_cor;
		this.server=server;		

		this.listener=listener;
		this.st=st;
		
		primes = new ArrayList<>();
		lowerBound= new BigInt(BigInt.ZERO);
		upperBound= new BigInt(BigInt.ZERO);
		primeLimit= 0;
		current_worked_on = new BigInt(BigInt.ZERO);
		
		this.listener.setTs(null);
		this.listener.setCoordinator(false);
	

	}
	
	public void loadFromSubscriber(Subscriber s) {
		this.lowerBound=s.getLowerBound();
		this.upperBound=s.getUpperBound();
		this.primeLimit=s.getPrimeLimit();
		
		this.primes=s.getPrimes();
		this.current_worked_on=s.getCurrent_worked_on();
	}
	
	public void notMain() {
		
	
		
		//Read from coordinator
		
		while(true) {
			if(server.viewNextMessage()!=null) {
				String next_message = server.receiveNextMessage();
				System.out.println("MESSAGE RECIEVED BY SUBSCRIBER: "+ next_message);
				Map<String, String> m = MessageDecoder.createmap(next_message);
				
				//Parse the message
				
				if(m.get("type").contentEquals("COR_PRIME")) {
					BigInt prime_add = new BigInt(m.get("prime"));
					System.out.println("Added "+ prime_add + " to the list");
					primes.add(prime_add);
				}
				else if(m.get("type").contentEquals("COR_CURRENT")) {
					BigInt current = new BigInt(m.get("current"));
					System.out.println("Chagned current number to "+ current);
					current_worked_on=current;
					
				}
				else if(m.get("type").contentEquals("COR_GOAL")) {
					
					System.out.println("----- Setting initial parameters ----- ");
					this.lowerBound=new BigInt(m.get("lower"));
					this.upperBound = new BigInt(m.get("upper"));
					this.primeLimit = Integer.parseInt(m.get("limit"));
				}
				else if(m.get("type").contentEquals("HSSR")) {
					
					//Add the message back for re-election to deal with it
					while(!InitializeServerCluster.reelectionStarted) {
						System.out.println("Waiting for re election to finish");
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					InitializeServerCluster.ElectReelectionLeader(next_message);
				}
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<BigInt> getPrimes() {
		return primes;
	}

	public void setPrimes(ArrayList<BigInt> primes) {
		this.primes = primes;
	}

	public BigInt getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(BigInt lowerBound) {
		this.lowerBound = lowerBound;
	}

	public BigInt getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(BigInt upperBound) {
		this.upperBound = upperBound;
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
}
