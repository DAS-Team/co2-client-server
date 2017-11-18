package server;

import java.io.Serializable;

public class FloorValueState implements Serializable, Comparable<FloorValueState> {
    private final Floor currentFloor;
    private final Floor newFloor;
    private final double value;

    public FloorValueState(Floor currentFloor, Floor newFloor, double value) {
        this.currentFloor = currentFloor;
        this.newFloor = newFloor;
        this.value = value;
    }

    public Floor getCurrentFloor() {
        return currentFloor;
    }

    public Floor getNewFloor() {
        return newFloor;
    }

    public double getValue() {
        return value;
    }

    @Override
    public int compareTo(FloorValueState other) {
        return Double.compare(this.value, other.value);
    }
}
