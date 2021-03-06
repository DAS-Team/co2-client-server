package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Created by paul on 05/11/17.
 */
public class ServerMain {
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        int port = 1099;
        String hostname;
        int numThreads = 0;

        if(args.length == 0){
            System.err.println("No URL provided");
            return;
        }
        else {
            hostname = args[0];
        }

        if(args.length >= 2){
            numThreads = Integer.valueOf(args[1]);
        }

        String bindAddr = "//" + hostname + ":" + port + "/server";
        LocateRegistry.createRegistry(1099);

        CO2Server server = new CO2ServerImpl(numThreads);

        Naming.rebind(bindAddr, server);

        System.out.println("Server ready!");
    }
}
