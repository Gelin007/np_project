package np2016.GraphSearch;

import np2016.CCSSemantics.LTS;
import np2016.CCSSemantics.State;
import np2016.CCSSemantics.Transition;
import np2016.Graph.Graph;

/**
 * Concurrent implementation of the {@link LTSBuilder}.
 *
 * @see LTSBuilder
 */
public class ConcurrentLTSBuilder extends LTSBuilder {

	@Override
	synchronized public void startVertex(final Graph<State, Transition> graph, final State state) {
		this.lts = new LTS(state);
	}

	@Override
	synchronized public void nonTreeEdge(final Graph<State, Transition> graph, final Transition transition) {
		this.lts.addTransition(transition);
	}

	@Override
	synchronized public void treeEdge(final Graph<State, Transition> graph, final Transition transition) {
		this.lts.addState(transition.getTarget());
		this.lts.addTransition(transition);
	}

	@Override
	synchronized public void discoverVertex(Graph<State, Transition> graph, State state) {
		// TODO Auto-generated method stub
	}

	@Override
	synchronized public void finishVertex(Graph<State, Transition> graph, State state) {
		// TODO Auto-generated method stub
	}

}
