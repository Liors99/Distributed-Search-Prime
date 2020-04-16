package server;

import java.net.Socket;
import java.util.HashMap;
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
					primes.add(prime_add);
					st.writeResult("Prime: "+prime_add);
					System.out.println("Added "+ prime_add + " to the list");
				}
				else if(m.get("type").contentEquals("COR_CURRENT")) {
					BigInt current = new BigInt(m.get("current"));
					current_worked_on=current;
					st.writeLast("Last checked: "+current);
					System.out.println("Chagned current number to "+ current);
					
				}
				else if(m.get("type").contentEquals("COR_GOAL")) {
					
					System.out.println("----- Setting initial parameters ----- ");
					setGoal(m);
				}
				else if(m.get("type").contentEquals("recover")) {
					
					int sendto=Integer.parseInt(m.get("id"));
					//Not functionally alive unless recovered
					InitializeServerCluster.isAlive[sendto]=false;
					try {
						Socket Sk = server.startConnection(InitializeServerCluster.ips[sendto],InitializeServerCluster.ports[sendto], InitializeServerCluster.ips[id], InitializeServerCluster.ports[id]+(InitializeServerCluster.offset*(id+1)));
					} catch (Exception e1) {
						
					}
				    InitializeServerCluster.offsetted[sendto] = false;
                    server.addServer(InitializeServerCluster.ips[sendto], InitializeServerCluster.ports[sendto]);
                    server.addServer(InitializeServerCluster.ips[id], InitializeServerCluster.ports[id]+(InitializeServerCluster.offset*(sendto+1)));
					int p = (InitializeServerCluster.offsetted[sendto])?InitializeServerCluster.ports[sendto]+InitializeServerCluster.offset*sendto:InitializeServerCluster.ports[sendto];
					try {
						server.send(InitializeServerCluster.ips[sendto],p,"type:l leader:"+InitializeServerCluster.LeaderId);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(m.get("type").equals("Notification")) {
					//Server Has reached operational state
					InitializeServerCluster.isAlive[Integer.parseInt(m.get("ID"))]=true;	
				}
				else if(m.get("type").contentEquals("recoverS")) {
			    	
				    int sendto=Integer.parseInt(m.get("id"));
				    InitializeServerCluster.isAlive[sendto]=false;
				    try {
				    	int tries=0;
				    	Socket Sk=null;
				    	while(tries<10 && Sk==null) {
						     Sk = server.startConnection(InitializeServerCluster.ips[sendto],InitializeServerCluster.ports[sendto], InitializeServerCluster.ips[sendto], InitializeServerCluster.ports[sendto]+(InitializeServerCluster.offset*(sendto+1)));
				    	     tries++;
				    	}
				    } catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				    InitializeServerCluster.offsetted[sendto] = false;
                    server.addServer(InitializeServerCluster.ips[sendto], InitializeServerCluster.ports[sendto]);
                    server.addServer(InitializeServerCluster.ips[id], InitializeServerCluster.ports[id]+(InitializeServerCluster.offset*(sendto+1)));
				    try {
				    	int p = (InitializeServerCluster.offsetted[sendto])?InitializeServerCluster.ports[sendto]+InitializeServerCluster.offset*sendto:InitializeServerCluster.ports[sendto];
				    	 //Send the goal
					      server.send(InitializeServerCluster.ips[sendto],p,"type:COR_GOAL upper:"+upperBound.toString()+" lower:"+lowerBound.toString()+" limit:"+primeLimit);
				        //Send the store
					      server.send(InitializeServerCluster.ips[sendto],InitializeServerCluster.ports[sendto],"type:Store "+st.get()); 
					    //Send the worker database (would prefer they reconnect)
					      //server.send(InitializeServerCluster.ips[sendto],InitializeServerCluster.ports[sendto],listener.wdb.workers());
				        //Let know recover is complete
					      server.send(InitializeServerCluster.ips[sendto],InitializeServerCluster.ports[sendto],"type:RC-Done id:"+id);
					      InitializeServerCluster.isAlive[sendto]=false;
				    } catch (Exception e) {
				    	
					    // Disconnected, Connectionhandler will handle 
				     }
			    }
			}
		}
	}
	
	public void setGoal(Map<String, String> m){
		this.lowerBound=new BigInt(m.get("lower"));
		this.upperBound = new BigInt(m.get("upper"));
		this.primeLimit = Integer.parseInt(m.get("limit"));
	}
	
	public void setStore(String file) {
		st.writeResult(file);
		String [] lines = file.split("\\r?\\n");
		current_worked_on=new BigInt(file.split("Last checked: ")[0]);
		for(int i=1; i<lines.length; i++) {
			primes.add(new BigInt(lines[i].split("Prime: ")[0]));
		}
	}
	
	public void setWDB(String workers) {
		listener.wdb.fromString(workers);
	}
}
