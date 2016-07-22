package np2016.CCSSemantics;

import np2016.ASTNodes.CCSExpression;
import np2016.Graph.Graph;
import np2016.Parser.CCSProgram;

import java.util.ArrayList;
import java.util.List;

/**
 * Implicit graph representation of the CCS semantics. Utilizes the "Post"
 * function of the CCS expressions to implement its successor method.
 */
public final class CCSSemantics implements Graph<State, Transition> {
    /**
     * The parsed CCS program.
     */
    private final CCSProgram program;

    /**
     * Constructs a CCS semantics object.
     *
     * @param program
     *            the parsed CCS program.
     */
    public CCSSemantics(final CCSProgram program) {
        assert program != null;

        this.program = program;
    }

    /**
     * Returns the CCS program on which this CCS semantics is based.
     *
     * @return the CCS program on which this CCS semantics is based.
     */
    public CCSProgram getProgram() {
        return this.program;
    }

    @Override
    public List<State> getSources() {
        List<State> sources = new ArrayList<>();

        CCSExpression e = this.program.getExpression();
        sources.add(new State(e));

        return sources;
    }

    @Override
    public List<Transition> getEdges(final State state) {
        assert state != null;
        assert state.getInfo() != null;

        return state.getInfo().computeTransitions(this);
    }
}
