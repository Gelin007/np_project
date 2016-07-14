package np2016.Graph;

/**
 * Base class for all graph edges. Edges may be labeled with additional
 * information.
 *
 * @param <N>
 *            type of the nodes connected by the edge.
 * @param <EdgeInfo>
 *            type of the edge label.
 */
public class Edge<N extends Node<?>, EdgeInfo> {
    /**
     * Stores the edge label.
     */
    protected EdgeInfo info;

    /**
     * Stores the start node.
     */
    protected N from;

    /**
     * Stores the target node.
     */
    protected N to;

    /**
     * Constructs a new edge label with the given information starting from
     * {@code from} to {@code to}.
     *
     * @param info
     *            the edge label.
     * @param from
     *            the start node.
     * @param to
     *            the target node.
     */
    public Edge(final EdgeInfo info, final N from, final N to) {
        assert from != null && to != null;

        this.info = info;
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the edge label.
     *
     * @return the edge label.
     */
    public EdgeInfo getInfo() {
        return this.info;
    }

    /**
     * Returns the start node.
     *
     * @return the start node.
     */
    public N getStart() {
        return this.from;
    }

    /**
     * Returns the target node.
     *
     * @return the target node.
     */
    synchronized public N getTarget() {
        return this.to;
    }

    /**
     * Updates the edge label with the given information.
     *
     * @param info
     *            the new edge label.
     */
    public void setInfo(final EdgeInfo info) {
        this.info = info;
    }

    /**
     * Updates the edge start node.
     *
     * @param start
     *            the new start node.
     */
    public void setStart(final N start) {
        assert start != null;

        this.from = start;
    }

    /**
     * Updates the edge target node.
     *
     * @param target
     *            the new target node.
     */
    public void setTarget(final N target) {
        assert target != null;

        this.to = target;
    }

    @Override
    public String toString() {
        return String.format(
                "%s -- %s --> %s",
                this.from.toString(),
                this.info.toString(),
                this.to.toString()
                );
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge<?, ?> edge = (Edge<?, ?>) o;

        if (info != null ? !info.equals(edge.info) : edge.info != null) return false;
        if (!from.equals(edge.from)) return false;
        return to.equals(edge.to);
    }

    @Override
    public int hashCode() {
        int result = this.info != null ? this.info.hashCode() : 0;
        result ^= this.from.hashCode() ^ this.to.hashCode();
        return result;
    }
}
