package client;

import com.sun.org.apache.bcel.internal.generic.RET;
import server.CO2Server;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.UUID;

public class CO2ClientImpl extends UnicastRemoteObject implements CO2Client {
    private final CO2Server server;
    private final UUID uuid;
    private List<ClientState> clientStates;
    private final int floor;
    private double latestPpmReading;
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

    // Polls the sensor for the current CO2 PPM value
    private double pollPpmSensor(){
        return latestPpmReading;
    }

    @Override
    public double getPPM() {
        try {
            latestPpmReading = sensor.pollForPPM();
            return latestPpmReading;
        } catch (IOException e) {
            // For now just fail, if seen in prod then there's an issue.
            throw new IllegalStateException();
        }
    }

    @Override
    // In the case that the client becomes disconnected, this will unsubscribe itself from the server.
    public void unreferenced() {
        // TODO: Check if need to sensor.close() here
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
