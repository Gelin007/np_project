package np2016.GraphSearch;

import np2016.CCSSemantics.LTS;
import np2016.CCSSemantics.State;
import np2016.CCSSemantics.Transition;
import np2016.Graph.Graph;

/**
 * Sequential implementation of the {@link LTSBuilder}.
 *
 * @see LTSBuilder
 */
public final class SequentialLTSBuilder extends LTSBuilder {

    @Override
    public void startVertex(final Graph<State, Transition> graph,
            final State state) {
        this.lts = new LTS(state);
    }

    @Override
    public void nonTreeEdge(final Graph<State, Transition> graph,
            final Transition transition) {
        this.lts.addTransition(transition);
    }

    @Override
    public void treeEdge(final Graph<State, Transition> graph,
            final Transition transition) {
        this.lts.addState(transition.getTarget());
        this.lts.addTransition(transition);
    }
}
