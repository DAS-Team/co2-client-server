package client;

import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by paul on 05/11/17.
 */
public class ClientMain {
    public static void main(String[] args) throws RemoteException {

        // TODO: Replace with real server
        CO2Client client = new CO2ClientImpl(null, 1);

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println(client.getPPM());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }
}
