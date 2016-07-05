package np2016.ASTNodes;

import np2016.CCSSemantics.CCSSemantics;
import np2016.CCSSemantics.State;
import np2016.CCSSemantics.Transition;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Representative for the restriction expression.
 * <p>
 * Examples:
 * <ul>
 * <li>{@code (exp) \ {}}</li>
 * <li>{@code (exp) \ {a}}</li>
 * <li>{@code (exp) \ {a,b}}</li>
 * </ul>
 */
public class ResExpression extends CCSExpression {
    /**
     * The expression to be restricted.
     */
    private final CCSExpression exp;

    /**
     * The restricted actions.
     */
    private final Set<String> restrictedActions;

    /**
     * Lambda expression which verifies whether a given transition is influenced
     * by the set of restricted actions.
     * <p>
     * Examples:
     * <ul>
     * <li>This is influenced ({@code a} is in the set of restricted actions)
     *
     * <pre>
     * {@code (a.0) \ {a} -- a --> 0 \ {a}}
     * </pre>
     *
     * </li>
     * <li>This is not influenced ({@code b} is not in the set of restricted
     * actions)
     *
     * <pre>
     * {@code (b.0) \ {a} -- b --> 0 \ {b}}
     * </pre>
     *
     * </li>
     * <li>This is not influenced ({@code τ} cannot be in the set of restricted
     * actions)
     *
     * <pre>
     * {@code (τ.0) \ {a} -- τ --> 0 \ {a}}
     * </pre>
     *
     * </li>
     * </ul>
     */
    private final Predicate<Transition> isNotRestricted;

    /**
     * Lambda expression for updating the start and target state of a
     * transition. This replaces the start state with a state that is labeled
     * with the current expression. Also replaces the target state with a state
     * that is labeled with the original expression which is restricted by the
     * restricted actions of this expression.
     * <p>
     * Example:
     * <ul>
     * <li>Original transition
     *
     * <pre>
     * {@code a.0 -- a --> 0}
     * </pre>
     *
     * </li>
     * <li>Current expression
     *
     * <pre>
     * {@code (a.0) \ {b}}
     * </pre>
     *
     * </li>
     * <li>Original transition
     *
     * <pre>
     * {@code (a.0) \ {b} -- a --> (0) \ {b}}
     * </pre>
     *
     * </li>
     * </ul>
     */
    private final Function<CCSSemantics, Function<Transition, Transition>>

    updateStatesRes;

    /**
     * Constructs a restriction expression.
     *
     * @param exp
     *            the expression to be restricted.
     * @param restrictedActions
     *            the restricted actions.
     */
    public ResExpression(final CCSExpression exp,
            final Set<String> restrictedActions) {
        super();

        for (String action : restrictedActions) {
            assert !(action.endsWith("?") || action.endsWith("!"));
        }

        this.exp = exp;
        this.restrictedActions = restrictedActions;

        this.isNotRestricted = t -> t.getInfo().isWeak() ||
                !this.restrictedActions.contains(t.getInfo().getName());

        this.updateStatesRes = semantics -> t -> {
            CCSExpression target = new ResExpression(t.getTarget().getInfo(),
                    this.restrictedActions);
            t.setStart(new State(this));
            t.setTarget(new State(target));
            return t;
        };
    }

    @Override
    public List<Transition> computeTransitions(final CCSSemantics semantics) {
        List<Transition> transitions;
        // compute all transitions for the restricted expression
        transitions = this.exp.computeTransitions(semantics);

        // filter expressions which are labeled with actions that are restricted
        // and update state labels
        transitions = transitions.stream()
                .filter(this.isNotRestricted)
                .map(this.updateStatesRes.apply(semantics))
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

        if (!(o instanceof ResExpression)) {
            return false;
        }

        ResExpression e = (ResExpression) o;

        return this.exp.equals(e.exp) &&
                this.restrictedActions.equals(e.restrictedActions);
    }

    @Override
    public int hashCode() {
        return this.exp.hashCode() ^ this.restrictedActions.hashCode();
    }

    @Override
    public String toString() {
        return "(" + exp.toString() + "\\" +
                restrictedActions.toString().replace("[", "{").replace("]", "}") +
                ")";
    }
}
