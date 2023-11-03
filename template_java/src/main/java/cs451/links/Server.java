package cs451.links;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;

import cs451.Host;
import cs451.utils.*;

public class Server extends Thread {
    private Viewer viewer;
    private DatagramSocket socket;
    private static final byte[] DATA = new byte[1024];
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(4);

    Server(Host host, Viewer viewer) {
        this.viewer = viewer;
        try {
            this.socket = new DatagramSocket(host.getPort());
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public void stopReceiving() {
        isRunning.set(false);
        socket.close();
    }

    @Override
    public void run() {
        isRunning.set(true);
        DatagramPacket packet = new DatagramPacket(DATA, DATA.length);
        try {
            while (isRunning.get()) {
                socket.receive(packet);
                // debug
                System.out.println("Socket received...");
                Message message = new Message(packet.getData());
                // debug
                System.out.println("Message received: " + String.format("d %d %d\n", message.getSender().getId(), message.getSeqNr()));
                THREAD_POOL.execute(() -> viewer.deliver(message));
            }
        } catch (IOException error) {
            error.printStackTrace();
        }
    } 


}
