package data;

public class HandShakeSubscriber {

	private int ka;
	private double token;
	private int id;

	/**
	 * Constructor, generates a random token when first set up
	 */
	public HandShakeSubscriber() {
		generateToken();
	}


	public HandShakeSubscriber(int ka) {
		this();
		setKA(ka);
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
	public String serializeHandShake(String obj) {
		StringBuilder s = new StringBuilder();
		s.append("type:HSS "+obj+": " +Integer.toString(getKA())+" "+Double.toString(getToken()));

		return s.toString();
	}

	public String serializeHandShake() {
		StringBuilder s = new StringBuilder();
		s.append("type:HSS "+Integer.toString(this.id)+": " +Integer.toString(getKA())+" "+Double.toString(getToken()));

		return s.toString();
	}

	/**
	 * Parses the handhshake and returns the token for the subscriber
	 * @param s - the serialization string
	 * @return - returns the token for subscriber
	 */
	public void parseHandShake(String s) {
		String[] serialized = s.split(" ");

		String type = serialized[0];
		String obj_from = serialized[1];
		this.id = Integer.parseInt(obj_from.split(":")[0]);

		int ka = Integer.parseInt(serialized[2]);
		Double token = Double.parseDouble(serialized[3]);

		setKA(ka);
		setToken(token);

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
