package client;

import server.FloorValueStates;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface CO2Client extends Remote, AutoCloseable {
    /**
     * Updates view of current network state.
     * @param floorValueStates list of value states of all floors.
     */
    void updateState(FloorValueStates floorValueStates) throws RemoteException;

    /**
     * @return the floor of the building that the client is located on.
     */
    int getFloor() throws RemoteException;

    /**
     * @return the uuid of the client.
     */
    UUID getUUID() throws RemoteException;

    /**
     * @return the current CO2 sensor value in parts-per-million.
     */
    double pollForPPM() throws IOException;

    /**
     * Called by the server when it has registered the client.
     * @param ready true if the server is subscribing the client, false if it is unsubscribing.
     */
    void setReady(boolean ready) throws RemoteException;

}
