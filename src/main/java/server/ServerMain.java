package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by paul on 05/11/17.
 */
public class ServerMain {
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        int port = 1099;
        String hostname = "0.0.0.0";
        String bindAddr = "//" + hostname + ":" + port + "/server";

        LocateRegistry.createRegistry(port);
        CO2Server server = new CO2ServerImpl();
        Naming.rebind(bindAddr, server);

        System.out.println("Server ready at " + bindAddr);

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    server.publish();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 10 * 1000);
    }
}
