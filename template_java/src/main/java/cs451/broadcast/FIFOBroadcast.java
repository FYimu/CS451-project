package cs451.broadcast;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import cs451.Host;
import cs451.utils.HostManager;
import cs451.utils.Message;
import cs451.utils.Viewer;


public class FIFOBroadcast implements Broadcast, Viewer{
    private final Viewer viewer;
    private final UniformReliableBroadcast urBroadcast;
    // pending
    private Map<Integer, Map<Integer, Message>> pendingMap = new HashMap<>();
    private AtomicInteger[] nextMessageIndices = new AtomicInteger[HostManager.getTotalHostNumber()];

    public FIFOBroadcast(Host host, Viewer viewer) {
        this.viewer = viewer;
        this.urBroadcast = new UniformReliableBroadcast(host, this);
        for (int i = 0; i < nextMessageIndices.length; i++) {
            nextMessageIndices[i] = new AtomicInteger(1);
        }
        int totalHost = HostManager.getTotalHostNumber();
        for (int j = 1; j < totalHost + 1; j++) {
            pendingMap.put(j, new HashMap<>());
        }
        
    }


    @Override
    public void broadcast(Message message) {
        this.urBroadcast.broadcast(message);
    }

    @Override
    public void deliver(Message message) {
        int hostIndex = message.getOriginalSender().getId();
        Map<Integer, Message> mapByHost = pendingMap.get(hostIndex);
        if (canDeliver(message)) {
            deliverMsg(message);
        } else {
            mapByHost.put(message.getMsgIndex(), message);
        }

        int next = nextMessageIndices[hostIndex - 1].get();
        if (mapByHost.containsKey(next)) {
            deliver(mapByHost.get(next));
            mapByHost.remove(next);
        }
        
    }

    private boolean canDeliver(Message message) {
        int hostIndex = message.getOriginalSender().getId();
        return message.getMsgIndex() == this.nextMessageIndices[hostIndex - 1].get();
    }

    private void deliverMsg(Message message) {
        this.viewer.deliver(message);
        int hostIndex = message.getOriginalSender().getId();
        this.nextMessageIndices[hostIndex - 1].incrementAndGet();
    }

    

    @Override
    public void startReceiving() {
        this.urBroadcast.startReceiving();
    }
    
    @Override
    public void stopReceiving() {
        this.urBroadcast.stopReceiving();
    }

}


