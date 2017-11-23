package server;

import client.CO2Client;
import client.ClientState;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CO2Server extends Remote {
    /**
     * Registers a client to periodically receive updates to overall network state.
     * @param client the client to register
     */
    void subscribe(CO2Client client, ClientState initialClientState) throws RemoteException;

    /**
     * Unregister a client to stop receiving network state updates.
     * @param client the client to unregister.
     */
    void unsubscribe(CO2Client client) throws RemoteException;

    /**
     * Notify all clients of their new {@link FloorValueState}s if they have changed since the last call.
     */
    void publishIfStateChanged() throws RemoteException;

    /**
     * Updates the server's view of a client's state.
     */
    void receiveStateUpdate(ClientState newState) throws RemoteException;
}
