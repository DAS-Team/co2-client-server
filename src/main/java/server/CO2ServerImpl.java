package server;

import client.CO2Client;
import client.ClientState;
import org.jfree.ui.ApplicationFrame;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CO2ServerImpl extends UnicastRemoteObject implements CO2Server {
    private final Map<Integer, Floor> floors;
    private final double FLOOR_WEIGHTING = 0.2; // alpha / beta in report
    private Map<Floor, List<FloorValueState>> prevFloorValueMap = new HashMap<>();
    private final Charter charter = new Charter("CO2 Chart");
    private final ExecutorService clientUpdaterService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


    public CO2ServerImpl() throws RemoteException {
        super();
        this.floors = new HashMap<>();
        ApplicationFrame panel = charter.render();
        panel.pack();
        panel.setVisible(true);
    }


    @Override
    public synchronized void subscribe(CO2Client client, ClientState initialClientState) throws RemoteException {
        System.out.println("Client with UUID: " + client.getUUID() + " subscribed for floor " + Integer.toString(client.getFloor()));
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

    /**
     * @return a {@link Map} from {@link Floor}s to the {@link FloorValueState}s of every floor from that one.
     */
    private Map<Floor, List<FloorValueState>> calcFloorValueMap(){
        Map<Floor, List<FloorValueState>> floorValueMap = new HashMap<>();

        for(Floor currentFloor: floors.values()){
            for(Floor newFloor: floors.values()){
                floorValueMap.putIfAbsent(currentFloor, new ArrayList<>());
                floorValueMap.get(currentFloor).add(new FloorValueState(newFloor, currentFloor.valueOfMovingTo(newFloor, FLOOR_WEIGHTING)));
            }
        }

        return floorValueMap;
    }

    /**
     * For any floor, we can construct a total ordering of the values to move to any floor (including itself).
     * This method returns true iff this total ordering has changed since {@link #publishIfStateChanged()} was last called.
     *
     * @param floor a {@link Floor} to check for total ordering changes on.
     * @param floorValueMap a {@link Map} from floors to its current {@link FloorValueState}s
     * @return true iff this total ordering has changed since {@link #publishIfStateChanged()} was last called.
     */
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
                // For now, send state continiously even if ordering hasn't changed
                .filter(e -> hasFloorValueOrderingChanged(e.getKey(), floorValueMap))
                .forEach(e -> {
                    List<CO2Client> floorClients = e.getKey().getClients();

                    for(CO2Client client: floorClients){
                        clientUpdaterService.submit(() -> {
                            try {
                                System.out.println(e.getKey().getFloorNum());
                                client.updateState(new FloorValueStates(e.getValue()));
                            } catch (RemoteException e1) {
                                System.out.println("Error updating client state");
                                e1.printStackTrace();
                            }
                        });
                    }


                });

        prevFloorValueMap = floorValueMap;
    }

    @Override
    public synchronized void receiveStateUpdate(ClientState newState) throws RemoteException {
        System.out.println("Client " + newState.getClientUuid() + " reading: " + newState.getPpm());
        floors.get(newState.getFloorNum()).addStateUpdate(newState);
        charter.addClientState(newState);
        publishIfStateChanged();
    }
}
