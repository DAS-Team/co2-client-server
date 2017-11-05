package client;

import server.CO2Server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.List;
import java.util.UUID;

public class CO2ClientImpl extends UnicastRemoteObject implements CO2Client, Unreferenced {
    private final CO2Server server;
    private final UUID uuid;
    private List<ClientState> clientStates;
    private final int floor;
    private final SerialSensorReader sensor;

    public CO2ClientImpl(CO2Server server, int floor) throws RemoteException {
        super();
        this.server = server;
        this.uuid = UUID.randomUUID();
        this.floor = floor;
        this.sensor = new SerialSensorReader();
    }

    @Override
    public void updateState(List<ClientState> clientStateList) {
        System.out.println("Got client state list from server");
        this.clientStates = clientStateList;

        // This would be true iff we didn't receive a message about ourselves.
        // Should we handle that in some way?
        /* if(this.clientStates.stream().map(ClientState::getClientUuid).noneMatch(e -> e == this.uuid)){
         * }
         */
    }

    @Override
    public void sendNewState() throws RemoteException {
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
        try {
            // TODO: Fix this?
            return sensor.pollForPPM().orElse(0.0);
        } catch (IOException e) {
            // For now just fail, if seen in prod then there's an issue.
            throw new IllegalStateException();
        }
    }

    @Override
    // In the case that the client becomes disconnected, this will unsubscribe itself from the server.
    public void unreferenced() {
        // TODO: Check if need to sensor.close() here
        try {
            server.unsubscribe(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
