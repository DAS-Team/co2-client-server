package client;

import server.CO2Server;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

/**
 * Created by paul on 05/11/17.
 */
public class ClientMain {
    public static void main(String[] args) throws IOException, NotBoundException {
        String hostname;
        int floorNum;
        double rZeroVal;

        if(args.length < 3){
            System.err.println("Wrong args provided, need rmiregistry URL, floor number and RZero value");
        }

        hostname = args[0];
        floorNum = Integer.parseInt(args[1]);
        rZeroVal = Double.parseDouble(args[2]);


        CO2Server server = (CO2Server) Naming.lookup("//" + hostname + ":1099/server");
        CO2Client client = new CO2ClientImpl(server, floorNum, rZeroVal);
        server.subscribe(client);

        // If we stop the JVM, unsubscribe first
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        server.receiveStateUpdate(new ClientState(client, client.pollForPPM()));

        // From here on, state changes will be sent automatically

    }
}
