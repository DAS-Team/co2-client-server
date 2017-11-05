package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by paul on 05/11/17.
 */
public class ServerMain {
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        String hostPortURL;

        if(args.length == 0){
            System.err.println("No URL provided");
            return;
        }
        else {
            hostPortURL = args[0];
        }

        CO2Server server = new CO2ServerImpl();

        Naming.rebind(hostPortURL + "/server", server);

        System.out.println("Server ready!");

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
