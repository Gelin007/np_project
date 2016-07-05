package np2016.Diagnostic;

/**
 * Basic diagnostic interface for error as well as information messages.
 */
public interface Diagnostic {

    /**
     * Reports an error to the user which was caused at the given location.
     *
     * @param location
     *            the location at which the error was reported.
     * @param fmt
     *            a format string for the error message.
     * @param args
     *            given format arguments for the message.
     */
    void printError(Locatable location, String fmt, Object... args);


    /**
     * Reports an information message to the user which was caused at the given
     * location.
     *
     * @param location
     *            the location at which the note was reported.
     * @param fmt
     *            a format string for the note message.
     * @param args
     *            given format arguments for the message.
     */
    void printNote(Locatable location, String fmt, Object... args);
}
