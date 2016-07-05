package np2016.CCSSemantics;

/**
 * Representation of actions. This includes internal (tau) actions as well as
 * communication actions. If an internal action is the result of a
 * synchronization the name of the synchronized action can be stored as well.
 */
public final class Action {
    /**
     * The name of the action.
     */
    private final String name;

    /**
     * Is this an input action?
     */
    private final boolean input;

    /**
     * Is this an output action?
     */
    private final boolean output;

    /**
     * Is this a tau action?
     */
    private final boolean weak;

    /**
     * Constructs a strong action (i.e. non tau action).
     *
     * @param action
     *            name of the action.
     */
    public Action(final String action) {
        this(action, false);
    }

    /**
     * Constructs an action. Depending on {@code weak} this will be a weak (tau)
     * or strong (non tau) action.
     *
     * @param action
     *            name of the action.
     * @param weak
     *            is this a weak (tau) action?
     */
    public Action(final String action, final boolean weak) {
        this.input = action.endsWith("?");
        this.output = action.endsWith("!");
        this.weak = weak;

        if (this.input || this.output) {
            assert !weak;
            this.name = action.substring(0, action.length() - 1);
        } else {
            this.name = action;
        }

        assert !(this.input && this.output);
    }

    /**
     * Returns the name of the action.
     *
     * @return the name of the action.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns true if the action is an input action.
     *
     * @return true if the action is an input action.
     */
    public boolean isInput() {
        return this.input;
    }

    /**
     * Returns true if the action is an output action.
     *
     * @return true if the action is an output action.
     */
    public boolean isOutput() {
        return this.output;
    }

    /**
     * Returns true if this a weak (i.e. tau) action.
     *
     * @return true if this a weak (i.e. tau) action.
     */
    public boolean isWeak() {
        return this.weak;
    }

    @Override
    public String toString() {
        if (this.weak) {
            return String.format("τ(%s)", this.name);
        } else {
            return String.format(
                    "%s%s%s",
                    this.name,
                    this.input ? "?" : "",
                            this.output ? "!" : ""
                    );
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }

        if (!(o instanceof Action)) {
            return false;
        }

        Action a = (Action) o;

        return this.name.equals(a.name) &&
                this.input == a.input &&
                this.output == a.output;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
