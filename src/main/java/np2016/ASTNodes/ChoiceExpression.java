package np2016.ASTNodes;

import np2016.CCSSemantics.CCSSemantics;
import np2016.CCSSemantics.Transition;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Representative for the choice expression.
 * <p>
 * Examples:
 * <ul>
 * <li>{@code a.0 + b.0}</li>
 * <li>{@code a?.X + a!.Y}</li>
 * </ul>
 */
public class ChoiceExpression extends CCSExpression {
    /**
     * The left hand side expression.
     */
    private final CCSExpression exp1;

    /**
     * The right hand side expression.
     */
    private final CCSExpression exp2;

    /**
     * Constructs a choice expression.
     *
     * @param exp1
     *            the left hand side expression.
     * @param exp2
     *            the right hand side expression.
     */
    public ChoiceExpression(final CCSExpression exp1, final CCSExpression exp2) {
        super();
        this.exp1 = exp1;
        this.exp2 = exp2;
    }

    @Override
    public List<Transition> computeTransitions(final CCSSemantics semantics) {
        // compute all "choice_l" and "choice_r" premise transitions
        List<Transition> lhsTransitions = this.exp1.computeTransitions(semantics);
        List<Transition> rhsTransitions = this.exp2.computeTransitions(semantics);

        List<Transition> transitions;
        transitions = Stream.concat(
                lhsTransitions.stream().map(this.updateStartState.apply(semantics)),
                rhsTransitions.stream().map(this.updateStartState.apply(semantics))
                ).distinct().collect(Collectors.toList());

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

        if (!(o instanceof ChoiceExpression)) {
            return false;
        }

        ChoiceExpression e = (ChoiceExpression) o;

        return this.exp1.equals(e.exp1) && this.exp2.equals(e.exp2);
    }

    @Override
    public int hashCode() {
        return this.exp1.hashCode() ^ this.exp2.hashCode();
    }

    @Override
    public String toString() {
        return "(" + exp1.toString() + "+" + exp2.toString() + ")";
    }
}
