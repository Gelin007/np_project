package np2016.Diagnostic;

/**
 * Represents an object which can be located in the input program.
 */
public interface Locatable {

    /**
     * Returns the file name that corresponds to this location.
     *
     * @return the file name that corresponds to this location.
     */
    String getInputName();

    /**
     * Returns the line of this location in the input program.
     *
     * @return the line of this location in the input program.
     */
    int getLine();

    /**
     * Returns the column of this location in the input program.
     *
     * @return the column of this location in the input program.
     */
    int getColumn();
}
