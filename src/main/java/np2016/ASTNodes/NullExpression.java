package np2016.ASTNodes;

import np2016.CCSSemantics.CCSSemantics;
import np2016.CCSSemantics.Transition;

import java.util.ArrayList;
import java.util.List;

/**
 * Representative for the null expression.
 */
public class NullExpression extends CCSExpression {

    @Override
    public List<Transition> computeTransitions(final CCSSemantics semantics) {
        return new ArrayList<>();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }

        if (!(o instanceof NullExpression)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "0";
    }
}
