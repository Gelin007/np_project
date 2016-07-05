package np2016.CCSSemantics;

import np2016.Graph.Edge;

/**
 * Transitions are concrete graph edges. They are labeled with actions and go
 * from state to state.
 *
 * @see Action
 * @see Transition
 * @see Edge
 */
public final class Transition extends Edge<State, Action> {

    /**
     * Constructs a transition.
     *
     * @param from
     *            the start of the transition.
     * @param to
     *            the target of the transition.
     * @param action
     *            the action that labels the transition.
     */
    public Transition(final State from, final State to, final Action action) {
        super(action, from, to);
    }
}
