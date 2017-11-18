package client;

import java.io.IOException;
import java.util.Random;

public class DummySensorReader implements SensorReader {
    private final Random random = new Random();

    @Override
    public void setListener(CO2ChangeEventListener listener, double co2Delta) {
        // Do nothing
    }

    @Override
    public double pollForPPM() throws IOException {
        return random.nextDouble() * 400;
    }

    @Override
    public void close() throws Exception {
        // Do nothing
    }
}
