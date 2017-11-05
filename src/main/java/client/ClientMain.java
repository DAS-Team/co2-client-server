package client;

import server.CO2Server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by paul on 05/11/17.
 */
public class ClientMain {
    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        String hostPortURL;

        if(args.length == 0){
            System.err.println("No URL provided");
            return;
        }
        else {
            hostPortURL = args[0];
        }

        CO2Server server = (CO2Server) Naming.lookup(hostPortURL + "/server");
        CO2Client client = new CO2ClientImpl(server, 1);
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
                    System.out.println(client.getPPM());
                    client.sendNewState();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }
}
