package client;

import server.CO2Server;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.UUID;

public class CO2ClientImpl extends UnicastRemoteObject implements CO2Client {
    private final CO2Server server;
    private final UUID uuid;
    private List<ClientState> clientStates;
    private final int floor;

    public CO2ClientImpl(CO2Server server, int floor) throws RemoteException {
        super();
        this.server = server;
        this.uuid = UUID.randomUUID();
        this.floor = floor;
    }

    @Override
    public void updateState(List<ClientState> clientStateList) {
        this.clientStates = clientStateList;

        // This would be true iff we didn't receive a message about ourselves.
        // Should we handle that in some way?
        /* if(this.clientStates.stream().map(ClientState::getClientUuid).noneMatch(e -> e == this.uuid)){
         * }
         */
    }

    @Override
    public void sendNewState() {
        server.receiveStateUpdate(new ClientState(this));
    }

    @Override
    public int getFloor() {
        return this.floor;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public double getPPM() {
        // TODO: Get current PPM value from Arduino or rPi
        throw new NotImplementedException();
    }

    @Override
    // In the case that the client becomes disconnected, this will unsubscribe itself from the server.
    public void unreferenced() {
        server.unsubscribe(this);
    }


    @Override
    // true iff uuid == o.uuid
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CO2ClientImpl co2Client = (CO2ClientImpl) o;

        return uuid.equals(co2Client.uuid);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + uuid.hashCode();
        return result;
    }
}