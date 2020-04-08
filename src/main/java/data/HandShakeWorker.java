package data;

public class HandShakeWorker {
	
	private final int MAX_SUBS = 2; //A maximum of 2 subscribers in the system
	private final String SUB_EMPTY="-1"; // A placeholder for an empty slot in the array
	
	private SubscriberInfo[] subscribers_info;
	private int ttl;
	
	
	class SubscriberInfo {
		private String ip;
		private String port;
		
		public SubscriberInfo(String ip, String port){
			this.ip=ip;
			this.port= port;
		}
		
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String getPort() {
			return port;
		}
		public void setPort(String port) {
			this.port = port;
		}
		
		
	}

	//If the ttl is known
	public HandShakeWorker(int ttl) {
		
		//Initialize the subscriber information
		this.subscribers_info = new SubscriberInfo[MAX_SUBS];
		
		for(int i=0; i<this.subscribers_info.length; i++) {
			this.subscribers_info[i] = new SubscriberInfo(SUB_EMPTY,SUB_EMPTY);
		}
		
		
		this.ttl=ttl;
		
	}
	
	
	//ttl is not known
	public HandShakeWorker() {
		//Initialize the subscriber information
		this.subscribers_info = new SubscriberInfo[MAX_SUBS];
		
		for(int i=0; i<this.subscribers_info.length; i++) {
			this.subscribers_info[i] = new SubscriberInfo(SUB_EMPTY,SUB_EMPTY);
		}
	}
	
	/**
	 * Adds the subscribers information to the subscriber info
	 * @param IP - subscriber's ip
	 * @param port - subscriber's port
	 */
	public void addSubscriber(String IP, String port) {
		
		Boolean overwrite=true;
		//Go through the array and check if we the array is full, and if not plug it somewhere in the array
		for(int i=0; i<this.subscribers_info.length; i++) {
			if(this.subscribers_info[i].getIp().equals(SUB_EMPTY) && this.subscribers_info[i].getPort().equals(SUB_EMPTY)) {
				System.out.println("Adding a subscriber at spot " + Integer.toString(i));
				this.subscribers_info[i].setIp(IP);
				this.subscribers_info[i].setPort(port);
				overwrite=false;
				break; //A vaccant spot has been found
			}
		}
		
		if(overwrite) {
			System.out.println("Maximum number of subscribers has been reached, overwriting the first subscriber");
			this.subscribers_info[0].setIp(IP);
			this.subscribers_info[0].setPort(port);
		}
		
	
	}
	
	
	
	/**
	 * Serializes the handshake parameters into a string
	 * @param obj - the object from which the handshake was initiated from
	 * @return - returns the serialized string
	 */
	public String serializeHandShake(String obj) {
		StringBuilder s = new StringBuilder();
		s.append("type:HSW "+obj+": "+Integer.toString(ttl)+" ");
		
		for(int i=0; i<this.subscribers_info.length; i++) {
			s.append(this.subscribers_info[i].getIp()+" ");
			s.append(this.subscribers_info[i].getPort()+" ");
		}
		return s.toString();
	}
	
	
	/**
	 * Parses the handshake serialization into the class
	 * @param s - the serialization string
	 */
	public void parseHandShake(String s) {
		String[] serialized = s.split(" ");
		
		String type = serialized[0];
		String obj_from = serialized[1];
		
		//Set the TTL
		int s_ttl = Integer.parseInt(serialized[2]);
		this.setTtl(s_ttl);
		
		//Add subscriber information
		for(int i=3; i<serialized.length; i+=2) {
			addSubscriber(serialized[i], serialized[i+1]);
		}
		
	}
	
	//Getters and setters for the ttl
	public void setTtl(int t) {
		this.ttl=t;
	}
	
	public int getTtl() {
		return this.ttl;
	}
	
	
	

}
