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
    private int randomiser = (int) (System.currentTimeMillis() % 1000);
    private boolean timerCancelled = false;
    private PollTask task;

    private class PollTask extends TimerTask {

        @Override
        public void run() {
            try {
                double newCo2 = pollForPPM();
                /*if (listener != null && Math.abs(prevCO2 - newCo2) >= co2Delta){
                    listener.onCO2LevelChange(newCo2);
                }*/

                if(listener != null) {
                    listener.onCO2LevelChange(newCo2);
                }
                prevCO2 = newCo2;
            } catch (Exception ignored) {
            }

            if(!timerCancelled) {
                task = new PollTask();
                timer.schedule(task, delayBetweenReadings);
            }
        }
    }

    @Override
    public void setListener(CO2ChangeEventListener listener, double co2Delta) {
        this.co2Delta = co2Delta;
        this.listener = listener;
        timerCancelled = false;
        timer = new Timer();

        task = new PollTask();

        timer.schedule(task, 10 * 1000);
    }

    @Override
    public void removeListener() {
        if(!timerCancelled){
            task.cancel();
            timer.cancel();
            timerCancelled = true;
        }

        this.listener = null;
    }

    @Override
    public double pollForPPM() throws IOException {
        double randomReading = this.randomiser + random.nextDouble()*30;
        return randomReading;
    }

    @Override
    public void close() throws Exception {
        if(!timerCancelled){
            timer.cancel();
            timerCancelled = true;
        }
    }
}
