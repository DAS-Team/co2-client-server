package client;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class DummySensorReader implements SensorReader {
    private final Random random = new Random();
    private Timer timer = new Timer();
    private CO2ChangeEventListener listener = null;
    private double co2Delta = 0.0;
    private double prevCO2 = 0.0;

    private class PollTask extends TimerTask {

        @Override
        public void run() {
            try {
                double newCo2 = pollForPPM();
                if(listener != null && Math.abs(prevCO2 - pollForPPM()) > co2Delta){
                    listener.onCO2LevelChange(newCo2);
                }

                prevCO2 = newCo2;
            } catch (IOException e) {
                e.printStackTrace();
            }
            int delay = random.nextInt(60) * 1000;
            timer.schedule(new PollTask(), delay);
        }
    }

    @Override
    public void setListener(CO2ChangeEventListener listener, double co2Delta) {
        this.co2Delta = co2Delta;
        this.listener = listener;
        timer.schedule(new PollTask(), random.nextInt(60) * 1000);
    }

    @Override
    public void removeListener() {
        timer.cancel();
        this.listener = null;
    }

    @Override
    public double pollForPPM() throws IOException {
        return random.nextDouble() * 400;
    }

    @Override
    public void close() throws Exception {
        timer.cancel();
    }
}
