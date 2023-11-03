package cs451.links;

import cs451.Host;
import cs451.utils.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class PerfectLinks implements Links, Viewer {
    private final Viewer viewer;
    private final StubbornLinks stubbornLinks;
    private final ReentrantLock lock = new ReentrantLock();
    private static Set<Message> delivered = new HashSet<>();
    
    public PerfectLinks(Host host, Viewer viewer) {
        this.viewer = viewer;
        this.stubbornLinks = new StubbornLinks(host, this);
    }

    @Override
    public void send(Message message, Host receiver) {
        this.stubbornLinks.send(message, receiver);
    }

    @Override
    public void startReceiving() {
        this.stubbornLinks.startReceiving();
    }

    @Override
    public void stopReceiving() {
        this.stubbornLinks.stopReceiving();
    }

    @Override
    public void deliver(Message message) {
        lock.lock();
        if (!delivered.contains(message)) {
            System.out.println("======DEBUG======");
            for (Message m : delivered) {
                System.out.println(m);
            }
            System.out.println("The message is: " + message.getSeqNr() + " sent by " + message.getSender());
            PerfectLinks.delivered.add(message);
            this.viewer.deliver(message);
        }
        lock.unlock();
    }
}
