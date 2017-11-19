package server;

import java.io.Serializable;

public class FloorValueState implements Serializable, Comparable<FloorValueState> {
    private final int currentFloor;
    private final int newFloor;
    private final double value;

    public FloorValueState(Floor currentFloor, Floor newFloor, double value) {
        this.currentFloor = currentFloor.getFloorNum();
        this.newFloor = newFloor.getFloorNum();
        this.value = value;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getNewFloor() {
        return newFloor;
    }

    public double getValue() {
        return value;
    }

    @Override
    public int compareTo(FloorValueState other) {
        return Double.compare(this.value, other.value);
    }

    @Override
    public String toString() {
        return "FloorValueState{" +
                "currentFloor=" + currentFloor +
                ", newFloor=" + newFloor +
                ", value=" + value +
                '}';
    }
}
