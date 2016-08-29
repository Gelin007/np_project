package np2016.GraphSearch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import np2016.AtomicNumber;
import np2016.NonSense;
import np2016.Options;
import np2016.Watcher;
import np2016.WorkList;
import np2016.Graph.Edge;
import np2016.Graph.Graph;
import np2016.Graph.Node;

/**
 * Concurrent graph search implementation. Uses a {@link BFSGraphVisitor} to
 * tell the progress and happenings of the search.
 *
 * @param <N>
 *            the node type of the graph that is being searched.
 * @param <E>
 *            the edge type of the graph that is being searched.
 */
/**
 * @author TheMonster
 *
 * @param <N>
 * @param <E>
 */
public class ConcurrentGraphSearch<N extends Node<?>, E extends Edge<N, ?>> extends BFSGraphSearch<N, E> {

	/**
	 * stores all nodes visited so far.
	 */
	private Set<N> visited;

	/**
	 * an instance of Worklist.
	 */
	private WorkList<N> todo;

	/**
	 * an instance of AtomicNumber.
	 */
	private AtomicNumber activWorker;

	/**
	 * number of given Workers.
	 */
	private int numberOfThreads;

	/**
	 * an instance of Watcher.
	 */
	private Watcher watcher;

	
	/**
	 * an object that is used for synchronizing the watcher and the worker
	 * so that the watcher can check again if he can terminate.
	 */
	private Object lock1;
	
	
	/**
	 * an object that is used, for synchronizing between The workers
	 * when a worker is waiting of work, some worker will notify them.
	 */
	private Object lock2;

	/**
	 * ArrayList of references of all Workers. It will be used for the
	 * termination of them.
	 */
	private ArrayList<Thread> destroyer;

	/**
	 * @param visitor
	 */
	public ConcurrentGraphSearch(BFSGraphVisitor<N, E> visitor) {
		super(visitor);
		lock1 = new Object();
		lock2 = new Object();
		activWorker = new AtomicNumber();
		watcher = new Watcher();
		visited = new HashSet<N>();
		todo = new WorkList<N>();
		destroyer = new ArrayList<Thread>();
		numberOfThreads = Options.THREADS.getNumber();
	}

	@Override
	public void search(Graph<N, E> graph, N startVertex, NonSense nonsense) {

		// handle the start node
		this.visitor.startVertex(graph, startVertex);

		// store the start node in the worklist
		todo.offer(startVertex);

		// start as many Workers as the given number of Threads and add their
		// references in destroyer.
		for (int i = 0; i < numberOfThreads; i++) {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					workOnGraph(graph);
				}
			});
			destroyer.add(thread);
			thread.start();
		}

		// start a thread, which first checks if any Workers still work or the
		// worklist is not empty. If one of both conditions is true, the thread
		// waits for a notify of the last Worker, which is going to wait.
		// Else the work is finished and all Workers are waiting. Then this
		// thread interrupts all Workers, sets the value of watcher to true and
		// notifies the Main-thread.
		Thread watcherThread = new Thread(new Runnable() {
			public void run() {

				synchronized (lock1) {
					while (atomarCheck()) {
						try {
							lock1.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				for (int i = 0; i < destroyer.size(); i++) {
					destroyer.get(i).interrupt();
				}
				watcher.setWatcher(true);

				synchronized (nonsense) {
					nonsense.notify();
				}
			}
		});
		watcherThread.start();

	}

	/**
	 * 
	 * @param graph
	 *            Abstract graph representation
	 */
	private void workOnGraph(Graph<N, E> graph) {
		while (true) {
			// The Worker first checks, whether the worklist is empty. If it's
			// the fall, then he currently has nothing to do and waits for a
			// notify of any Worker, which add a node in the worklist.
			// Before he do it, he may send a notify to the Watcher-thread, iff
			// no task is available for some Worker.
			synchronized (lock2) {
				while (todo.isEmpty()) {
					try {
						synchronized (lock1) {
							if (activWorker.check()) {
								lock1.notify();
							}
						}
						lock2.wait();
					} catch (InterruptedException e) {
						return;
					}
				}
			}

			// increment the number of working thread.
			activWorker.increase();
			// get the first node in the worklist
			// add the node to the visited set
			N next = (N) todo.poll();
			remember(next);

			// make sure that next is not null
			if (next != null) {
				// generate the outgoing edges of the node next
				for (E edge : graph.getEdges(next)) {
					N target = edge.getTarget();
					// check whether the reached state is already discovered
					if (!alreadyWorked(graph, edge)) {
						// not discovered => add to the worklist
						todo.offer(target);
						// notify another waiting Worker that there is a
						// available task now but only the unlucky one (^_^)
						synchronized (lock2) {
							lock2.notify();
						}
					} else {
						// discovered => tell the visitor there is a non-tree
						// edge
						this.visitor.nonTreeEdge(graph, edge);
					}

				}
			}

			// the Worker has finished with the node, so we decrease the number
			// of working Thread
			activWorker.decrease();

		}

	}

	/**
	 * Checks, whether the target node is already contained in the visited Set.
	 * If yes return true, else add this one and call treeEdge.
	 * 
	 * @param graph
	 *            Abstract graph representation.
	 * @param edge
	 *            the edge type of the graph that is being searched.
	 * @return boolean
	 */
	synchronized private boolean alreadyWorked(Graph<N, E> graph, E edge) {
		if (!visited.contains(edge.getTarget())) {
			visited.add(edge.getTarget());
			this.visitor.treeEdge(graph, edge);
			return false;
		}
		return true;
	}

	/**
	 * Adds a node in the visited Set.
	 * 
	 * @param node
	 *            the node type of the graph that is being searched.
	 */
	synchronized private void remember(N node) {
		visited.add(node);
	}

	@Override
	public boolean getWatcher() {
		return watcher.getWatcher();
	}

	/**
	 * Checks atomarily, whether the number of working Threads is egal to 0 
	 * and todo is empty (We return the negated value).
	 *  
	 * @return boolean
	 */
	public boolean atomarCheck() {
		synchronized (todo) {
			synchronized (activWorker) {
				return !(activWorker.check() && todo.isEmpty());
			}
		}
	}

}