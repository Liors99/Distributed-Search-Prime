package server;

import java.net.Socket;
import java.util.HashSet;
import java.util.Map;

import data.BigInt;
import data.MessageDecoder;

public class Subscriber {
	
	private int id;
	private int id_cor;
	private ServerNetwork server;
	private static Store st;
	private HashSet<BigInt> primes;
	
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
		
		primes = new HashSet<>();
		current_worked_on = new BigInt(BigInt.ZERO);

	}
	
	public void notMain(int listenerPort) {
		listener.start();

		
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
				else if(m.get("type").contentEquals("recover")) {
					
					int sendto=Integer.parseInt(m.get("id"));
					try {
						Socket Sk = server.startConnection(InitializeServerCluster.ips[sendto],InitializeServerCluster.ports[sendto], InitializeServerCluster.ips[sendto], InitializeServerCluster.ports[sendto]+(InitializeServerCluster.offset*(sendto+1)));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				    InitializeServerCluster.offsetted[sendto] = false;
                    server.addServer(InitializeServerCluster.ips[sendto], InitializeServerCluster.ports[sendto]);
                    server.addServer(InitializeServerCluster.ips[id], InitializeServerCluster.ports[id]+(InitializeServerCluster.offset*(sendto+1)));
					int p = (InitializeServerCluster.offsetted[sendto])?InitializeServerCluster.ports[sendto]+InitializeServerCluster.offset*sendto:InitializeServerCluster.ports[sendto];
					try {
						server.send(InitializeServerCluster.ips[sendto],p,"leader:"+InitializeServerCluster.LeaderId);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
