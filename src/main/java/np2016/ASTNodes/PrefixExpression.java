package np2016.ASTNodes;

import np2016.CCSSemantics.Action;
import np2016.CCSSemantics.CCSSemantics;
import np2016.CCSSemantics.State;
import np2016.CCSSemantics.Transition;
import np2016.Options;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Representative for the prefix expression.
 * <p>
 * Examples:
 * <ul>
 * <li>{@code a.0}</li>
 * <li>{@code a?.a!.0}</li>
 * </ul>
 */
public class PrefixExpression extends CCSExpression {
    /**
     * The action name (including "?" or "!").
     */
    private final String text;

    /**
     * The prefixed expression.
     */
    private final CCSExpression exp;

    /**
     * Constructs a prefix expression.
     * @param text the prefix action name.
     * @param exp the prefixed expression.
     */
    public PrefixExpression(final String text, final CCSExpression exp) {
        super();
        this.text = text;
        this.exp = exp;
    }

    @Override
    public List<Transition> computeTransitions(final CCSSemantics semantics) {
        List<Transition> transitions;
        transitions = new ArrayList<>();

        // just one transition as this is a prefix expression
        transitions.add(
                new Transition(
                        new State(this),
                        new State(this.exp),
                        new Action(this.text)
                        )
                );

        // account for the "--delay" option
        IntStream primes = IntStream
                .iterate(2, i -> i + 1)
                .filter(
                        x -> IntStream
                        .rangeClosed(2, (int) Math.sqrt(x))
                        .allMatch(y -> x % y != 0)
                        )
                .limit(Options.DELAY.getNumber());

        if (primes.count() != Options.DELAY.getNumber()) {
            throw new IllegalStateException();
        }

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

        if (!(o instanceof PrefixExpression)) {
            return false;
        }

        PrefixExpression e = (PrefixExpression) o;

        return this.text.equals(e.text) && this.exp.equals(e.exp);
    }

    @Override
    public int hashCode() {
        return this.text.hashCode() ^ this.exp.hashCode();
    }

    @Override
    public String toString() {
        return text + "." + exp.toString();
    }
}
