package cs451.links;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import cs451.Host;
import cs451.utils.*;

public class FairLossLinks implements Links, Viewer {
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors() + 1;
    private final Viewer viewer;
    private final Server server;
    private final Host host;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private DatagramSocket[] clientSockets;

    public FairLossLinks(Host host, Viewer viewer) {
        this.viewer = viewer;
        this.host = host;
        this.server = new Server(host, this);
        try {
            clientSockets = new DatagramSocket[NUMBER_OF_THREADS];
            for (int i = 0; i < clientSockets.length; i++) {
                clientSockets[i] = new DatagramSocket();
            }
        } catch (IOException error) {
            error.printStackTrace();
        }

    }

    @Override
    public void send(Message message, Host receiver) {
        int selectedSocket = ThreadLocalRandom.current().nextInt(clientSockets.length);
        Client client = new Client(receiver, message, clientSockets[selectedSocket]);
        //debug
        System.out.println("Message sent: " + String.format("d %d %d\n", message.getSender().getId(), message.getSeqNr()));
        threadPool.execute(client);
    }



    @Override
    public void deliver(Message message) {
        System.out.println("FL is delivering message: " + String.format("d %d %d\n", message.getSender().getId(), message.getSeqNr()));
        this.viewer.deliver(message);
    }

    @Override
    public void startReceiving() {
        server.start();
    }

    @Override
    public void stopReceiving() {
        server.stopReceiving();
    }
}
