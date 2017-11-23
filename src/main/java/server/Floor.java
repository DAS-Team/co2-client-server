package server;

import client.CO2Client;
import client.ClientState;

import java.rmi.RemoteException;
import java.util.*;

/**
 * A floor of a building, consisting of a number of CO2 sensors.
 */
public class Floor {
    private final Set<CO2Client> clients;
    private final int floorNum;
    private double prevC02Level = Double.POSITIVE_INFINITY;
    private final List<ClientState> stateUpdates;

    /**
     * @param floorNum the floor number in the building
     * @param clients the {@link CO2Client}s on that floor.
     */
    public Floor(int floorNum, Set<CO2Client> clients) {
        this.clients = clients;
        this.floorNum = floorNum;
        stateUpdates = new ArrayList<>();
    }

    /**
     * @param floorNum the floor number in the building
     */
    public Floor(int floorNum){
        this(floorNum, new HashSet<>());
    }

    /**
     * @param client a new client on this floor
     * @return true if the client wasn't already added to this {@link Floor}
     */
    public synchronized boolean addClient(CO2Client client){
        return clients.add(client);
    }

    /**
     * @param client the client to remove from this floor
     * @return true if the client was contained on this floor
     */
    public synchronized boolean removeClient(CO2Client client){
        return clients.remove(client);
    }

    public synchronized void addStateUpdate(ClientState newState){
        if(newState.getFloorNum() != floorNum){
            throw new IllegalStateException("Cannot add state for floor " + newState.getFloorNum() + " to Floor " + floorNum);
        }

        stateUpdates.add(newState);
    }

    /**
     * @return the exponentially smoothed average CO2 value of this floor, based on the previous average and any new state updates which
     *         occured since the last call.
     * @throws IllegalStateException if no states have been recorded yet.
     */
    public synchronized double averagePPM(){
        if(stateUpdates.isEmpty()){
            if(prevC02Level == Double.POSITIVE_INFINITY){
                throw new IllegalStateException("Cannot calculate average, no states seen yet");
            }

            return prevC02Level;
        }

        double averagePPM = stateUpdates.stream()
                .mapToDouble(ClientState::getPpm)
                .average()
                .orElse(prevC02Level);

        stateUpdates.clear();

        // https://en.wikipedia.org/wiki/Exponential_smoothing
        // Prevents the occasional massive spiking seen in testing by weighting the previous value more highly
        double alpha = 0.1;

        if(prevC02Level == Double.POSITIVE_INFINITY || averagePPM == Double.POSITIVE_INFINITY){
            prevC02Level = averagePPM;
        }
        else {
            prevC02Level = alpha * averagePPM + (1 - alpha) * prevC02Level;
        }

        return prevC02Level;
    }

    /**
     * @param otherFloor
     * @return number of floors you need to traverse to get to {@code otherFloor}
     */
    private int distanceToFloor(Floor otherFloor){
        return Math.abs(otherFloor.floorNum - this.floorNum);
    }

    /**
     * Value of moving from one floor to another, as described by the value function in the paper.
     * @param newFloor
     * @param weightingConstant alpha / beta in the paper
     * @return value of this floor move
     */
    public synchronized double valueOfMovingTo(Floor newFloor, double weightingConstant){
        if(newFloor.getFloorNum() == this.floorNum){
            return 0;
        }
        else {
            return weightingConstant * (this.averagePPM() - newFloor.averagePPM()) / distanceToFloor(newFloor);
        }
    }

    @Override
    public String toString() {
        return "Floor{" +
                "floorNum=" + floorNum +
                '}';
    }

    /**
     * Send new {@code floorValueStates} to client.
     * @param floorValueStates
     */
    public synchronized void publishFloorValueStates(Collection<FloorValueState> floorValueStates){
        for(CO2Client client: clients){
            try {
                client.updateState(floorValueStates);
            }
            catch(RemoteException e){
                System.err.println("Send to client failed");
                e.printStackTrace();
            }
        }
    }

    public int getFloorNum() {
        return floorNum;
    }

    /**
     * @return true iff there are no clients registered to this floor.
     */
    public boolean isEmpty(){
        return clients.isEmpty();
    }

}
