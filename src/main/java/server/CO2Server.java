package server;

import client.CO2Client;
import client.ClientState;

import java.rmi.Remote;

public interface CO2Server extends Remote {
    /**
     * Registers a client to periodically receive updates to overall network state.
     * @param client the client to register
     */
    void subscribe(CO2Client client);

    /**
     * Unregister a client to stop receiving network state updates.
     * @param client the client to unregister.
     */
    void unsubscribe(CO2Client client);

    /**
     * Notify all clients of the current network state.
     */
    void publish();

    /**
     * Updates the server's view of a client's state.
     */
    void receiveStateUpdate(ClientState newState);
}
