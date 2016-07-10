package np2016.GraphSearch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import np2016.Options;
import np2016.Graph.Edge;
import np2016.Graph.Graph;
import np2016.Graph.Node;

public class ConcurrentGraphSearch<N extends Node<?>, E extends Edge<N, ?>> extends BFSGraphSearch<N, E> {

	// stores all nodes visited so far
	final Set<N> visited = new HashSet<N>();
	// stores all nodes that still need processing
	final HashMap<Integer, Queue<N>> todo = new HashMap<>();
	final HashMap<Integer, Integer> checker = new HashMap<>();
	private Queue<N> queue = new LinkedList<>();
	private int myID;

	public ConcurrentGraphSearch(BFSGraphVisitor<N, E> visitor) {
		super(visitor);
	}

	@Override
	public void search(Graph<N, E> graph, N startVertex) {

		// handle the start node
		this.visitor.startVertex(graph, startVertex);
		this.visitor.discoverVertex(graph, startVertex);
		for (int i = 0; i < Options.THREADS.getNumber(); i++) {
			checker.put(i, 0);
		}
		
		Random random = new Random();
		int number = random.nextInt(Options.THREADS.getNumber());
		queue.offer(startVertex);
		todo.put(number, queue);
		checker.put(number, 1);
		
		for (int i = 0; i < Options.THREADS.getNumber(); i++) {
//			workOnGraph(graph);
			Thread thread = new Thread(new Runnable() {
				public void run() {
					workOnGraph(graph);
				}
			});
			thread.start();
		}
	}

	public void workOnGraph(Graph<N, E> graph) {
		int ID;
		synchronized (this) {
			ID = getID();
			setID();
		}
//		System.out.println(Thread.currentThread().getName() + " with ID -> " + ID);
		while (true) {
//			System.out.println("Worker with ID -> " + ID + " works on Todo: " + checker.get(ID).toString());
			while (checker.get(ID) == 0) {
				synchronized (todo) {
					try {
						todo.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			N next;
			synchronized (todo) {
				next = todo.get(ID).poll();
				checker.put(ID, checker.get(ID) - 1);
				remember(next);
			}
			for (E edge : graph.getEdges(next)) {
				N target = edge.getTarget();
				Random random = new Random();
				int number = random.nextInt(Options.THREADS.getNumber());
				if (!alreadyWorked(target)) {
					this.visitor.treeEdge(graph, edge);
					this.visitor.discoverVertex(graph, target);
					
					synchronized (todo) {
						queue.offer(target);
						todo.put(number, queue);
						checker.put(number, checker.get(number) + 1);
						todo.notifyAll();
					}
					
				} else {
					this.visitor.nonTreeEdge(graph, edge);
				}
			}
		}
	}

	synchronized private boolean alreadyWorked(N node) {
		if (visited.contains(node))
			return true;
		else
			return false;
	}

	synchronized private void remember(N node) {
		visited.add(node);
	}

	synchronized private void setID() {
		this.myID++;
	}

	synchronized private int getID() {
		return this.myID;
	}
}
