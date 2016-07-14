package np2016.GraphSearch;

import np2016.CCSSemantics.LTS;
import np2016.CCSSemantics.State;
import np2016.CCSSemantics.Transition;
import np2016.Graph.Graph;

/**
 * The LTSBuilder is an abstract implementation of a BFS (breadth-first-search)
 * based graph search visitor. It specializes the BFS graph search visitor by
 * setting the node type to {@link State} and the edge type to
 * {@link Transition}. Additionally, it stores the LTS while its being
 * constructed.
 *
 * @see State
 * @see Transition
 */
public abstract class LTSBuilder implements BFSGraphVisitor<State, Transition> {
    /**
     * The constructed LTS.
     */
    protected LTS lts;

    /**
     * Returns the constructed LTS.
     *
     * @return the constructed LTS.
     */
    public LTS getLTS() {
        assert this.lts != null;
        return this.lts;
    }

    @Override
    public void startVertex(final Graph<State, Transition> graph,
            final State vertex) {
        // Do nothing. Override this method as needed.
    }

    @Override
    public void discoverVertex(final Graph<State, Transition> graph,
            final State state) {
        // Do nothing. Override this method as needed.
    }

    @Override
    public void finishVertex(final Graph<State, Transition> graph,
            final State state) {
        // Do nothing. Override this method as needed.
    }

    @Override
    public void nonTreeEdge(final Graph<State, Transition> graph,
            final Transition edge) {
        // Do nothing. Override this method as needed.
    }

    @Override
    public void treeEdge(final Graph<State, Transition> graph,
            final Transition edge) {
        // Do nothing. Override this method as needed.
    }
}
