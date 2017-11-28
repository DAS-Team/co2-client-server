package client;

import server.CO2Server;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StressTestClientMain {
    public static void main(String[] args) throws IOException, NotBoundException {
        String hostname;
        long timeToRunFor;

        if(args.length < 1){
            System.err.println("Wrong args provided, need rmiregistry URL");
        }

        hostname = args[0];
        timeToRunFor = Long.valueOf(args[1]);



        CO2Server server = (CO2Server) Naming.lookup("//" + hostname + ":1099/server");

        List<CO2Client> clients = new ArrayList<>();
        for(int i = 0; i < 200; ++i){
            CO2Client client = new CO2ClientImpl(new DummySensorReader(), server, i);
            clients.add(client);
        }

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, timeToRunFor * 1000);

        clients.forEach(c -> {
            try {
                server.subscribe(c, new ClientState(c, c.pollForPPM()));
                System.out.println("Client " + c.getFloor() + " ready!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
