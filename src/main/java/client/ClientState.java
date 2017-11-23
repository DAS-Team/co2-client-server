package client;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable representation of a {@link CO2Client} state at a given time.
 */
public class ClientState implements Serializable {
    private final Instant timestamp;
    private final UUID clientUuid;
    private final double ppm;
    private final Integer floorNum;

    ClientState(CO2Client client, double ppm) throws RemoteException {
        this.clientUuid = client.getUUID();
        this.floorNum = client.getFloor();
        this.ppm = ppm;
        this.timestamp = Instant.now();
    }

    public UUID getClientUuid() {
        return clientUuid;
    }

    public double getPpm() {
        return ppm;
    }

    public int getFloorNum(){
        return floorNum;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
