package client;

import server.CO2Server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by paul on 10/11/17.
 */
public class TestClientMain {
    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        String hostname;
        int floorNum;

        if(args.length < 2){
            System.err.println("Wrong args provided, need rmiregistry URL and floor number");
        }

        hostname = args[0];
        floorNum = Integer.parseInt(args[1]);

        CO2Server server = (CO2Server) Naming.lookup("//" + hostname + ":1099/server");
        CO2Client client = new CO2ClientImpl(new DummySensorReader(), server, floorNum);
        server.subscribe(client);

        // If we stop the JVM, unsubscribe first
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    server.receiveStateUpdate(new ClientState(client, client.pollForPPM()));
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }
}
