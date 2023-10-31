package cs451.links;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.HashMap;

import cs451.Host;
import cs451.utils.Message;
import cs451.utils.Viewer;
import cs451.utils.HostAddress;

public class StubbornLinks implements Links, Viewer{
    private final Viewer viewer;
    private final FairLossLinks fairLossLinks;
    private final Host host;
    // private final HashMap<> deliveredMap;
    private static HashMap<HostAddress, Message> sent = new HashMap<>();
    private MessageResender messageResender;

    public StubbornLinks(Host host, Viewer viewer) {
        this.viewer = viewer;
        this.host = host;
        this.fairLossLinks = new FairLossLinks(host, this);
        //this.deliveredMap = new HashMap<>();
        this.messageResender = new MessageResender(fairLossLinks);
        // start to resend messages periodically
        messageResender.start();
    }

    @Override
    public void send(Message message, Host receiver) {
        if (!message.isAck()) {
            message.ack();
            sent.put(new HostAddress(message.getSender(), receiver), message);
        }
        fairLossLinks.send(message, receiver);
        
    }



    @Override
    public void deliver(Message message) {
        System.out.println("BL is delivering message: " + String.format("d %d %d\n", message.getSender().getId(), message.getSeqNr()));
        this.viewer.deliver(message);
    }

    @Override
    public void startReceiving() {
        fairLossLinks.startReceiving();
    }

    @Override
    public void stopReceiving() {
        fairLossLinks.stopReceiving();
    }

    private static class MessageResender extends Thread{
        private Links links;

        MessageResender(Links links) {
            this.links = links;
        }

        public void run() {
            try {
                while(true) {
                    HashMap<HostAddress, Message> copySent = new HashMap<>(StubbornLinks.sent);

                    for (Message message : copySent.values()) {
                        // resend the message
                        this.links.send(message, message.getReceiver());
                    }
                
                    Thread.sleep(400);
                }
            } catch (InterruptedException error) {
                error.printStackTrace();
            }
        }
    }
}



