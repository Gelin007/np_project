package np2016;

import java.util.LinkedList;
import java.util.Queue;

import np2016.Graph.Node;

public class WorkList <N extends Node<?>> {
		
	/**
	 * 
	 */
	private Queue<N> queue;
		
	/**
	 * 
	 */
	public WorkList() {
		queue = new LinkedList<>();
	}
	
	/**
	 * @param vertex
	 */
	synchronized public void offer(N vertex){
		queue.offer(vertex);
	}
	
	/**
	 * @return
	 */
	synchronized public N poll(){
		return queue.poll();
	}

	/**
	 * @return
	 */
	synchronized public N peek(){
		return queue.peek();
	}
	
	/**
	 * @return
	 */
	synchronized public boolean isEmpty() {
		return queue.isEmpty();
	}
		
}
