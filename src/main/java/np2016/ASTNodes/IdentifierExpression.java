package np2016.ASTNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import np2016.CCSSemantics.CCSSemantics;
import np2016.CCSSemantics.Transition;
import np2016.Parser.CCSProgram;

/**
 * Representative for the identifier expression.
 * <p>
 * Example:
 * <ul>
 * <li>{@code X}</li>
 * </ul>
 */
public class IdentifierExpression extends CCSExpression {
    /**
     * The recursion variable name.
     */
    private final String text;

    /**
     * Constructs a identifier expression.
     *
     * @param text
     *            the recursion variable name.
     */
    public IdentifierExpression(final String text) {
        super();

        assert !(text.endsWith("?") || text.endsWith("!"));

        this.text = text;
    }

    @Override
    public List<Transition> computeTransitions(final CCSSemantics semantics) {
        CCSProgram program = semantics.getProgram();
        CCSExpression e = program.getBinding(this.text);

        if (e == null) {
            return new ArrayList<>();
        }

        List<Transition> transitions = e.computeTransitions(semantics).stream()
                .map(this.updateStartState.apply(semantics))
                .distinct()
                .collect(Collectors.toList());

        return transitions;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }

        if (!(o instanceof IdentifierExpression)) {
            return false;
        }

        IdentifierExpression e = (IdentifierExpression) o;

        return this.text.equals(e.text);
    }

    @Override
    public int hashCode() {
        return this.text.hashCode();
    }

    @Override
    public String toString() {
        return text;
    }
}
