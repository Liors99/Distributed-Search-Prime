package server;

public class ReElectionProtocol {

	/**
	 * Compare the down times of two servers 
	 *
	 * @param  time1 down time of first server
	 * @param  time2 down time of second server
	 * @return      the server with the least amount of down time
	 */
	public int compareDownTime(long time1, long time2) {
		int server = 1; 
		int subscriber1 = 1; 
		int subscriber2 = 2; 
		
		if(time1 < time2) {
			server = subscriber1;
		} else if (time1 > time2) {
			server = subscriber2; 
		}
		
		return server; 
	}
	
}
