package np2016.GraphSearch;

import np2016.Graph.Edge;
import np2016.Graph.Graph;
import np2016.Graph.Node;

/**
 * Specifies an abstract BFS (breadth-first-search) based graph search visitor.
 *
 * @param <N>
 *            the node type of the graph that is being searched.
 * @param <E>
 *            the edge type of the graph that is being searched.
 */
public interface BFSGraphVisitor<N extends Node<?>, E extends Edge<N, ?>> {

    /**
     * Is called by the graph search for the starting node.
     *
     * @param graph
     *            the searched graph.
     * @param vertex
     *            the starting node.
     */
    void startVertex(Graph<N, E> graph, N vertex);

    /**
     * Is called by the graph search for every node immediately after its
     * <b>first</b> discovery.
     *
     * @param graph
     *            the searched graph.
     * @param vertex
     *            the discovered node.
     */
    void discoverVertex(Graph<N, E> graph, N vertex);

    /**
     * Is called by the graph search immediately after the node is fully
     * processed (i.e. all outgoing edges are generated and the thereby
     * reachable states are added to the working queue).
     *
     * @param graph
     *            the searched graph.
     * @param vertex
     *            the finished node.
     */
    void finishVertex(Graph<N, E> graph, N vertex);

    /**
     * Is called by the graph search for every edge for which the target node is
     * already discovered (hence the name "non-tree" since the target node must
     * be already explored before the edge is found the tree property of the
     * graph no longer holds).
     *
     * @param graph
     *            the searched graph.
     * @param edge
     *            the found edge.
     */
    void nonTreeEdge(Graph<N, E> graph, E edge);

    /**
     * Is called by the graph search for every edge for which the target node is
     * not already discovered. Hence the found edge spans a tree from the start
     * node of the edge.
     *
     * @param graph
     *            the searched graph.
     * @param edge
     *            the found edge.
     */
    void treeEdge(Graph<N, E> graph, E edge);
}
