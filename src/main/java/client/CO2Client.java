package client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.Unreferenced;
import java.util.List;
import java.util.UUID;

public interface CO2Client extends Remote {
    /**
     * Updates view of current network state.
     * @param clientStateList list of states of all clients.
     */
    void updateState(List<ClientState> clientStateList) throws RemoteException;

    /**
     * Notifies the server of the current client state.
     */
    void sendNewState() throws RemoteException;

    /**
     * @return the floor of the building that the client is located on.
     */
    int getFloor() throws RemoteException;

    /**
     * @return the uuid of the client.
     */
    UUID getUUID() throws RemoteException;

    /**
     * @return a measurement of CO2 concentration in ppm.
     */
    double getPPM() throws RemoteException;
}
