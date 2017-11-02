package client;

import java.io.Serializable;
import java.util.UUID;

/**
 * Immutable representation of a {@link CO2Client} state at a given time.
 */
public class ClientState implements Serializable {
    private final UUID clientUuid;
    private final double ppm;
    private final int floorNum;

    public ClientState(CO2Client client){
        this.clientUuid = client.getUUID();
        this.floorNum = client.getFloor();
        this.ppm = client.getPPM();
    }

    public ClientState(UUID clientUuid, double ppm, int floorNum) {
        this.clientUuid = clientUuid;
        this.ppm = ppm;
        this.floorNum = floorNum;
    }

    public UUID getClientUuid() {
        return clientUuid;
    }

    public double getPpm() {
        return ppm;
    }

    public int getFloorNum() {
        return floorNum;
    }
}
