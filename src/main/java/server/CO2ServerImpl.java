package server;

import client.CO2Client;
import client.CO2ClientImpl;
import client.ClientState;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CO2ServerImpl implements CO2Server {
    private final Set<CO2Client> clients;

    public CO2ServerImpl(){
        this.clients = new HashSet<>();
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
        throw new NotImplementedException();
    }

    @Override
    public void publish() {
        List<ClientState> clientStateList = calculateSmoothedClientStateList();

        for(CO2Client client : clients){
            client.updateState(clientStateList);
        }
    }

    @Override
    public void receiveStateUpdate(ClientState newState) {
        throw new NotImplementedException();
    }
}
