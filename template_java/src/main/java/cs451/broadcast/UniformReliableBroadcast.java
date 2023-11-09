package cs451.broadcast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import cs451.Host;
import cs451.utils.HostManager;
import cs451.utils.Message;
import cs451.utils.MessageIdentification;
import cs451.utils.Viewer;


public class UniformReliableBroadcast implements Broadcast, Viewer{
    private final Viewer viewer;
    private final BestEffortBroadcast bestEffortBroadcast;
    // delivered
    private static Set<MessageIdentification> delivered = new HashSet<>();
    // pending
    private static Set<MessageIdentification> pending = new HashSet<>();
    // ack
    private static Map<MessageIdentification, Set<Host>> ack = new HashMap<>();
    private static int N = HostManager.getAllHosts().size();
    //private final DeliverHelper deliverHelper;

    public UniformReliableBroadcast(Host host, Viewer viewer) {
        this.viewer = viewer;
        this.bestEffortBroadcast = new BestEffortBroadcast(host, this);
        //deliverHelper = new DeliverHelper(viewer);
        
    }


    @Override
    public void broadcast(Message message) {
        // Message has info: <self, self, m>
        pending.add(new MessageIdentification(message));
        this.bestEffortBroadcast.broadcast(message);
    }

    @Override
    public synchronized void deliver(Message message) {
        MessageIdentification messageId = new MessageIdentification(message);
        if (!ack.containsKey(messageId)) {
            ack.put(messageId, new HashSet<>());
        }
        // add the host who broadcasts this message to the set of hosts who acknowledge
        System.out.println(messageId);
        System.out.println(ack.get(messageId));
        ack.get(messageId).add(message.getSender());
        System.out.println("Host " + message.getSender().getId() + " acknowledges message " + message);
        System.out.println(ack.get(messageId));
        if (!pending.contains(messageId)) {
            pending.add(messageId);
            this.bestEffortBroadcast.broadcast(message);
        }

        //deliverHelper.start();
        // message equivalence is checked by
        // 1. same original sender
        // 2. same information
        // 3. same receiver
        // 4. same sender
        for (MessageIdentification messageId2 : pending) {
            Message message2 = messageId.getMessage();
            if (candeliver(messageId2) && !delivered.contains(messageId2)) {
                viewer.deliver(message2);
                delivered.add(messageId2);
            }
        }

        
    }

    

    @Override
    public void startReceiving() {
        this.bestEffortBroadcast.startReceiving();
    }
    
    @Override
    public void stopReceiving() {
        this.bestEffortBroadcast.stopReceiving();
    }

    public boolean candeliver(MessageIdentification messageId) {
        System.out.println("DEBUG: " + UniformReliableBroadcast.ack.getOrDefault(messageId, new HashSet<>()).size());
        return UniformReliableBroadcast.ack.getOrDefault(messageId, new HashSet<>()).size() > N/2;
    }

    /*x

    private static class DeliverHelper extends Thread {
        private final Viewer viewer;

        DeliverHelper(Viewer viewer) {
            this.viewer = viewer;
        }

        public synchronized void run() {
            try {
                while (true) {
                    for (MessageIdentification messageId : pending) {
                        Message message = messageId.getMessage();
                        if (UniformReliableBroadcast.candeliver(messageId) && !delivered.contains(messageId)) {
                            viewer.deliver(message);
                            delivered.add(messageId);
                        }
                    }
                    Thread.sleep(2000l);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
        
    } */
}


