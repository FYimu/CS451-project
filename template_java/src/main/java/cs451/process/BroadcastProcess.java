package cs451.process;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import cs451.Host;
import cs451.broadcast.Broadcast;
import cs451.broadcast.FIFOBroadcast;
import cs451.utils.Message;
import cs451.utils.Viewer;

public class BroadcastProcess implements Process, Viewer {
    private final ConcurrentLinkedQueue<String> logs;
    private final Broadcast broadcast;
    private final int numOfMsg;
    private final Host host;
    private final String outputPath;

    public BroadcastProcess(Host host, int numOfMsg, String outputPath) {
        this.host = host;
        this.numOfMsg = numOfMsg;
        this.logs = new ConcurrentLinkedQueue<>();
        this.broadcast = new FIFOBroadcast(host, this);
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
            Message message = new Message(host, host, i);
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
