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

public class ConcurrentGraphSearch<N extends Node<?>, E extends Edge<N, ?>> extends BFSGraphSearch<N, E> {

	// stores all nodes visited so far
	final Set<N> visited;
	// stores all nodes that still need processing
	final HashMap<Integer, Queue<N>> todo;
	final ArrayList<Integer> numberOfWork;
	private int myID;
	private int numberOfWorker;
	private boolean watcher;

	public ConcurrentGraphSearch(BFSGraphVisitor<N, E> visitor) {
		super(visitor);
		myID = 0;
		watcher = false;
		visited = new HashSet<N>();
		todo = new HashMap<>();
		numberOfWork = new ArrayList<>();
		numberOfWorker = Options.THREADS.getNumber();
	}

	@Override
	public void search(Graph<N, E> graph, N startVertex) {
		Queue<N> queue = new LinkedList<>();

		// handle the start node
		this.visitor.startVertex(graph, startVertex);
		this.visitor.discoverVertex(graph, startVertex);

		for (int i = 0; i < numberOfWorker; i++) {
			numberOfWork.add(i, 0);
			todo.put(i, new LinkedList<>());
		}

		Random random = new Random();
		int number = random.nextInt(numberOfWorker);
		System.out.println("The start vertex is copied in the index " + number);

		queue.offer(startVertex);
		todo.put(number, queue);
		numberOfWork.set(number, 1);

		for (int i = 0; i < numberOfWorker; i++) {
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
			System.out.println("Number of work for Threads -> " + numberOfWork.toString());
			while (!(numberOfWork.get(ID) > 0)) {
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
				next = todo.get(ID).remove();
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
				int number = random.nextInt(numberOfWorker);
				if (!alreadyWorked(target)) {
					this.visitor.treeEdge(graph, edge);
					this.visitor.discoverVertex(graph, target);
					System.out.println("Edge: " + edge.toString() + " : Target: " + target.toString());
					remember(target);
					todo.get(ID).offer(target);

					synchronized (this) {
						todo.put(number, todo.get(ID));
						numberOfWork.set(number, numberOfWork.get(number) + 1);
						System.out.println(Thread.currentThread().getName() + " puts in " + number + " -> " + todo.toString());
						System.out.println(numberOfWork.toString());
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

			synchronized (numberOfWork) {
				numberOfWork.set(ID, numberOfWork.get(ID) - 1);
				numberOfWork.notifyAll();
				System.out.println(numberOfWork.toString());
			}
		}

	}

	synchronized private boolean alreadyWorked(N node) {
		if (!visited.contains(node))
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
}
