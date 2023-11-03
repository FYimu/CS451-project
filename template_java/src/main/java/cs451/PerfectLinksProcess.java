package cs451;

import cs451.utils.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import cs451.broadcast.BestEffortBroadcast;
import cs451.broadcast.Broadcast;

public class PerfectLinksProcess implements Process, Viewer {
    private final ConcurrentLinkedQueue<String> logs;
    private final Broadcast broadcast;
    private final int numOfMsg;
    private final Host host;
    private final Host desHost;
    private final String outputPath;

    public PerfectLinksProcess(Host host, Host desHost, int numOfMsg, String outputPath) {
        this.host = host;
        this.numOfMsg = numOfMsg;
        this.logs = new ConcurrentLinkedQueue<>();
        this.desHost = desHost;
        this.broadcast = new BestEffortBroadcast(host, this);
        this.outputPath = outputPath;
        // start listening
        this.broadcast.startReceiving();
    }

    public void stop() {
        this.broadcast.stopReceiving();
    }

    public void start() {
        System.out.println("Num of msg: " + numOfMsg);
        for (int i = 1; i < numOfMsg + 1; i ++ ) {
            Message message = new Message(host, desHost, i);
            broadcast.broadcast(message);
            // write logs
            logs.add("b " + message.getSeqNr() + "\n");
        }
    }

    public void writeLogs() {
        try {
            FileOutputStream output = new FileOutputStream(outputPath);
            logs.forEach(x -> {
                try {
                    output.write(x.getBytes());
                } catch (IOException error) {
                    error.printStackTrace();
                }
            });
            output.close();
            // debug
            System.out.println("Logs updated...");
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    @Override
    public void deliver(Message message) {
        //debug
        System.out.println("Message delivered: " + String.format("d %d %d\n", message.getSender().getId(), message.getSeqNr()));
        logs.add(String.format("d %d %d\n", message.getSender().getId(), message.getSeqNr()));
    }
}
