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
    
    public Floor(int floorNum, Set<CO2Client> clients) {
        this.clients = clients;
        this.floorNum = floorNum;
        stateUpdates = new ArrayList<>();
    }

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
    
    public synchronized double averagePPM(){
        if(stateUpdates.isEmpty()){
            return prevC02Level;
        }

        double averagePPM = stateUpdates.stream()
                .mapToDouble(ClientState::getPpm)
                .average()
                .orElse(prevC02Level);

        stateUpdates.clear();

        if(prevC02Level == Double.POSITIVE_INFINITY || averagePPM == Double.POSITIVE_INFINITY){
            prevC02Level = averagePPM;
        }
        else {
            prevC02Level = (averagePPM + prevC02Level) / 2;
        }

        return prevC02Level;
    }

    private int distanceToFloor(Floor otherFloor){
        return Math.abs(otherFloor.floorNum - this.floorNum);
    }

    public synchronized double valueOfMovingTo(Floor newFloor, double weightingConstant){
        if(newFloor.getFloorNum() == this.floorNum){
            return 0;
        }
        else {
            return weightingConstant * (this.averagePPM() - newFloor.averagePPM()) / distanceToFloor(newFloor);
        }
    }

    public synchronized void publishFloorValueStates(Collection<FloorValueState> floorValueStates){
        for(CO2Client client: clients){

            try {
                client.updateState(floorValueStates);
            }
            catch(RemoteException e){
                System.err.println("Send to client failed");
            }
        }
    }

    public int getFloorNum() {
        return floorNum;
    }

    public Set<CO2Client> getClients(){
        return new HashSet<>(clients);
    }

}
