package np2016.Parser;

/**
 * Lists all possible types of tokens.
 */
public enum TokenType {
    /**
     * Identifier token such as "a" or "X".
     */
    IDENTIFIER ("<id>"),

    /**
     * End of file token.
     */
    EOF        ("EOF"),

    /**
     * Token for indicating a new line.
     */
    NEWLINE    ("NEWLINE"),

    /**
     * Token for the opening parentheses.
     */
    PAREN_L    ("("),

    /**
     * Token for the closing parentheses.
     */
    PAREN_R    (")"),

    /**
     * Token for the null process.
     */
    NULL       ("0"),

    /**
     * Token for the choice operator.
     */
    CHOICE     ("+"),

    /**
     * Token for the parallel operator.
     */
    PAR        ("|"),

    /**
     * Token for the restriction operator.
     */
    RES        ("\\"),

    /**
     * Token for the opening brace.
     */
    BRACE_L    ("{"),

    /**
     * Token for the closing brace.
     */
    BRACE_R    ("}"),

    /**
     * Token for the prefix operator.
     */
    DOT        ("."),

    /**
     * Token for separating actions inside the restriction set.
     */
    COMMA      (","),

    /**
     * Token for the assignment operator.
     */
    ASSIGN     (":=");

    /**
     * Stores the string representation of the token type.
     */
    private final String printstr;

    /**
     * Constructs a new token type.
     *
     * @param printstr
     *            the string representation of the token type.
     */
    TokenType(final String printstr) {
        this.printstr = printstr;
    }

    @Override
    public String toString() {
        return this.printstr;
    }
}
