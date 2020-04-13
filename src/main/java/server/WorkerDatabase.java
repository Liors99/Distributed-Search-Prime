package server;
import java.util.*;


public class WorkerDatabase {

	public HashMap<Integer, WorkerRecord> workers;
	public HashMap<Integer, WorkerConnection> workerConnections;
	
	public WorkerDatabase() {
		workers = new HashMap<Integer, WorkerRecord>();
		workerConnections = new HashMap<Integer, WorkerConnection>();
	}
	
	public void addWorker(int id, WorkerRecord wr, WorkerConnection wc) {
		workers.put(id, wr);
		workerConnections.put(id, wc);

	}
	
	public WorkerConnection getConnection(int id) {
		
		return workerConnections.get(id);
	}
	
	public WorkerRecord getRecord(int id) {
		
		return workers.get(id);
	}
	
	public int generateID() {
		int id;
		Random rand = new Random();
		do {
			id = rand.nextInt(2000);
		}while(workers.containsKey(id));
		
		return id;
	}
}
