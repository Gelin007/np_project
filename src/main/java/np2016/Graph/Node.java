package np2016.Graph;

/**
 * Base class for all graph nodes. Nodes may be labeled with additional
 * information.
 *
 * @param <NodeInfo>
 *            type of the node label.
 */
public class Node<NodeInfo> {
    /**
     * Stores the node label.
     */
    protected final NodeInfo info;

    /**
     * Constructs a new node with the given label.
     *
     * @param info
     *            the node label.
     */
    public Node(final NodeInfo info) {
        this.info = info;
    }

    /**
     * Returns the node label.
     *
     * @return the node label.
     */
    public NodeInfo getInfo() {
        return this.info;
    }

    @Override
    public String toString() {
        return this.info.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }

        if (!(o instanceof Node<?>)) {
            return false;
        }

        Node<?> n = (Node<?>) o;

        return this.info.equals(n.info);
    }

    @Override
    public int hashCode() {
        return this.info.hashCode();
    }
}
