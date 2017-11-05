package server;

import client.CO2Client;
import client.ClientState;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.function.Supplier;

public class CO2ServerImpl extends UnicastRemoteObject implements CO2Server {
    private final Set<CO2Client> clients;

    /* Contains the ClientStates received since the last publish()
     * This way we could also calculate the variance if we want.
     * Could be useful for displaying uncertainty in our results.
     */
    private final Map<UUID, List<ClientState>> statesReceived;

    public CO2ServerImpl() throws RemoteException {
        super();
        this.clients = new HashSet<>();
        this.statesReceived = new HashMap<>();
    }


    @Override
    public synchronized void subscribe(CO2Client client) throws RemoteException {
        System.out.println("Client with UUID: " + client.getUUID() + " subscribed");
        clients.add(client);
    }

    @Override
    public synchronized void unsubscribe(CO2Client client) throws RemoteException {
        System.out.println("Client with UUID: " + client.getUUID() + " unsubscribed");
        clients.remove(client);
    }

    private synchronized List<ClientState> calculateSmoothedClientStateList(){
        List<ClientState> smoothedStates = new ArrayList<>();

        for(Map.Entry<UUID, List<ClientState>> entry : statesReceived.entrySet()){
            int floorNum;

            // If there are no client states, there's no point in sending them anyway so just continue.
            if(entry.getValue().isEmpty()){
                continue;
            }
            else {
                floorNum = entry.getValue().get(0).getFloorNum();
            }

            double averagePpm = entry.getValue().stream()
                    .mapToDouble(ClientState::getPpm)
                    .average()
                    .orElseThrow(() -> new IllegalStateException("List of states is empty"));

            /*
                double ppmVariance = MathUtils.variance(averagePpm, entry.getValue()
                    .stream()
                    .map(ClientState::getPpm)
                    .collect(Collectors.toList()));
            */

            ClientState smoothedState = new ClientState(entry.getKey(), averagePpm, floorNum);
            smoothedStates.add(smoothedState);
        }

        return smoothedStates;
    }

    @Override
    public synchronized void publish() throws RemoteException {
        if(clients.isEmpty()){
            statesReceived.clear();
            return;
        }

        List<ClientState> clientStateList = calculateSmoothedClientStateList();

        for(CO2Client client : clients){
            client.updateState(clientStateList);
        }

        statesReceived.clear();
    }

    @Override
    public synchronized void receiveStateUpdate(ClientState newState) {
        System.out.println("Received new state from client with UUID: " + newState.getClientUuid());

        statesReceived.putIfAbsent(newState.getClientUuid(), new ArrayList<>());
        List<ClientState> currentList = statesReceived.get(newState.getClientUuid());

        currentList.add(newState);
    }
}
