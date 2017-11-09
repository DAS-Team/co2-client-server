package client;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

/**
 * Created by paul on 09/11/17.
 *
 * A fake sensor reader which returns random values for testing.
 */
public class DummySensorReader implements SensorReader {
    @Override
    public Optional<Double> pollForPPM() throws IOException {
        return Optional.of(new Random().nextDouble() * 400);
    }

    @Override
    public void close() throws IOException {

    }
}
