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
	final Set<N> visited;
	// stores all nodes that still need processing
	final HashMap<Integer, Queue<N>> todo;
	final HashMap<Integer, Integer> checker;
//	private Queue<N> queue;
	private int myID;
	private int numberOfWorker;

	public ConcurrentGraphSearch(BFSGraphVisitor<N, E> visitor) {
		super(visitor);
		visited = new HashSet<N>();
		todo = new HashMap<>();
		checker = new HashMap<>();
		numberOfWorker = Options.THREADS.getNumber();
		myID = 0;
	}

	@Override
	public void search(Graph<N, E> graph, N startVertex) {
		Queue<N> queue = new LinkedList<>();
		
		// handle the start node
		this.visitor.startVertex(graph, startVertex);
		this.visitor.discoverVertex(graph, startVertex);
		
		for (int i = 0; i < numberOfWorker; i++) {
			checker.put(i, 0);
			todo.put(i, null);
		}

		Random random = new Random();
		int number = random.nextInt(numberOfWorker);
		System.out.println("The start vertex is copied in the index " + number);
		queue.offer(startVertex);
		todo.put(number, queue);
		checker.put(number, 1);

		for (int i = 0; i < numberOfWorker; i++) {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					workOnGraph(graph);
				}
			});
			thread.start();
		}
	}

	 private void workOnGraph(Graph<N, E> graph) {
		int ID;
		Queue<N> queue = new LinkedList<>();
		
		synchronized (this) {
			ID = getID();
			setID();
		}
		Thread.currentThread().setName("Thread with ID " + ID);
		
		while (true) {
			System.out.println(Thread.currentThread().getName() + " tries to work on Todo: " + todo.toString());
			while (!(checker.get(ID) > 0)) {
				try {
					System.out.println(Thread.currentThread().getName() + " can't work on Todo -> wait!!!");
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			N next;
			synchronized (this) {
				next = todo.get(ID).poll();
				checker.put(ID, checker.get(ID) - 1);
			}
			System.out.println(Thread.currentThread().getName() + " takes the next node in " + ID + " ->" + todo.toString());
			remember(next);
			for (E edge : graph.getEdges(next)) {
				N target = edge.getTarget();
				Random random = new Random();
				int number = random.nextInt(numberOfWorker);
				if (!alreadyWorked(target, ID)) {
					this.visitor.treeEdge(graph, edge);
					this.visitor.discoverVertex(graph, target);
					System.out.println("Edge: " + edge.toString() + " : Target: " + target.toString());
					
					synchronized (this) {
						queue.offer(target);
						todo.put(number, queue);
						checker.put(number, checker.get(number) + 1);
						System.out.println(Thread.currentThread().getName() + " puts in " + number + " -> " + todo.toString());
						notifyAll();
						System.out.println(Thread.currentThread().getName() + " finishs his work with the node -> notifyAll!!!");
					}

				} else {
					this.visitor.nonTreeEdge(graph, edge);
					System.out.println("Edge: " + edge.toString());
				}
			}

			// done processing the node => tell the visitor
			this.visitor.finishVertex(graph, next);
		}

	}

	synchronized private boolean alreadyWorked(N node, int ID) {
		if (!visited.contains(node) && !todo.get(ID).contains(node))
			return false;
		else
			return true;
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
