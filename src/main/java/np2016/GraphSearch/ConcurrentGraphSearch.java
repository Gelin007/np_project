package np2016.GraphSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import np2016.NumberOfWork;
import np2016.Blodsinn;
import np2016.Watcher;
import np2016.Options;
import np2016.Graph.Edge;
import np2016.Graph.Graph;
import np2016.Graph.Node;

public class ConcurrentGraphSearch<N extends Node<?>, E extends Edge<N, ?>> extends BFSGraphSearch<N, E> {

	// stores all nodes visited so far
	private Set<N> visited;
	// stores all nodes that still need processing
	private HashMap<Integer, Queue<N>> todo;
	private NumberOfWork numberOfWork;
	private int myID;
	private int numberOfThreads;
	private Watcher watcher;

	public ConcurrentGraphSearch(BFSGraphVisitor<N, E> visitor) {
		super(visitor);
		myID = 0;
		watcher=new Watcher();
		visited = new HashSet<N>();
		todo = new HashMap<>();
		numberOfThreads = Options.THREADS.getNumber();
	}

	@Override
	public void search(Graph<N, E> graph, N startVertex, Blodsinn blöd) {
		Queue<N> queue = new LinkedList<>();

		// handle the start node
		this.visitor.startVertex(graph, startVertex);
		this.visitor.discoverVertex(graph, startVertex);

		// Initialisation
		numberOfWork=new NumberOfWork(numberOfThreads);
		for (int i = 0; i < numberOfThreads; i++) {
//			numberOfWork.add(i, 0);
			todo.put(i, new LinkedList<>());
		}

		Random random = new Random();
		int number = random.nextInt(numberOfThreads);

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
		
		Thread watcherThread = new Thread(new Runnable() {
			public void run() {
				while (!numberOfWork.isWorkFinished()) {
					try {
						synchronized (numberOfWork) {
							numberOfWork.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				watcher.setWatcher(true);
				synchronized (blöd) {
					blöd.notifyAll();
				}
			}
		});
		watcherThread.start();

	}

	private void workOnGraph(Graph<N, E> graph) {
		int ID;

		synchronized (this) {
			ID = getID();
			setID();
		}

		Thread.currentThread().setName("Thread with ID " + ID);

		while (true) {
			while (isTodoEmpty(ID)) {
				try {
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
						numberOfWork.increase(number);
						notifyAll();
					}
				}
			}

			// done processing the node => tell the visitor
			this.visitor.finishVertex(graph, next);
			synchronized (numberOfWork) {
				numberOfWork.decrease(ID);
				numberOfWork.notifyAll();
			}

		}

	}

	synchronized private boolean alreadyWorked(Graph<N, E> graph, E edge) {
		if (!visited.contains(edge.getTarget())) {
			remember(edge.getTarget());
			this.visitor.treeEdge(graph, edge);
			this.visitor.discoverVertex(graph, edge.getTarget());
			return false;
		} else {
			this.visitor.nonTreeEdge(graph, edge);
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

	


	

	

	synchronized private boolean isTodoEmpty(int ID) {
		if (todo.get(ID).isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean getWatcher() {
		// TODO Auto-generated method stub
		return watcher.getWatcher();
	}
}
