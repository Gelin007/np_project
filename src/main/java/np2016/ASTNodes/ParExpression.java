package np2016.ASTNodes;

import np2016.CCSSemantics.Action;
import np2016.CCSSemantics.CCSSemantics;
import np2016.CCSSemantics.State;
import np2016.CCSSemantics.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Representative for the parallel expression.
 * <p>
 * Examples:
 * <ul>
 * <li>{@code a.0 | b.0}</li>
 * <li>{@code a?.X | a!.Y}</li>
 * </ul>
 */
public class ParExpression extends CCSExpression {
    /**
     * The left hand side expression.
     */
    private final CCSExpression exp1;

    /**
     * The right hand side expression.
     */
    private final CCSExpression exp2;

    /**
     * Lambda expression for updating the start and target state of a
     * transition. This replaces the start state with a state that is labeled
     * with the current expression. Also replaces the target state with a state
     * that is labeled with {@code orig | exp2} where {@code orig} is the
     * original target state label.
     */
    private final Function<CCSSemantics, Function<Transition, Transition>>

    updateStatesParLeft;

    /**
     * Lambda expression for updating the start and target state of a
     * transition. This replaces the start state with a state that is labeled
     * with the current expression. Also replaces the target state with a state
     * that is labeled with {@code exp1 | orig} where {@code orig} is the
     * original target state label.
     */
    private final Function<CCSSemantics, Function<Transition, Transition>>

    updateStatesParRight;

    /**
     * Constructs a parallel expression.
     *
     * @param exp1
     *            the left hand side expression.
     * @param exp2
     *            the right hand side expression.
     */
    public ParExpression(final CCSExpression exp1, final CCSExpression exp2) {
        super();

        this.exp1 = exp1;
        this.exp2 = exp2;

        this.updateStatesParLeft = semantics -> t -> {
            CCSExpression target =
                    new ParExpression(t.getTarget().getInfo(), this.exp2);
            t.setStart(new State(this));
            t.setTarget(new State(target));
            return t;
        };

        this.updateStatesParRight = semantics -> t -> {
            CCSExpression target =
                    new ParExpression(this.exp1, t.getTarget().getInfo());
            t.setStart(new State(this));
            t.setTarget(new State(target));
            return t;
        };
    }

    @Override
    public List<Transition> computeTransitions(final CCSSemantics semantics) {
        // compute all "par_l" and "par_r" premise transitions
        List<Transition> lhsTransitions = this.exp1.computeTransitions(semantics);
        List<Transition> rhsTransitions = this.exp2.computeTransitions(semantics);

        // compute "sync" transitions
        List<Transition> taus = new ArrayList<>();
        for (Transition t1 : lhsTransitions) {
            for (Transition t2 : rhsTransitions) {
                Action a1 = t1.getInfo();
                Action a2 = t2.getInfo();

                if ((a1.isInput() && a2.isOutput() || a1.isOutput() && a2.isInput()) &&
                        a1.getName().equals(a2.getName())) {
                    CCSExpression e =
                            new ParExpression(
                                    t1.getTarget().getInfo(),
                                    t2.getTarget().getInfo()
                                    );

                    taus.add(new Transition(
                            new State(this),
                            new State(e),
                            new Action(a1.getName(), true)
                            ));
                }
            }
        }

        // update state labels for "par_l" and "par_r" transitions
        List<Transition> transitions;
        transitions = Stream.concat(
                taus.stream(),
                Stream.concat(
                        lhsTransitions.stream().map(this.updateStatesParLeft.apply(semantics)),
                        rhsTransitions.stream().map(this.updateStatesParRight.apply(semantics))
                        )).distinct().collect(Collectors.toList());

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

        if (!(o instanceof ParExpression)) {
            return false;
        }

        ParExpression e = (ParExpression) o;

        return this.exp1.equals(e.exp1) && this.exp2.equals(e.exp2);
    }

    @Override
    public int hashCode() {
        return this.exp1.hashCode() ^ this.exp2.hashCode();
    }

    @Override
    public String toString() {
        return "(" + exp1.toString() + "|" + exp2.toString() + ")";
    }
}
