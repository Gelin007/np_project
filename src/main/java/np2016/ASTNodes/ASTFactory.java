package np2016.ASTNodes;

import np2016.Diagnostic.Diagnostic;

import java.util.HashSet;
import java.util.Set;

/**
 * Factory for constructing the nodes of the AST (abstract syntax tree).
 */
public class ASTFactory {

    /**
     * Helper for printing error and information messages related to positions
     * in the input program.
     *
     * @see Diagnostic
     */
    private final Diagnostic diagnostic;

    /**
     * Stores all encountered recursion variable names.
     */
    private final Set<String> identifiers = new HashSet<>();

    /**
     * Stores all encountered action names (i.e. the alphabet of the CCS
     * program).
     */
    private final Set<String> alphabet = new HashSet<>();

    /**
     * Stores all identifiers that the user was warned about being both an
     * action and a recursion variable.
     */
    private final Set<String> warnedIdentifiers = new HashSet<>();

    /**
     * Constructs an AST factory.
     *
     * @param diagnostic
     *            the diagnostic module for error reporting.
     */
    public ASTFactory(final Diagnostic diagnostic) {
        this.diagnostic = diagnostic;
    }

    /**
     * Add an identifier to the set of actions. Print a warning if this
     * identifier is also a recursion variable. Only warn a user once for every
     * identifier that is used in a potentially erroneous way.
     *
     * @param name
     *            the identifier of the action to add
     */
    private void addAction(final String name) {
        this.alphabet.add(name);

        if (this.identifiers.contains(name) &&
                !warnedIdentifiers.contains(name)) {
            warnedIdentifiers.add(name);
            diagnostic.printNote(
                    null,
                    "'%s' is used both for recursion and as an action. " +
                            "This might cause unintended behavior.",
                            name
                    );
        }
    }

    /**
     * Add an identifier to the set of recursion variables. Print a warning if
     * this identifier is also a recursion variable. Only warn a user once for
     * every identifier that is used in a potentially erroneous way.
     *
     * @param name
     *            the identifier of the recursion variable to add
     */
    private void addIdentifier(final String name) {
        this.identifiers.add(name);

        if (this.alphabet.contains(name) &&
                !warnedIdentifiers.contains(name)) {
            warnedIdentifiers.add(name);
            diagnostic.printNote(
                    null,
                    "'%s' is used both for recursion and as an action. " +
                            "This might cause unintended behavior.",
                            name
                    );
        }
    }

    /**
     * Constructs a new parallel expression (i.e. {@code exp1 | exp2}).
     *
     * @param exp1
     *            the left hand side expression.
     * @param exp2
     *            the right hand side expression.
     * @return the parallel expression consisting of {@code exp1} and
     *         {@code exp2}.
     */
    public CCSExpression createParExpression(final CCSExpression exp1,
            final CCSExpression exp2) {
        return new ParExpression(exp1, exp2);
    }

    /**
     * Constructs a new choice expression (i.e. {@code exp1 + exp2}).
     *
     * @param exp1
     *            the left hand side expression.
     * @param exp2
     *            the right hand side expression.
     * @return the choice expression consisting of {@code exp1} and
     *         {@code exp2}.
     */
    public CCSExpression createChoiceExpression(final CCSExpression exp1,
            final CCSExpression exp2) {
        return new ChoiceExpression(exp1, exp2);
    }

    /**
     * Constructs a new restriction expression (i.e. {@code (exp) \ {actions}}).
     *
     * @param exp
     *            the expression to be restricted.
     * @param restrictedActions
     *            the actions to be restricted.
     * @return the restriction expression.
     */
    public CCSExpression createResExpression(final CCSExpression exp,
            final Set<String> restrictedActions) {
        for (String action : restrictedActions) {
            addAction(action);
        }
        return new ResExpression(exp, restrictedActions);
    }

    /**
     * Constructs a new null expression (i.e. {@code 0}).
     *
     * @return the null expression.
     */
    public CCSExpression createNullExpression() {
        return new NullExpression();
    }

    /**
     * Constructs a new identifier expression (i.e. {@code X} where {@code X} is
     * a recursion variable). This should not be confused with action names as
     * they are stored inside of prefix expressions.
     *
     * @param text
     *            the identifier name.
     * @return the identifier expression.
     */
    public CCSExpression createIdentifierExpression(final String text) {
        addIdentifier(text);
        return new IdentifierExpression(text);
    }

    /**
     * Constructs a prefix expression (i.e. {@code a.exp}).
     *
     * @param text
     *            the action name (prefix name).
     * @param exp
     *            the prefixed expression.
     * @return the prefix expression.
     */
    public CCSExpression createPrefixExpression(final String text,
            final CCSExpression exp) {
        addAction(text);
        return new PrefixExpression(text, exp);
    }

}
