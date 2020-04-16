package data;

import java.util.Map;

public class HandShakeSubscriber {

	private int ka;
	private double token;
	private int id;

	/**
	 * Constructor, generates a random token when first set up
	 */

	
	public HandShakeSubscriber() {
		setKA(-1);
		setToken(-1);
		setID(-1);
	}
	
	public HandShakeSubscriber(int id, int ka) {
		setKA(ka);
		this.id=id;
		generateToken();
	}
	
	public HandShakeSubscriber(int id, int token, double up_time) {
		setToken(up_time);
		setKA(token);
		this.id=id;
	}


	/**
	 * Generates a random number for leader election
	 * @return - returns a random number
	 */
	public void generateToken() {
		setToken(Math.random());
	}

	/**
	 * Serialize the handshake
	 * @param obj - The name of the object from which this handshake was initiated from
	 * @return
	 */
	public String serializeHandShake() {
		StringBuilder s = new StringBuilder();
		s.append("type:HSS "+ "server:" +this.id +" ka:"+Integer.toString(getKA())+" token:"+Double.toString(getToken()));

		return s.toString();
	}

	/**
	 * Parses the handhshake and returns the token for the subscriber
	 * @param s - the serialization string
	 * @return - returns the token for subscriber
	 */
	public void parseHandShake(String s) {
		
		
		Map<String, String> m = MessageDecoder.createmap(s);
		this.id=Integer.parseInt(m.get("server"));
		int ka_recv = Integer.parseInt(m.get("ka"));
		double token_recv = Double.parseDouble(m.get("token"));
		
		setKA(ka_recv);
		setToken(token_recv);

	}


	//Getters and setters for the ka and token
	public void setKA(int ka) {
		this.ka=ka;
	}

	public int getKA() {
		return this.ka;
	}

	public void setID(int id) {
		this.id=id;
	}

	public int getID() {
		return this.id;
	}

	public double getToken() {
		return token;
	}

	public void setToken(double token) {
		this.token = token;
	}
}
