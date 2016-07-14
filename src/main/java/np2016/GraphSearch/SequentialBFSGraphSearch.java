package np2016.GraphSearch;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import np2016.Graph.Edge;
import np2016.Graph.Graph;
import np2016.Graph.Node;

/**
 * Sequential BFS (breadth-first-search) graph search implementation. Uses a
 * {@link BFSGraphVisitor} to tell the progress and happenings of the search.
 *
 * @param <N>
 *            the node type of the graph that is being searched.
 * @param <E>
 *            the edge type of the graph that is being searched.
 */
public final class SequentialBFSGraphSearch
<N extends Node<?>, E extends Edge<N, ?>>
extends BFSGraphSearch<N, E> {

    /**
     * Constructs a new sequential BFS graph search.
     *
     * @param visitor
     *            the visitor to be utilized by the search.
     */
    public SequentialBFSGraphSearch(final BFSGraphVisitor<N, E> visitor) {
        super(visitor);
    }

    @Override
    public void search(final Graph<N, E> graph, final N startVertex) {
        // stores all nodes visited so far
        final Set<N> visited = new HashSet<N>();
        // stores all nodes that still need processing
        final Queue<N> todo = new LinkedList<>();

        // handle the start node
        this.visitor.startVertex(graph, startVertex);
        this.visitor.discoverVertex(graph, startVertex);

        todo.offer(startVertex);

        // process nodes as long as there are nodes in the queue
        while (todo.peek() != null) {
            // get the first node in the queue and generate the outgoing edges
            // add the node to the visited set
            N next = todo.poll();
            visited.add(next);

            for (E edge : graph.getEdges(next)) {

                // check whether the reached state is already discovered (a node
                // counts as discovered if it is in the visited set or
                // the queue)
                N target = edge.getTarget();
                if (!visited.contains(target) && !todo.contains(target)) {
                    // not discovered => add to the queue,
                    // also tell the visitor (discovered node and tree edge)
                    this.visitor.treeEdge(graph, edge);
                    this.visitor.discoverVertex(graph, target);

                    todo.offer(target);
                } else {
                    // discovered => tell the visitor there is a non-tree edge
                    this.visitor.nonTreeEdge(graph, edge);
                }
            }

            // done processing the node => tell the visitor
            this.visitor.finishVertex(graph, next);
        }
    }

	@Override
	public boolean getWatcher() {
		return true;
	}
}
