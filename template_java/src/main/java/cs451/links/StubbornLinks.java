package cs451.links;

import java.util.HashMap;

import cs451.utils.Message;
import cs451.utils.Viewer;
import cs451.Host;
import cs451.utils.HostAddress;

public class StubbornLinks implements Links, Viewer{
    private final Viewer viewer;
    private final FairLossLinks fairLossLinks;
    // private final HashMap<> deliveredMap;
    private static HashMap<HostAddress, Message> sent = new HashMap<>();
    private MessageResender messageResender;

    public StubbornLinks(Host host, Viewer viewer) {
        this.viewer = viewer;
        this.fairLossLinks = new FairLossLinks(host, this);
        //this.deliveredMap = new HashMap<>();
        this.messageResender = new MessageResender(fairLossLinks);
        // start to resend messages periodically
        messageResender.start();
    }

    @Override
    public void send(Message message, Host receiver) {
        if (!message.isAck()) {
            sent.put(new HostAddress(message.getSender(), receiver), message);
        }
        fairLossLinks.send(message, receiver);
        
    }



    @Override
    public void deliver(Message message) {
        // System.out.println("BL is delivering message: " + String.format("d %d %d\n", message.getSender().getId(), message.getSeqNr()));
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
            // it keeps running after SIGTERM?
            try {
                while(true) {
                    HashMap<HostAddress, Message> copySent = new HashMap<>(StubbornLinks.sent);

                    for (HostAddress hostAddress : copySent.keySet()) {
                        // resend the message
                        this.links.send(copySent.get(hostAddress), hostAddress.getServer());
                        System.out.println("Resending message: from " + hostAddress.getClient() + " to " + hostAddress.getServer() + " message # " + copySent.get(hostAddress).getSeqNr());
                    }
                
                    Thread.sleep(1000);
                }
            } catch (InterruptedException error) {
                error.printStackTrace();
            }
        }
    }
}



