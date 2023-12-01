package cs451.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import cs451.Host;

public class Message implements Comparable<Message> {
    private static AtomicInteger MSG_INDEX = new AtomicInteger(1);
    private final int msgIndex;
    private final int seqNr;

    private final boolean isAck;
    
    private final Host sender;
    // private final Host receiver;
    private final Host originalSender;

    public Message(Host sender, Host receiver, int seqNr) {
        this.sender = sender;
        // this.receiver = receiver;
        this.seqNr = seqNr;
        this.originalSender = sender;
        this.msgIndex = MSG_INDEX.get();
        Message.MSG_INDEX.incrementAndGet();
        this.isAck = false;
    }

    public Message(Message message, boolean ack) {
        this.sender = message.sender;
        // this.receiver = receiver;
        this.seqNr = message.seqNr;
        this.originalSender = message.originalSender;
        this.msgIndex = message.msgIndex;
        this.isAck = ack;
    }

    public Message(Message message, Host host) {
        this.sender = host;
        // this.receiver = receiver;
        this.seqNr = message.seqNr;
        this.originalSender = message.originalSender;
        this.msgIndex = message.msgIndex;
        this.isAck = message.isAck;
    }

    public Message(byte[] byteMessage) {
        this.seqNr = ByteBuffer.wrap(byteMessage).order(ByteOrder.LITTLE_ENDIAN).getInt();
        int senderId = byteMessage[4];
        int originalSenderId = byteMessage[5];
        msgIndex = (int) byteMessage[6];
        isAck = (int) byteMessage[7] == 1 ? true : false;
        this.sender = HostManager.getHostById(senderId);
        // this.receiver = HostManager.getHostById(receiverId);
        this.originalSender = HostManager.getHostById(originalSenderId);
    }

    public byte[] getByteMessage(Host resender) {
        byte[] seqNrArray = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(seqNr).array();
        byte[] byteMsg = new byte[8];
        System.arraycopy(seqNrArray, 0, byteMsg, 0, seqNrArray.length);
        byteMsg[4] = (byte) resender.getId();
        byteMsg[5] = (byte) originalSender.getId();
        byteMsg[6] = (byte) msgIndex;
        byteMsg[7] = (byte) (isAck ? 1 : 0);
        return byteMsg;
    }

    public Host getSender() {
        return this.sender;
    }

    //public Host getReceiver() {
    //    return this.receiver;
    //}

    public boolean isAck() {
        return this.isAck;
    }

    public Host getOriginalSender() {
        return this.originalSender;
    }

    public int getSeqNr() {
        return this.seqNr;
    }

    public int getMsgIndex() {
        return msgIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null | getClass() != o.getClass()) return false;
        Message other = (Message) o;
        return this.seqNr == other.seqNr && 
        // need to compare sender otherwise PL would only deliver once for URB
        this.sender.getId() == other.sender.getId() &&
        this.originalSender.getId() == other.originalSender.getId() &&
        this.msgIndex == other.msgIndex;

    }

    @Override
    public String toString() {
        return this.seqNr + " " + this.originalSender.getId() + " " + this.msgIndex;
    }

    // override for Set.contains()
    @Override
    public int hashCode() {
        return Objects.hash(seqNr, sender.getId(), originalSender.getId(), msgIndex);
    }

    @Override
    public int compareTo(Message o) {
        return this.msgIndex - o.msgIndex;
    }
}
