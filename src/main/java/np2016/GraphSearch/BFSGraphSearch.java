package np2016.GraphSearch;

import np2016.Graph.Edge;
import np2016.Graph.Node;

/**
 * Specifies an abstract BFS (breadth-first-search) based graph search. It
 * implements everything related to storing the {@link BFSGraphVisitor}.
 *
 * @param <N>
 *            the node type of the graph that is being searched.
 * @param <E>
 *            the edge type of the graph that is being searched.
 */
public abstract class BFSGraphSearch<N extends Node<?>, E extends Edge<N, ?>>
implements GraphSearch<N, E> {

    /**
     * Stores the visitor for later use during the search.
     */
    protected final BFSGraphVisitor<N, E> visitor;

    /**
     * Constructs a new BFS based graph search (the search is not started yet).
     *
     * @param visitor
     *            the visitor that should be used by the graph search.
     */
    protected BFSGraphSearch(final BFSGraphVisitor<N, E> visitor) {
        this.visitor = visitor;
    }
}
