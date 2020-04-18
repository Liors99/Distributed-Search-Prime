package server;
import java.util.*;

/**
 * Aggregates information about the the active workers
 * 
 * @author Mark
 *
 */
public class WorkerDatabase {

	public HashMap<Integer, WorkerRecord> workers;
	public HashMap<Integer, WorkerConnection> workerConnections;
	private final static Object lock = new Object();
	
	public WorkerDatabase() {
		workers = new HashMap<Integer, WorkerRecord>();
		workerConnections = new HashMap<Integer, WorkerConnection>();
	}
	
	public String workers() {
		String work="type:workers ";
		synchronized(lock) {
			Iterator it = workers.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
			    work=work+"wid:"+pair.getKey()+"worker:"+pair.getValue().toString();
			}
		}
		return work;
	}
	
	/**
	 * Adds a worker encoded in a string to a database
	 * 
	 * @param worker
	 */
	public void fromString(String worker) {
		String[] data=worker.split(" wid:");
		for (String i:data) {
			String[] sep=i.split(" ",2);
			int wid=Integer.parseInt(sep[0]);
			WorkerRecord w=new WorkerRecord(sep[1]);
			workers.put(wid, w);
		}
		
	}
	
	
	/**
	 * Adds a worker to the database
	 * @param id the unique worker id
	 * @param wr the worker's record
	 * @param wc the worker's connection
	 */
	public void addWorker(int id, WorkerRecord wr, WorkerConnection wc) {
		synchronized(lock) {
		workers.put(id, wr);
		workerConnections.put(id, wc);
		}
	}
	
	/**
	 * Get the connection object for the specified worker id
	 * @param id the worker's ID
	 * @return the connection instance to the worker
	 */
	public WorkerConnection getConnection(int id) {
		
		return workerConnections.get(id);
	}
	
	/**
	 * Get the record object for the specified worker id
	 * @param id the worker's ID
	 * @return the record instance to the worker
	 */
	public WorkerRecord getRecord(int id) {
		
		return workers.get(id);
	}
	
	
	/**
	 * Randomly generate a unique ID for a worker
	 * @return
	 */
	public int generateID() {
		int id;
		Random rand = new Random();
		do {
			id = rand.nextInt(2000);
		}while(workers.containsKey(id));
		
		return id;
	}
}
