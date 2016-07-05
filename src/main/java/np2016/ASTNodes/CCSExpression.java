package np2016.ASTNodes;

import np2016.CCSSemantics.CCSSemantics;
import np2016.CCSSemantics.State;
import np2016.CCSSemantics.Transition;

import java.util.List;
import java.util.function.Function;

/**
 * This is the abstract base class for CCS expressions.
 */
public abstract class CCSExpression {
    /**
     * Lambda expression for updating the start state of a transition. This
     * replaces the start state with a state that is labeled with the current
     * expression.
     *
     * @see State
     * @see Transition
     */
    protected Function<CCSSemantics, Function<Transition, Transition>>

    updateStartState = semantics -> t -> {
        t.setStart(new State(this));
        return t;
    };

    /**
     * Computes a list of all outgoing transitions. This is the "Post" function
     * of a CCS expression.
     *
     * @param semantics
     *            used to look up recursion variables.
     * @return a list of outgoing transitions.
     *
     * @see Transition
     * @see CCSSemantics
     */
    public abstract List<Transition> computeTransitions(CCSSemantics semantics);
}
