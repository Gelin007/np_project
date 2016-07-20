package np2016;

import java.util.LinkedList;
import java.util.Queue;

import np2016.Graph.Edge;
import np2016.Graph.Node;

public class WorkList <N extends Node<?>, E extends Edge<N, ?>> {
		
	private Queue<N> queue;
		
	public WorkList() {
		queue = new LinkedList<>();
	}
	
	synchronized public void offer(N vertex){
		queue.offer(vertex);
	}
	
	synchronized public N poll(){
		return queue.poll();
	}

	synchronized public boolean isEmpty() {
		// TODO Auto-generated method stub
		return queue.isEmpty();
	}
	
		
}
