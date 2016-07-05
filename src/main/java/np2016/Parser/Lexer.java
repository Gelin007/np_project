package np2016.Parser;

import np2016.Diagnostic.Diagnostic;
import np2016.Diagnostic.ModifiableLocation;

import java.io.IOException;
import java.io.Reader;

/**
 * Lexes the given input file and generates a token sequence from it.
 */
public class Lexer {

    /**
     * Helper for printing error and information messages related to positions
     * in the input program.
     *
     * @see Diagnostic
     */
    private final Diagnostic diagnostic;

    /**
     * Used to read the input characters.
     */
    private Reader reader;

    /**
     * Current program location.
     */
    private final ModifiableLocation currentLocation;

    /**
     * Start location of the current token.
     */
    private final ModifiableLocation startLocation;

    /**
     * Buffer for identifier.
     */
    private StringBuilder id = new StringBuilder();

    /**
     * Starting character.
     */
    private int c = '\n';

    /**
     * Constructs a lexer for a CCS program.
     *
     * @param diagnostic
     *            the diagnostic module.
     * @param reader
     *            the input file.
     * @param fileName
     *            the input file name.
     */
    public Lexer(final Diagnostic diagnostic, final Reader reader, final String fileName) {
        if (reader == null || diagnostic == null) {
            throw new IllegalArgumentException();
        }

        this.diagnostic = diagnostic;
        this.reader = reader;
        this.currentLocation = new ModifiableLocation(fileName);
        this.startLocation = new ModifiableLocation(fileName);

        //Delete the first newline immediately.
        this.next();
    }

    /**
     * Reads the next character from the input sequence.
     */
    private void nextChar() {
        currentLocation.incColumn();
        try {
            c = reader.read();
        } catch (final IOException e) {
            diagnostic.printError(currentLocation, "%s", e);
            c = -1;
        }
    }

    /**
     * Tries to read the next character from the input sequence but only if it
     * matches the given one.
     *
     * @param character
     *            the character to match.
     * @return true if the match was successful.
     */
    public boolean acceptChar(final int character) {
        if (c == character) {
            nextChar();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Reads a newline from the input file. It handles all common line endings.
     */
    private void consumeNewline() {
        acceptChar('\r');
        acceptChar('\n');
        currentLocation.incLine();
    }

    /**
     * Constructs a token of the given type.
     *
     * @param type
     *            the token type of the new token.
     * @return the new token.
     */
    private Token makeToken(final TokenType type) {
        nextChar();
        return new Token(startLocation, type);
    }

    /**
     * Decides what token should be created by looking at the current character
     * and takes action accordingly.
     *
     * @return the constructed token.
     */
    public Token next() {
        for (;;) {
            startLocation.set(currentLocation);
            switch (c) {
            case -1:
                return new Token(startLocation, TokenType.EOF);

            case '\r':
            case '\n':
                consumeNewline();
                return new Token(startLocation, TokenType.NEWLINE);

            case '\13':
            case '\f':
            case '\t':
            case ' ':
                break;

            case ':':
                nextChar();
                if (acceptChar('=')) {
                    return new Token(startLocation, TokenType.ASSIGN);
                } else {
                    diagnostic.printError(startLocation, "invalid char following ':' : '%c'", c);
                    break;
                }

            case '(':
                return makeToken(TokenType.PAREN_L);
            case ')':
                return makeToken(TokenType.PAREN_R);
            case '0':
                return makeToken(TokenType.NULL);
            case '+':
                return makeToken(TokenType.CHOICE);
            case '|':
                return makeToken(TokenType.PAR);
            case '{':
                return makeToken(TokenType.BRACE_L);
            case '}':
                return makeToken(TokenType.BRACE_R);
            case '\\':
                return makeToken(TokenType.RES);
            case '.':
                return makeToken(TokenType.DOT);
            case ',':
                return makeToken(TokenType.COMMA);

            default:
                if (Character.isJavaIdentifierStart(c)) {
                    do {
                        id.append((char) c);
                        nextChar();
                    } while (Character.isJavaIdentifierPart(c) ||
                            c == '?' ||
                            c == '!');
                    final String text = id.toString();
                    id.setLength(0);

                    return new Token(startLocation, TokenType.IDENTIFIER, text);
                } else {
                    diagnostic.printError(startLocation, "invalid input character '%c'", c);
                }
            }
            nextChar();
        }
    }
}
