package client;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by paul on 08/11/17.
 *
 * Allows access to a sensor which returns CO2 PPM values.
 */
public interface SensorReader extends AutoCloseable {

    /**
     * @return Optional.of(PPM) if it is available from the sensor, Optional.empty() otherwise.
     * @throws IOException if the sensor cannot be accessed
     */
    Optional<Double> pollForPPM() throws IOException;

    void close() throws IOException;
}
