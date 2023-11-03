package cs451.broadcast;

import java.util.HashMap;

import cs451.links.PerfectLinks;
import cs451.utils.HostManager;
import cs451.utils.Message;
import cs451.utils.Viewer;
import cs451.Host;

public class BestEffortBroadcast implements Broadcast, Viewer {
    private final Viewer viewer;
    private final PerfectLinks perfectLinks;

    public BestEffortBroadcast(Host host, Viewer viewer) {
        this.viewer = viewer;
        this.perfectLinks = new PerfectLinks(host, this);
    }
    @Override
    public void broadcast(Message message) {
        HashMap<Integer, Host> allHosts = HostManager.getAllHosts();
        allHosts.values().parallelStream().forEach(host -> this.perfectLinks.send(message, host));
    }

    @Override
    public void deliver(Message message) {
        this.viewer.deliver(message);
    }

    @Override
    public void startReceiving() {
        this.perfectLinks.startReceiving();
    }
    
    @Override
    public void stopReceiving() {
        this.perfectLinks.stopReceiving();
    }
    
}
