package client;

import server.CO2Server;
import server.FloorValueState;
import server.FloorValueStates;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.List;
import java.util.UUID;

public class CO2ClientImpl extends UnicastRemoteObject implements CO2Client, Unreferenced {
    private final CO2Server server;
    private final UUID uuid;
    private List<FloorValueState> floorStates;
    private final int floor;
    private final SensorReader sensor;
    private long prevUpdateId = Long.MIN_VALUE;
    private boolean ready = false;

    public CO2ClientImpl(SensorReader sensorReader, CO2Server server, int floor) throws RemoteException {
        super();
        this.server = server;
        this.uuid = UUID.randomUUID();
        this.floor = floor;
        this.sensor = sensorReader;

        System.out.println("CO2 client created");
    }

    public void setReady(boolean ready){
        this.ready = ready;

        if(ready) {
            sensor.setListener(newCO2Level -> {
                        try {
                            System.out.println("Sending new reading: " + newCO2Level.toString());
                            server.receiveStateUpdate(new ClientState(this, newCO2Level));
                        } catch (RemoteException e) {
                            System.err.println("Failed to send new CO2 value to server");
                        }
                    },
                    100
            );
        }
        else {
            sensor.removeListener();
        }
    }

    public CO2ClientImpl(CO2Server server, int floor, double rZeroValue) throws IOException {
        this(new MCP3008SensorReader(rZeroValue), server, floor);
    }

    @Override
    public void updateState(FloorValueStates floorValueStates) throws RemoteException {
        if(floorValueStates.getId() >= prevUpdateId) {
            floorStates = floorValueStates.getStates();
            prevUpdateId = floorValueStates.getId();
        }
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
    public double pollForPPM() throws IOException {
        return sensor.pollForPPM();
    }

    @Override
    // In the case that the client becomes disconnected, this will unsubscribe itself from the server.
    public void unreferenced() {
        try {
            close();
        } catch (Exception e) {
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

    @Override
    public void close() throws Exception {
        this.ready = false;
        server.unsubscribe(this);
        sensor.close();
    }
}
