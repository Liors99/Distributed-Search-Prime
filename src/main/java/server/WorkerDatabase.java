package server;
import java.util.*;


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
	
	public void fromString(String worker) {
		String[] data=worker.split(" wid:");
		for (String i:data) {
			String[] sep=i.split(" ",2);
			int wid=Integer.parseInt(sep[0]);
			WorkerRecord w=new WorkerRecord(sep[1]);
			workers.put(wid, w);
		}
		
	}
	
	public void addWorker(int id, WorkerRecord wr, WorkerConnection wc) {
		synchronized(lock) {
		workers.put(id, wr);
		workerConnections.put(id, wc);
		}
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
