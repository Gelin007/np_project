package np2016;

import java.util.HashMap;
import np2016.WorkList;

public class TodoHashMap {
	private HashMap<Integer, WorkList> todo;

	public TodoHashMap (int numberOfThreads){
		todo=new HashMap<Integer, WorkList>();
		
		for (int i = 0; i < numberOfThreads; i++) {
		todo.put(i, new WorkList());
		}
	}
	
	synchronized public void put(int ID, WorkList queue) {
		todo.put (ID, queue);
	}
	
	synchronized public WorkList get(int ID) {
		return todo.get (ID);
	}
	
}
