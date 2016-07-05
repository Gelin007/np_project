package np2016.Parser;

import np2016.Diagnostic.Locatable;
import np2016.Diagnostic.Location;

/**
 * A token is a location with additional information such as a type and some
 * text.
 */
public class Token extends Location {

    /**
     * Stores the type of the token.
     */
    private final TokenType type;

    /**
     * Stores the text of the token.
     */
    private final String text;

    /**
     * Constructs a token at the given location with the given type.
     *
     * @param loc
     *            the program location.
     * @param type
     *            the type of the token.
     */
    public Token(final Locatable loc, final TokenType type) {
        this(loc, type, "");
    }

    /**
     * Constructs a token at the given location with the given type and text.
     *
     * @param loc
     *            the program location.
     * @param type
     *            the type of the token.
     * @param text
     *            the text of the token.
     */
    public Token(final Locatable loc, final TokenType type, final String text) {
        super(loc);
        this.type = type;
        this.text = text;
    }

    @Override
    public String toString() {
        return super.toString() + " " + type.toString() + " " + text;
    }

    /**
     * Returns the type of the token.
     *
     * @return the type of the token.
     */
    public TokenType getType() {
        return type;
    }

    /**
     * Returns the text of the token.
     *
     * @return the text of the token.
     */
    public String getText() {
        return text;
    }

}
