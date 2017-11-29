package server;

import client.CO2Client;
import client.ClientState;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CO2ServerImpl extends UnicastRemoteObject implements CO2Server {
    private final Map<Integer, Floor> floors;
    private final double FLOOR_WEIGHTING = 0.5; // alpha / beta in report
    private Map<Floor, SortedSet<FloorValueState>> prevFloorValueMap = new HashMap<>();
    //private final Charter charter = new Charter("CO2 Chart");
    private Executor clientUpdaterService;
    private final List<Duration> timeDeltas = new ArrayList<>();

    public CO2ServerImpl() throws RemoteException {
        super();
        this.floors = new HashMap<>();
        //ApplicationFrame panel = charter.render();
        //panel.pack();
        //panel.setVisible(true);
        init(0);
    }

    /**
     * @param numThreads if > 0, updates sent out to clients will be sent by a threadpool with this many threads.
     * @throws RemoteException
     */
    public CO2ServerImpl(int numThreads) throws RemoteException {
        this();
        init(numThreads);
    }

    private void init(int numThreads){
        if(numThreads <= 0){
            clientUpdaterService = Runnable::run;
        }
        else {
            clientUpdaterService = Executors.newFixedThreadPool(numThreads);
        }
    }


    @Override
    public synchronized void subscribe(CO2Client client, ClientState initialClientState) throws RemoteException {
        System.out.println("Client with UUID: " + client.getUUID() + " subscribed for floor " + Integer.toString(client.getFloor()));
        floors.putIfAbsent(client.getFloor(), new Floor(client.getFloor()));
        floors.get(client.getFloor()).addClient(client);

        client.setReady(true);

        receiveStateUpdate(initialClientState);
    }

    @Override
    public synchronized void unsubscribe(CO2Client client) throws RemoteException {
        System.out.println("Client with UUID: " + client.getUUID() + " unsubscribed");
        floors.putIfAbsent(client.getFloor(), new Floor(client.getFloor()));
        floors.get(client.getFloor()).removeClient(client);

        client.setReady(false);

        if(floors.get(client.getFloor()).isEmpty()){
            floors.remove(client.getFloor());
        }
    }

    /**
     * @return a {@link Map} from {@link Floor}s to the {@link FloorValueState}s of every floor from that one.
     */
    private Map<Floor, SortedSet<FloorValueState>> calcFloorValueMap(){
        Map<Floor, SortedSet<FloorValueState>> floorValueMap = new HashMap<>();

        for(Floor currentFloor: floors.values()){
            for(Floor newFloor: floors.values()){
                floorValueMap.putIfAbsent(currentFloor, new TreeSet<>());
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
     * @param floorValues {@code floor}'s current {@link FloorValueState}s
     * @return true iff this total ordering has changed since {@link #publishIfStateChanged()} was last called.
     */
    private boolean hasFloorValueOrderingChanged(Floor floor, SortedSet<FloorValueState> floorValues){
        SortedSet<FloorValueState> sortedPrevStateSet = prevFloorValueMap.getOrDefault(floor, null);

        if(sortedPrevStateSet == null){
            return true;
        }

        if(sortedPrevStateSet.size() != floorValues.size()){
            return true;
        }

        Iterator<FloorValueState> it1 = sortedPrevStateSet.iterator();
        Iterator<FloorValueState> it2 = floorValues.iterator();

        while(it1.hasNext() && it2.hasNext()){
            if(it1.next().getNewFloor() != it2.next().getNewFloor()){
                return true;
            }
        }

        return false;
    }

    private void publishIfStateChanged() throws RemoteException {
        if(floors.isEmpty()){
            return;
        }

        Map<Floor, SortedSet<FloorValueState>> floorValueMap;

        synchronized (this) {
            floorValueMap = calcFloorValueMap();
        }

        floorValueMap
                .entrySet()
                .parallelStream()
                .filter(e -> hasFloorValueOrderingChanged(e.getKey(), e.getValue()))
                .forEach(e -> {
                    List<CO2Client> floorClients = e.getKey().getClients();
                    FloorValueStates floorValueState = new FloorValueStates(e.getValue());

                    floorClients.forEach(client -> clientUpdaterService.execute(() -> {
                        try {
                            client.updateState(floorValueState);
                        } catch (RemoteException e1) {
                            System.out.println("Error updating client state");
                        }
                    }));

                });

        prevFloorValueMap = floorValueMap;
    }

    @Override
    public synchronized void receiveStateUpdate(ClientState newState) throws RemoteException {
        System.out.println("Client " + newState.getClientUuid() + " reading: " + newState.getPpm());

        floors.get(newState.getFloorNum()).addStateUpdate(newState);

        //charter.addClientState(newState);

        publishIfStateChanged();
        timeDeltas.add(Duration.between(newState.getTimestamp(), Instant.now()));


        System.out.println("Avg: " + timeDeltas.stream().map(Duration::toMillis).mapToDouble(d -> d).average());


        System.out.println(Duration.between(newState.getTimestamp(), Instant.now()));
    }
}
