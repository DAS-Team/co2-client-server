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
    private int delayBetweenReadings = 1000;

    private class PollTask extends TimerTask {

        @Override
        public void run() {
            try {
                Double newCo2 = pollForPPM();
                /*if (listener != null && Math.abs(prevCO2 - newCo2) >= co2Delta){
                    listener.onCO2LevelChange(newCo2);
                }*/
                listener.onCO2LevelChange(newCo2);
                prevCO2 = newCo2;
            } catch (IOException e) {
                e.printStackTrace();
            }
            timer.schedule(new PollTask(), delayBetweenReadings);
        }
    }

    @Override
    public void setListener(CO2ChangeEventListener listener, double co2Delta) {
        this.co2Delta = co2Delta;
        this.listener = listener;
        timer.schedule(new PollTask(), delayBetweenReadings);
    }

    @Override
    public void removeListener() {
        timer.cancel();
        this.listener = null;
    }

    @Override
    public double pollForPPM() throws IOException {
        long timestamp = System.currentTimeMillis();
        double randomReading = (timestamp/100000)%700 + random.nextDouble()*30;
        return randomReading;
    }

    @Override
    public void close() throws Exception {
        timer.cancel();
    }
}
