package np2016.GraphSearch;

import np2016.Graph.Edge;
import np2016.Graph.Graph;
import np2016.Graph.Node;

/**
 * Specifies an abstract graph search. All a graph search is capable of is
 * searching a graph from a given start node.
 *
 * @param <N>
 *            the node type of the graph that is being searched.
 * @param <E>
 *            the edge type of the graph that is being searched.
 */
public interface GraphSearch<N extends Node<?>, E extends Edge<N, ?>> {
    /**
     * Performs the graph search from the given start node.
     *
     * @param graph
     *            the graph that should be searched.
     * @param startVertex
     *            the node from where the search should start.
     */
    void search(Graph<N, E> graph, N startVertex);
}
