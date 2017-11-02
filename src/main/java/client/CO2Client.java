package client;

import java.rmi.Remote;
import java.rmi.server.Unreferenced;
import java.util.List;
import java.util.UUID;

public interface CO2Client extends Remote, Unreferenced {
    /**
     * Updates view of current network state.
     * @param clientStateList list of states of all clients.
     */
    void updateState(List<ClientState> clientStateList);

    /**
     * Notifies the server of the current client state.
     */
    void sendNewState();

    /**
     * @return the floor that the client is located on.
     */
    int getFloor();

    /**
     * @return the uuid of the client.
     */
    UUID getUUID();

    /**
     * @return a measurement of CO2 concentration in ppm.
     */
    double getPPM();
}
