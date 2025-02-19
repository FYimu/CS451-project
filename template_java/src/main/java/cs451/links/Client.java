package cs451.links;

import cs451.Host;
import cs451.utils.Message;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Client implements Runnable {
    private final DatagramSocket socket;
    private final Host receiver;
    private final Message message;
    private final Host sender;

    public Client(Host sender, Host receiver, Message message, DatagramSocket socket) {
            this.receiver = receiver;
            this.socket = socket;
            this.message = message;
            this.sender = sender;
    }

    @Override
    public void run() {
        byte[] msg = message.getByteMessage(sender);
        InetSocketAddress address = new InetSocketAddress(receiver.getIp(), receiver.getPort());
        DatagramPacket packet = new DatagramPacket(msg, msg.length, address);

        try {
            socket.send(packet);
            //System.out.println("Packet sent by client...");
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

}
