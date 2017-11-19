package server;

import client.CO2Client;
import client.ClientState;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class CO2ServerImpl extends UnicastRemoteObject implements CO2Server {
    private final Map<Integer, Floor> floors;
    private final double FLOOR_WEIGHTING = 0.5; // alpha / beta in report
    private Map<Floor, List<FloorValueState>> prevFloorValueMap = new HashMap<>();


    public CO2ServerImpl() throws RemoteException {
        super();
        this.floors = new HashMap<>();
    }


    @Override
    public synchronized void subscribe(CO2Client client, ClientState initialClientState) throws RemoteException {
        System.out.println("Client with UUID: " + client.getUUID() + " subscribed");
        floors.putIfAbsent(client.getFloor(), new Floor(client.getFloor()));
        floors.get(client.getFloor()).addClient(client);
        receiveStateUpdate(initialClientState);
    }

    @Override
    public synchronized void unsubscribe(CO2Client client) throws RemoteException {
        System.out.println("Client with UUID: " + client.getUUID() + " unsubscribed");
        floors.putIfAbsent(client.getFloor(), new Floor(client.getFloor()));
        floors.get(client.getFloor()).removeClient(client);

        if(floors.get(client.getFloor()).isEmpty()){
            floors.remove(client.getFloor());
        }
    }

    private Map<Floor, List<FloorValueState>> calcFloorValueMap(){
        Map<Floor, List<FloorValueState>> floorValueMap = new HashMap<>();

        for(Floor currentFloor: floors.values()){
            for(Floor newFloor: floors.values()){
                floorValueMap.putIfAbsent(currentFloor, new ArrayList<>());
                floorValueMap.get(currentFloor).add(new FloorValueState(currentFloor, newFloor, currentFloor.valueOfMovingTo(newFloor, FLOOR_WEIGHTING)));
            }
        }

        return floorValueMap;
    }

    private boolean hasFloorValueOrderingChanged(Floor floor, Map<Floor, List<FloorValueState>> floorValueMap){
        List<FloorValueState> sortedNewStateSet = floorValueMap.get(floor);
        List<FloorValueState> sortedPrevStateSet = prevFloorValueMap.getOrDefault(floor,  null);

        if(sortedPrevStateSet == null){
            return true;
        }

        if(sortedNewStateSet.size() != sortedPrevStateSet.size()){
            return true;
        }

        Collections.sort(sortedNewStateSet);
        Collections.sort(sortedPrevStateSet);

        for(int i = 0; i < sortedNewStateSet.size(); ++i){
            if(sortedNewStateSet.get(i).getNewFloor() != sortedPrevStateSet.get(i).getNewFloor()){
                return true;
            }
        }

        return false;
    }

    @Override
    public synchronized void publishIfStateChanged() throws RemoteException {
        if(floors.isEmpty()){
            return;
        }

        Map<Floor, List<FloorValueState>> floorValueMap = calcFloorValueMap();

        floorValueMap
                .entrySet()
                .stream()
                .filter(e -> hasFloorValueOrderingChanged(e.getKey(), floorValueMap))
                .forEach(e -> e.getKey().publishFloorValueStates(e.getValue()));

        prevFloorValueMap = floorValueMap;
    }

    @Override
    public synchronized void receiveStateUpdate(ClientState newState) throws RemoteException {
        System.out.println("Received new state, PPM: " + newState.getPpm() + " from client with UUID: " + newState.getClientUuid());
        floors.get(newState.getFloorNum()).addStateUpdate(newState);
        publishIfStateChanged();
    }
}
