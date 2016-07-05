package np2016.Diagnostic;

/**
 * A location in the context of a source program.
 */
public class Location implements Locatable {

    /**
     * Name of the input file.
     */
    private final String inputName;

    /**
     * Line in the input file.
     */
    private final int line;

    /**
     * Column in the input file.
     */
    private final int column;

    /**
     * Initializes a new location with the given parameters.
     *
     * @param inputName
     *            the input file name for this location.
     * @param line
     *            the line number of this location.
     * @param column
     *            the column number of this location.
     */
    public Location(final String inputName, final int line, final int column) {
        if (inputName == null || line < 0 || column < 0) throw new IllegalArgumentException();
        this.inputName = inputName;
        this.line = line;
        this.column = column;
    }

    /**
     * Initializes a new location with the given input file name.
     *
     * @param inputName
     *            the input file name for this location.
     */
    public Location(final String inputName) {
        this(inputName, 0, 0);
    }

    /**
     * Initializes the location with the given location.
     *
     * @param location
     *            the location to copy the data from.
     */
    public Location(final Locatable location) {
        this(location.getInputName(), location.getLine(), location.getColumn());
    }

    /**
     * Stringyfy the location.
     *
     * @param l
     *            the location which is to be stringyfied.
     * @return the string representation of the given location.
     */
    public static String toString(final Locatable l) {
        return l.getInputName() + ':' + l.getLine() + ':' + l.getColumn();
    }

    @Override
    public final String getInputName() {
        return inputName;
    }

    @Override
    public final int getLine() {
        return line;
    }

    @Override
    public final int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return inputName + ":" + line + ":" + column;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location location = (Location) o;

        if (line != location.line) return false;
        if (column != location.column) return false;
        return inputName.equals(location.inputName);

    }

    @Override
    public int hashCode() {
        return this.inputName.hashCode() ^ this.line ^ this.column;
    }
}
