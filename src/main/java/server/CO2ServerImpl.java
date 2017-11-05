package server;

import client.CO2Client;
import client.ClientState;

import java.rmi.RemoteException;
import java.util.*;

public class CO2ServerImpl implements CO2Server {
    private final Set<CO2Client> clients;

    /* Contains the ClientStates received since the last publish()
     * This way we could also calculate the variance if we want.
     * Could be useful for displaying uncertainty in our results.
     */
    private final Map<UUID, List<ClientState>> statesReceived;

    public CO2ServerImpl(){
        this.clients = new HashSet<>();
        this.statesReceived = new HashMap<>();
    }


    @Override
    public void subscribe(CO2Client client) {
        clients.add(client);
    }

    @Override
    public void unsubscribe(CO2Client client) {
        clients.remove(client);
    }

    private List<ClientState> calculateSmoothedClientStateList(){
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
                    .orElse(0.0);

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
    public void publish() throws RemoteException {
        List<ClientState> clientStateList = calculateSmoothedClientStateList();

        for(CO2Client client : clients){
            client.updateState(clientStateList);
        }

        statesReceived.clear();
    }

    @Override
    public void receiveStateUpdate(ClientState newState) {
        statesReceived.putIfAbsent(newState.getClientUuid(), new ArrayList<>());
        List<ClientState> currentList = statesReceived.get(newState.getClientUuid());

        currentList.add(newState);
    }
}
