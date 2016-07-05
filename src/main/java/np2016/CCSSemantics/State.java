package np2016.CCSSemantics;

import np2016.ASTNodes.CCSExpression;
import np2016.Graph.Node;

/**
 * States are concrete graph nodes. They are labeled with CCS expressions.
 *
 * @see CCSExpression
 * @see Node
 */
public final class State extends Node<CCSExpression> {

    /**
     * Constructs a state.
     * @param expr the (CCS expression) label of the state.
     */
    public State(final CCSExpression expr) {
        super(expr);
    }
}
