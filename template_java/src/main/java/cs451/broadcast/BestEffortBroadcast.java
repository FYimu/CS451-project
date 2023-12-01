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
    private final Host host;

    public BestEffortBroadcast(Host host, Viewer viewer) {
        this.host = host;
        this.viewer = viewer;
        this.perfectLinks = new PerfectLinks(host, this);
    }
    @Override
    public void broadcast(Message message) {
        HashMap<Integer, Host> allHosts = HostManager.getAllHosts();
        allHosts.values().parallelStream().forEach(host2 -> {
            if (host2.getId() != this.host.getId())
                this.perfectLinks.send(message, host2);
        });
        deliver(new Message(message, this.host)); // directly deliver by itself
        // change the host to itself to make sure it  gets registered by URB
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
