package client;

import server.CO2Server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by paul on 05/11/17.
 */
public class ClientMain {
    public static void main(String[] args) throws IOException, NotBoundException {
        String hostPortURL;
        int floorNum;

        if(args.length < 2){
            System.err.println("Wrong args provided, need rmiregistry URL and floor number");
            return;
        }

        hostPortURL = args[0];
        floorNum = Integer.parseInt(args[1]);

        System.out.println("Client ready!");
        System.out.println("Looking for server at " + hostPortURL + ":1099/server");
        CO2Server server = (CO2Server) Naming.lookup(hostPortURL + ":1099/server");
        System.out.println("Found it!");

        CO2Client client = new CO2ClientImpl(new DummySensorReader(), server, floorNum);
        server.subscribe(client);

        // If we stop the JVM, unsubscribe first
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.unsubscribe(client);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }));

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    client.sendNewState();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }
}
