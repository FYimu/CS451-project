package cs451.links;

import cs451.utils.*;

import java.net.DatagramSocket;
import java.util.HashMap;

import cs451.Host;

public class PerfectLinks implements Links, Viewer {
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors() + 1;
    private final Viewer viewer;
    private final StubbornLinks stubbornLinks;
    private final Host host;
    
    PerfectLinks(Host host, Viewer viewer) {
        this.host = host;
        this.viewer = viewer;
        this.stubbornLinks = new StubbornLinks(host, viewer);
    }

    @Override
    public void send(Message message, Host receiver) {}

    @Override
    public void startReceiving() {
        // server.start();
    }

    @Override
    public void stopReceiving() {}

    @Override
    public void deliver(Message message) {}
}
