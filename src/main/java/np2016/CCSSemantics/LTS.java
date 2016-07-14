package np2016.CCSSemantics;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import np2016.Graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Explicit graph representation of a CCS program. To be precise: only the
 * reachable part is stored.
 */
public class LTS implements Graph<State, Transition> {
    /**
     * Stores states and transitions for states. A state resides in the graph if
     * and only if there is a key for it. For each such state the list of
     * outgoing transitions is stored as the value in the mapping.
     */
    private final Map<State, List<Transition>> edges;

    /**
     * Stores the initial state.
     */
    private final State initialState;

    /**
     * Constructs an LTS (explicit graph representation of a CCS program).
     *
     * @param initialState
     *            the initial state of the LTS.
     */
    public LTS(final State initialState) {
        assert initialState != null;

        this.edges = new ConcurrentHashMap<>();

        this.addState(initialState);
        this.initialState = initialState;
    }

    /**
     * Returns the initial state.
     *
     * @return the initial state.
     */
    public State getInitialState() {
        return this.initialState;
    }

    /**
     * Checks whether the given state is part of the LTS.
     *
     * @param state
     *            the questioned state.
     * @return true if the state is in the LTS.
     */
    public boolean existsState(final State state) {
        assert state != null;

        return this.edges.containsKey(state);
    }

    /**
     * Checks whether the given transition is part of the LTS.
     *
     * @param transition
     *            the questioned transition.
     * @return true if the transition is in the LTS.
     */
    public boolean existsTransition(final Transition transition) {
        assert transition != null;

        State start = transition.getStart();
        State target = transition.getTarget();

        if (!this.edges.containsKey(start) || !this.edges.containsKey(target)) {
            return false;
        }

        List<Transition> transitions = this.edges.get(start);

        return transitions.contains(transition);
    }

    /**
     * Adds the given state to the LTS. Before any transition is added this
     * state will not have any outgoing transitions.
     * <p>
     * The given state should not already be in the LTS.
     *
     * @param state
     *            the state to be added.
     */
    public void addState(final State state) {
        assert state != null;
        assert !this.edges.containsKey(state);

        State newState = new State(state.getInfo());

        this.edges.put(newState, new CopyOnWriteArrayList<>());
    }

    /**
     * Adds the given transitions to the LTS. The start and target state of the
     * transition should already be in the LTS.
     * <p>
     * The given transition should not already be in the LTS.
     *
     * @param transition
     *            the transition to be added.
     */
    public void addTransition(final Transition transition) {
        assert transition != null;

        State start = transition.getStart();
        State target = transition.getTarget();

        assert this.edges.containsKey(start) && this.edges.containsKey(target);

        List<Transition> transitions = this.edges.get(start);

        assert !transitions.contains(transition);

        Transition newTransition = new Transition(
                start,
                target,
                transition.getInfo()
                );

        transitions.add(newTransition);
    }

    @Override
    public List<State> getSources() {
        List<State> s = new ArrayList<>();
        s.add(this.initialState);
        return s;
    }

    @Override
    public List<Transition> getEdges(final State state) {
        assert state != null && this.edges.containsKey(state);

        return new ArrayList<>(this.edges.get(state));
    }

    /**
     * Constructs the JSON object that corresponds to the current state of the
     * LTS.
     *
     * @return the JSON object representation of the LTS.
     */
    public JsonObject toJSON() {
        JsonObject lts = new JsonObject();

        lts.addProperty("initialState", this.initialState.toString());

        JsonObject states = new JsonObject();
        for (Map.Entry<State, List<Transition>> entry : this.edges.entrySet()) {
            JsonArray array = new JsonArray();
            for (Transition t : entry.getValue()) {
                JsonObject transition = new JsonObject();
                Action a = t.getInfo();

                if (a.isWeak()) {
                    transition.addProperty("weak", true);
                    transition.addProperty("detailsLabel", a.getName());
                } else {
                    transition.addProperty("label", a.toString());
                    transition.addProperty("detailsLabel", false);
                }

                transition.addProperty("target", t.getTarget().toString());
                array.add(transition);
            }

            JsonObject transitions = new JsonObject();
            transitions.add("transitions", array);
            states.add(entry.getKey().toString(), transitions);
        }
        lts.add("states", states);

        return lts;
    }
}
