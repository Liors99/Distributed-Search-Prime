package server;
import java.util.*;


public class WorkerDatabase {

	private HashMap<Integer, WorkerRecord> workers;
	private HashMap<Integer, WorkerConnection> workerConnections;
	
	public WorkerDatabase() {
		workers = new HashMap<Integer, WorkerRecord>();
	}
	
	public void addWorker(WorkerRecord wr, WorkerConnection wc) {
		int id = generateID();
		workers.put(id, wr);
		workerConnections.put(id, wc);
	}
	
	public int generateID() {
		int id;
		Random rand = new Random();
		do {
			id = rand.nextInt();
		}while(!workers.containsKey(id));
		
		return id;
	}
}
