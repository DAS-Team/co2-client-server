package client;

import server.CO2Server;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

/**
 * Created by paul on 10/11/17.
 */
public class TestClientMain {
    public static void main(String[] args) throws IOException, NotBoundException {
        String hostname;
        int floorNum;

        if(args.length < 2){
            System.err.println("Wrong args provided, need rmiregistry URL and floor number");
        }

        hostname = args[0];
        floorNum = Integer.parseInt(args[1]);

        CO2Server server = (CO2Server) Naming.lookup("//" + hostname + ":1099/server");
        CO2Client client = new CO2ClientImpl(new DummySensorReader(), server, floorNum);
        server.subscribe(client, new ClientState(client, client.pollForPPM()));

        // If we stop the JVM, unsubscribe first
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

    }
}
