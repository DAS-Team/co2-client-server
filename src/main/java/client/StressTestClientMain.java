package client;

import server.CO2Server;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.List;

public class StressTestClientMain {
    public static void main(String[] args) throws IOException, NotBoundException {
        String hostname;

        if(args.length < 1){
            System.err.println("Wrong args provided, need rmiregistry URL");
        }

        hostname = args[0];

        CO2Server server = (CO2Server) Naming.lookup("//" + hostname + ":1099/server");

        List<CO2Client> clients = new ArrayList<>();
        for(int i = 0; i < 200; ++i){
            CO2Client client = new CO2ClientImpl(new DummySensorReader(), server, i);
            clients.add(client);
        }

        clients.forEach(c -> {
            try {
                server.subscribe(c, new ClientState(c, c.pollForPPM()));
                System.out.println("Client " + c.getFloor() + " ready!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        // If we stop the JVM, unsubscribe first
        Runtime.getRuntime().addShutdownHook(new Thread(() -> clients.forEach(c -> {
            try {
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        })));

    }
}
