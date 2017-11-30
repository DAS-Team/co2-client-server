package server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Wrapper for a collection of {@link FloorValueState}s, including an incrementing identifier for synchronisation.
 */
public class FloorValueStates implements Serializable {
    private static final AtomicLong currentId = new AtomicLong(Long.MIN_VALUE);
    private final long id;
    private final List<FloorValueState> states;

    public FloorValueStates(SortedSet<FloorValueState> states) {
        this.states = new ArrayList<>(states);
        this.id = currentId.getAndIncrement();
    }

    public List<FloorValueState> getSortedStates() {
        return states;
    }

    public long getId() {
        return id;
    }

    public boolean isEmpty(){
        return states.isEmpty();
    }

    public int size(){
        return states.size();
    }

    @Override
    public String toString() {
        return "FloorValueStates{" +
                "states=" + states +
                '}';
    }
}
