package np2016;

import java.util.LinkedList;
import java.util.Queue;

import np2016.Graph.Node;

/**
 * Worklist is a monitor, that represent a data structure (LinkedList), where
 * nodes that still need processing are stored. On this data structure, Workers (Threads) can take
 * nodes, with which they can use the semantics on.
 * 
 * @param <N>
 *            Node
 */
public class WorkList<N extends Node<?>> {

	/**
	 * The data structure, where nodes are stored.
	 */
	private Queue<N> queue;

	public WorkList() {
		queue = new LinkedList<>();
	}

	/**
	 * Inserts the specified node into this worklist if it is possible to do so
	 * immediately without violating capacity restrictions. When using a
	 * capacity-restricted queue, this method is generally preferable to add,
	 * which can fail to insert a node only by throwing an exception.
	 * 
	 * @param vertex,
	 *            The node
	 */
	synchronized public void offer(N vertex) {
		queue.offer(vertex);
	}

	/**
	 * Retrieves and removes the head of this worklist, or returns null if this
	 * worklist is empty.
	 * 
	 * @return a Node
	 */
	synchronized public N poll() {
		return queue.poll();
	}

	/**
	 * Returns true if this collection contains no nodes.
	 * 
	 * @return boolean
	 */
	synchronized public boolean isEmpty() {
		return queue.isEmpty();
	}

}
