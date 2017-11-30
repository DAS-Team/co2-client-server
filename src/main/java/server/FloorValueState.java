package server;

import java.io.Serializable;

/**
 * Immutable object representing the value of moving to a new floor at the time of construction.
 */
public class FloorValueState implements Serializable, Comparable<FloorValueState> {
    private final int newFloor;
    private final double value;

    public FloorValueState(Floor newFloor, double value) {
        this.newFloor = newFloor.getFloorNum();
        this.value = value;
    }

    public int getNewFloor() {
        return newFloor;
    }

    public double getValue() {
        return value;
    }

    @Override
    public int compareTo(FloorValueState other) {
        int vCompare = Double.compare(this.value, other.value);

        if(vCompare != 0){
            return vCompare;
        }
        else {
            return Integer.compare(this.newFloor, other.newFloor);
        }
    }

    @Override
    public String toString() {
        return "FloorValueState{" +
                "newFloor=" + newFloor +
                ", value="  + value +
                '}';
    }
}
