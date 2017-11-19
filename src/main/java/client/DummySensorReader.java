package client;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class DummySensorReader implements SensorReader {
    private final Random random = new Random();
    private Timer timer = new Timer();
    private CO2ChangeEventListener listener = null;

    private class PollTask extends TimerTask {

        @Override
        public void run() {
            try {
                if(listener != null){
                    listener.onCO2LevelChange(pollForPPM());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            int delay = random.nextInt(60) * 1000;
            timer.schedule(new PollTask(), delay);
        }
    }

    @Override
    public void setListener(CO2ChangeEventListener listener, double co2Delta) {
        this.listener = listener;
        timer.schedule(new PollTask(), random.nextInt(60) * 1000);
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
