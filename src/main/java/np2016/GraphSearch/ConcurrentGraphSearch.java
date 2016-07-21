package np2016.GraphSearch;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import np2016.AtomicNumber;
import np2016.Blödsinn;
import np2016.Options;
import np2016.Watcher;
import np2016.WorkList;
import np2016.Graph.Edge;
import np2016.Graph.Graph;
import np2016.Graph.Node;

public class ConcurrentGraphSearch<N extends Node<?>, E extends Edge<N, ?>> extends BFSGraphSearch<N, E> {

	// stores all nodes visited so far
	private Set<N> visited;
	// stores all nodes that still need processing
	private WorkList<N> todo;
	private AtomicNumber activWorker;
	private int numberOfThreads;
	private Watcher watcher;
	private LinkedList<Thread> listOfThreads;

	public ConcurrentGraphSearch(BFSGraphVisitor<N, E> visitor) {
		super(visitor);
		activWorker = new AtomicNumber();
		watcher = new Watcher();
		visited = new HashSet<N>();
		todo = new WorkList<N>();
		listOfThreads = new LinkedList<>();
		numberOfThreads = Options.THREADS.getNumber();
	}

	@Override
	public void search(Graph<N, E> graph, N startVertex, Blödsinn blöd) {

		// handle the start node
		this.visitor.startVertex(graph, startVertex);
		this.visitor.discoverVertex(graph, startVertex);
		todo.offer(startVertex);

		for (int i = 0; i < numberOfThreads; i++) {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					workOnGraph(graph, blöd);
				}
			});
			thread.start();
		}

		Thread watcherThread = new Thread(new Runnable() {
			public void run() {
				while (!activWorker.check(0)) {
					try {
						synchronized (activWorker) {
							activWorker.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				interruptAllWorker(listOfThreads);

			}
		});
		watcherThread.start();

	}

	private void workOnGraph(Graph<N, E> graph, Blödsinn blöd) {
		listOfThreads.offer(Thread.currentThread());

		while (true) {
			while (todo.isEmpty()) {
				try {
					if (activWorker.check(0)) {
						synchronized (activWorker) {
							activWorker.notify();
						}
					}
					
					synchronized (todo) {
						watcher.setWatcher(true);
						todo.wait();
					}
				} catch (InterruptedException e) {
					synchronized (blöd) {
						blöd.notifyAll();
					}
				}
			}

			watcher.setWatcher(false);
			activWorker.increase();
			N next = (N) todo.poll();
			remember(next);

			if (next != null) {
				for (E edge : graph.getEdges(next)) {
					N target = edge.getTarget();

					if (!alreadyWorked(graph, edge)) {
						todo.offer(target);
						synchronized (todo) {
							todo.notify();
						}
					} else {
						this.visitor.nonTreeEdge(graph, edge);
					}

				}
			}
			// done processing the node => tell the visitor
			this.visitor.finishVertex(graph, next);
			activWorker.decrease();

		}

	}

	synchronized private boolean alreadyWorked(Graph<N, E> graph, E edge) {
		if (!visited.contains(edge.getTarget())) {
			visited.add(edge.getTarget());
			this.visitor.treeEdge(graph, edge);
			this.visitor.discoverVertex(graph, edge.getTarget());
			return false;
		}
		return true;
	}

	synchronized private void remember(N node) {
		visited.add(node);
	}
	
	synchronized private void interruptAllWorker(LinkedList<Thread> listOfThreads) {
		for (int i = 0; i < listOfThreads.size(); i++) {
			listOfThreads.get(i).interrupt();
		}
	}

}