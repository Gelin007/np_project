package np2016.Parser;

import np2016.ASTNodes.ASTFactory;
import np2016.ASTNodes.CCSExpression;
import np2016.Diagnostic.Diagnostic;
import np2016.Diagnostic.Locatable;

import java.util.HashSet;
import java.util.Set;

/**
 * Parses a given input CCS program and constructs an AST (abstract syntax
 * tree).
 */
public class Parser {
    /**
     * Helper for printing error and information messages related to positions
     * in the input program.
     *
     * @see Diagnostic
     */
    private final Diagnostic diagnostic;

    /**
     * Lexer for tokenizing the input file.
     */
    private final Lexer lexer;

    /**
     * AST factory for constructing the AST of the CCS program.
     */
    private final ASTFactory factory;

    /**
     * Stores the current token in the token sequence.
     */
    private Token token;

    /**
     * Stores the next token in the token sequence.
     */
    private Token lookAhead;

    /**
     * Constructs a parser for a CCS program.
     *
     * @param diagnostic
     *            the diagnostic module.
     * @param lexer
     *            the corresponding lexer.
     * @param factory
     *            the corresponding AST factory.
     */
    public Parser(final Diagnostic diagnostic, final Lexer lexer, final ASTFactory factory) {
        if (diagnostic == null || lexer == null || factory == null) throw new IllegalArgumentException();
        this.diagnostic = diagnostic;
        this.lexer = lexer;
        this.factory = factory;

        nextToken();
        nextToken();
    }

    /**
     * Reads the next token in the token sequence (i.e. the old look ahead is
     * the new current and a new look ahead is lexed).
     */
    private void nextToken() {
        token = lookAhead;
        lookAhead = lexer.next();
    }

    /**
     * Checks whether the current token has the given token type.
     *
     * @param type
     *            the questioned token type.
     * @return true if the current token has the given token type.
     */
    private boolean peek(final TokenType type) {
        return token.getType() == type;
    }

    /**
     * Checks whether the current token has the given token type and if it does
     * continues read the token sequence.
     *
     * @param type
     *            the questioned token type.
     * @return true if the current token has the given token type.
     */
    private boolean accept(final TokenType type) {
        if (peek(type)) {
            nextToken();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tries to match the specified token type with the current token. In case
     * this fails an error message is printed.
     *
     * @param type
     *            the questioned token type.
     */
    private void expect(final TokenType type) {
        if (!accept(type)) diagnostic.printError(token, "expected '%s', but got '%s'", type, token);
    }

    /**
     * Parses an entire CCS expression.
     *
     * @return the parsed CCS expression.
     */
    public CCSExpression parseCCSExpression() {
        CCSExpression exp1 = parseCCSChoiceExpression();
        while (peek(TokenType.PAR)) {
            nextToken();
            CCSExpression exp2 = parseCCSChoiceExpression();
            exp1 = factory.createParExpression(exp1, exp2);
        }
        return exp1;
    }

    /**
     * Parses a choice expression.
     *
     * @return the parsed choice expression.
     */
    private CCSExpression parseCCSChoiceExpression() {
        CCSExpression exp1 = parseCCSResExpression();
        while (peek(TokenType.CHOICE)) {
            nextToken();
            CCSExpression exp2 = parseCCSResExpression();
            exp1 = factory.createChoiceExpression(exp1, exp2);
        }
        return exp1;
    }

    /**
     * Parses a restriction expression.
     *
     * @return the parsed restriction expression.
     */
    private CCSExpression parseCCSResExpression() {
        CCSExpression exp = parseCCSBaseExpression();
        if (peek(TokenType.RES)) {
            Set<String> res = new HashSet<>();

            nextToken();
            expect(TokenType.BRACE_L);

            if (peek(TokenType.IDENTIFIER)) {
                res.add(token.getText());
                nextToken();
            }
            while (peek(TokenType.COMMA)) {
                nextToken();
                if (!peek(TokenType.IDENTIFIER)) {
                    diagnostic.printNote(token, "Expected another identifier.");
                } else {
                    res.add(token.getText());
                    nextToken();
                }
            }

            expect(TokenType.BRACE_R);

            exp = factory.createResExpression(exp, res);
        }
        return exp;
    }

    /**
     * Parses a ground expression (i.e. null, prefix, a recursion variable or an
     * entire bracketed CCS expression).
     *
     * @return the parsed ground expression.
     */
    private CCSExpression parseCCSBaseExpression() {
        final Locatable loc = token;
        CCSExpression result = null;
        switch (token.getType()) {
        case NULL:
            result = factory.createNullExpression();
            nextToken();
            break;
        case IDENTIFIER:
            String text = token.getText();
            if (lookAhead.getType() == TokenType.DOT) {
                nextToken();
                nextToken();
                CCSExpression exp = parseCCSBaseExpression();
                return factory.createPrefixExpression(text, exp);
            } else {
                result = factory.createIdentifierExpression(text);
                nextToken();
                break;
            }
        case PAREN_L:
            nextToken();
            result = parseCCSExpression();
            expect(TokenType.PAREN_R);
            break;
        default:
            diagnostic.printError(loc, "Syntax error, got '%s', expected something different.", loc.toString());
        }
        return result;
    }

    /**
     * Parses an entire CCS program (i.e. all recursion variable definitions and
     * the CCS expression itself).
     *
     * @return the parsed CCS program.
     */
    public CCSProgram parseCCSProgram() {
        CCSProgram program = new CCSProgram();

        while (lookAhead.getType() == TokenType.ASSIGN) {
            String identifier = token.getText();
            if (program.containsKey(identifier)) {
                diagnostic.printError(token, "already defined!");
            }

            nextToken();
            nextToken();

            CCSExpression exp = parseCCSExpression();
            expect(TokenType.NEWLINE);
            while (peek(TokenType.NEWLINE)) {
                nextToken();
            }

            program.addEval(identifier, exp);
        }

        CCSExpression exp = parseCCSExpression();
        program.addExp(exp);

        while (peek(TokenType.NEWLINE)) {
            nextToken();
        }
        if (!peek(TokenType.EOF)) {
            diagnostic.printError(token, "EOF expected, but got '%s'", token);
        }

        return program;
    }

}
