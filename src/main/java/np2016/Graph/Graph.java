package np2016.Graph;

import java.util.List;

/**
 * Abstract graph representation.
 * @param <N> type of nodes.
 * @param <E> type of edges.
 */
public interface Graph<N extends Node<?>, E extends Edge<N, ?>> {

    /**
     * Returns a list of all source nodes. These are nodes where a graph search
     * starts.
     *
     * @return a list of all source nodes.
     */
    List<N> getSources();

    /**
     * Returns a list of all outgoing edges for the given node.
     *
     * @param node
     *            the node for which the edges should be returned.
     * @return a list of all outgoing edges for the given node.
     */
    List<E> getEdges(N node);
}
