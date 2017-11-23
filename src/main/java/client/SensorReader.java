package client;

import java.io.IOException;

/**
 * Created by paul on 08/11/17.
 *
 * Allows access to a sensor which returns CO2 PPM values.
 */
public interface SensorReader extends AutoCloseable {

    /**
     * @param listener method to be invoked whenever CO2 value changes enough.
     * @param ppmCo2Change the amount that the CO2 level should change before calling {@code listener}
     */
    void setListener(CO2ChangeEventListener listener, double ppmCo2Change);

    /**
     * Removes listener if one exists.
     */
    void removeListener();

    /**
     * @return the CO2 value at the current instant in PPM
     * @throws IOException if access to the sensor fails
     */
    double pollForPPM() throws IOException;
}
