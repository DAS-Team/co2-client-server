package client;

import java.io.IOException;

/**
 * Created by paul on 08/11/17.
 *
 * Allows access to a sensor which returns CO2 PPM values.
 */
public interface SensorReader extends AutoCloseable {
    void setListener(CO2ChangeEventListener listener, double co2Delta);

    double pollForPPM() throws IOException;
}
