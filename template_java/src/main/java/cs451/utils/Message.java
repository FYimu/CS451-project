package cs451.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import cs451.Host;

public class Message {
    private static AtomicInteger MSG_INDEX = new AtomicInteger(1);
    private final int msgIndex;
    private final int seqNr;
    
    private final Host sender;
    private final Host receiver;
    private final Host originalSender;

    public Message(Host sender, Host receiver, int seqNr) {
        this.sender = sender;
        this.receiver = receiver;
        this.seqNr = seqNr;
        this.originalSender = sender;
        this.msgIndex = MSG_INDEX.get();
        Message.MSG_INDEX.incrementAndGet();
    }

    public Message(byte[] byteMessage) {
        this.seqNr = ByteBuffer.wrap(byteMessage).order(ByteOrder.LITTLE_ENDIAN).getInt();
        int senderId = byteMessage[4];
        int receiverId = byteMessage[5];
        int originalSenderId = byteMessage[6];
        msgIndex = (int) byteMessage[7];
        this.sender = HostManager.getHostById(senderId);
        this.receiver = HostManager.getHostById(receiverId);
        this.originalSender = HostManager.getHostById(originalSenderId);
    }

    public byte[] getByteMessage(Host resender) {
        byte[] seqNrArray = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(seqNr).array();
        byte[] byteMsg = new byte[8];
        System.arraycopy(seqNrArray, 0, byteMsg, 0, seqNrArray.length);
        byteMsg[4] = (byte) resender.getId();
        byteMsg[5] = (byte) receiver.getId();
        byteMsg[6] = (byte) originalSender.getId();
        byteMsg[7] = (byte) msgIndex;
        return byteMsg;
    }

    public Host getSender() {
        return this.sender;
    }

    public Host getReceiver() {
        return this.receiver;
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
}
