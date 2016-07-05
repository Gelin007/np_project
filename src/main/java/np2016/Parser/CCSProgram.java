package np2016.Parser;

import np2016.ASTNodes.CCSExpression;

import java.util.HashMap;
import java.util.Map;

/**
 * Representation of an entire CCS program. This involves the recursion variable
 * definitions as well as the global CCS expression.
 */
public class CCSProgram {
    /**
     * Stores the recursion variable definitions.
     */
    private final Map<String, CCSExpression> evaluation = new HashMap<>();

    /**
     * Stores the global CCS expression.
     */
    private CCSExpression exp = null;

    /**
     * Adds a new recursion variable definition.
     *
     * @param identifier
     *            name of the recursion variable.
     * @param newExp
     *            corresponding CCS expression.
     */
    public void addEval(final String identifier, final CCSExpression newExp) {
        this.evaluation.put(identifier, newExp);
    }

    /**
     * Updates the global CCS expression by replacing it with the given one.
     *
     * @param newExp
     *            the new global CCS expression.
     */
    public void addExp(final CCSExpression newExp) {
        this.exp = newExp;
    }

    /**
     * Returns the global CCS expression.
     * @return the global CCS expression.
     */
    public CCSExpression getExpression() {
        assert this.exp != null;

        return this.exp;
    }

    /**
     * Returns true if the given recursion variable is defined.
     *
     * @param name
     *            the name of the recursion variable.
     * @return true if the given recursion variable is defined.
     */
    public boolean containsKey(final String name) {
        return evaluation.containsKey(name);
    }

    /**
     * Returns the CCS expression corresponding to the given recursion variable.
     *
     * @param name
     *            the name of the recursion variable.
     * @return the CCS expression corresponding to the given recursion variable.
     */
    public CCSExpression getBinding(final String name) {
        return this.evaluation.get(name);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (String key : evaluation.keySet()) {
            b.append(key.toString() + " := " + evaluation.get(key).toString() + "\n");
        }
        b.append(exp.toString());
        return b.toString();
    }
}
