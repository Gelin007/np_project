package np2016.GraphSearch;

import java.util.ArrayList;
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

public class ConcurrentGraphSearchWithSysOut<N extends Node<?>, E extends Edge<N, ?>> extends BFSGraphSearch<N, E> {

	// stores all nodes visited so far
	final Set<N> visited;
	// stores all nodes that still need processing
	final HashMap<Integer, Queue<N>> todo;
	final ArrayList<Integer> numberOfWork;
	private int myID;
	private int numberOfThreads;
	private boolean watcher;

	public ConcurrentGraphSearchWithSysOut(BFSGraphVisitor<N, E> visitor) {
		super(visitor);
		myID = 0;
		watcher = false;
		visited = new HashSet<N>();
		todo = new HashMap<>();
		numberOfWork = new ArrayList<>();
		numberOfThreads = Options.THREADS.getNumber();
	}

	@Override
	public void search(Graph<N, E> graph, N startVertex) {
		Queue<N> queue = new LinkedList<>();

		// handle the start node
		this.visitor.startVertex(graph, startVertex);
		this.visitor.discoverVertex(graph, startVertex);

		// Initialisation
		for (int i = 0; i < numberOfThreads; i++) {
			numberOfWork.add(i, 0);
			todo.put(i, new LinkedList<>());
		}

		Random random = new Random();
		int number = random.nextInt(numberOfThreads);
		System.out.println("The start vertex is copied in the index " + number);

		queue.offer(startVertex);
		todo.put(number, queue);
		numberOfWork.set(number, 1);

		for (int i = 0; i < numberOfThreads; i++) {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					workOnGraph(graph);
				}
			});
			thread.start();
		}

		Thread t = new Thread(new Runnable() {
			public void run() {
				while (!isWorkFinished()) {
					try {
						System.out.println("Terminate -> wait!!!");
						synchronized (numberOfWork) {
							numberOfWork.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				setWatcher(true);
				System.out.println("The Watcher terminate now!!!");

			}
		});
		t.start();
	}

	private void workOnGraph(Graph<N, E> graph) {
		int ID;

		synchronized (this) {
			ID = getID();
			setID();
		}

		Thread.currentThread().setName("Thread with ID " + ID);

		while (!watcher) {
			synchronized (this) {
				System.out.println(Thread.currentThread().getName() + " tries to work on Todo: " + todo.toString());
			}
			synchronized (this) { 
				System.out.println("Number of work for Threads -> " + numberOfWork.toString());
			}
			while (isTodoEmpty(ID)) {
				try {
					System.out.println(Thread.currentThread().getName() + " can't work on Todo -> wait!!!");
					synchronized (numberOfWork) {
						numberOfWork.notify();
					}
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
			}
			synchronized (this) {
				System.out.println(Thread.currentThread().getName() + " takes the next node in " + ID + " -> " + todo.toString());
			}
			remember(next);
			
			for (E edge : graph.getEdges(next)) {
				N target;
				synchronized (this) {
					target = edge.getTarget();
				}
				
				Random random = new Random();
				int number;
				number = random.nextInt(numberOfThreads);
				
				if (!alreadyWorked(graph, edge)) {
					synchronized (this) {
						todo.get(number).offer(target);
						todo.put(number, todo.get(number));
						numberOfWork.set(number, numberOfWork.get(number) + 1);
						System.out.println(Thread.currentThread().getName() + " puts in " + number + " -> " + todo.toString());
						System.out.println(Thread.currentThread().getName() + " -> " + numberOfWork.toString());
						notifyAll();
						System.out.println(Thread.currentThread().getName() + " finishs to work the target : " + target.toString() + " -> notifyAll!!!");
					}
				}
			}

			// done processing the node => tell the visitor
			this.visitor.finishVertex(graph, next);

			synchronized (numberOfWork) {
				numberOfWork.set(ID, numberOfWork.get(ID) - 1);
				numberOfWork.notifyAll();
				System.out.println(Thread.currentThread().getName() + " finishs his work -> " + numberOfWork.toString() + " -> notify the Watcher");
			}
		}

	}

	synchronized private boolean alreadyWorked(Graph<N, E> graph, E edge) {
		if (!visited.contains(edge.getTarget())) {
			remember(edge.getTarget());
			this.visitor.treeEdge(graph, edge);
			this.visitor.discoverVertex(graph, edge.getTarget());
			System.out.println(Thread.currentThread().getName() + " -> Edge: " + edge.toString() + " : Target: " + edge.getTarget().toString());
			return false;
		}
		else {
			this.visitor.nonTreeEdge(graph, edge);
			System.out.println(Thread.currentThread().getName() + " -> Edge: " + edge.toString());
			return true;
		}

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

	@Override
	synchronized public boolean getWatcher() {
		return watcher;
	}

	private boolean isWorkFinished() {
		boolean check = true;
		for (int i = 0; i < numberOfWork.size(); i++) {
			if (numberOfWork.get(i) != 0)
				check = false;
		}
		return check;
	}

	private void setWatcher(boolean observer) {
		this.watcher = observer;
	}
	
	synchronized private boolean isTodoEmpty(int ID) {
		if (todo.get(ID).isEmpty()) {
			return true;
		}
		return false;
	}
}
