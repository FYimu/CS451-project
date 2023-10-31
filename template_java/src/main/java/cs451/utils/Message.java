package cs451.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import cs451.Host;

public class Message {
    private final int seqNr;
    
    private final Host sender;
    private final Host receiver;
    private boolean isAck = false;

    public Message(Host sender, Host receiver, int seqNr) {
        this.sender = sender;
        this.receiver = receiver;
        this.seqNr = seqNr;
    }

    public Message(byte[] byteMessage) {
        this.seqNr = ByteBuffer.wrap(byteMessage).order(ByteOrder.LITTLE_ENDIAN).getInt();
        int senderId = byteMessage[4];
        int receiverId = byteMessage[5];
        this.isAck = (int) byteMessage[6] != 0;
        this.sender = HostManager.getHostById(senderId);
        this.receiver = HostManager.getHostById(receiverId);
    }

    public void ack() {
        this.isAck = true;
    }

    public boolean isAck() {
        return isAck;
    }

    public byte[] getByteMessage() {
        byte[] seqNrArray = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(seqNr).array();
        byte[] byteMsg = new byte[8];
        System.arraycopy(seqNrArray, 0, byteMsg, 0, seqNrArray.length);
        byteMsg[4] = (byte) sender.getId();
        byteMsg[5] = (byte) receiver.getId();
        byteMsg[6] = isAck ? (byte) 1 : 0;
        return byteMsg;
    }

    public Host getSender() {
        return this.sender;
    }

    public Host getReceiver() {
        return this.receiver;
    }

    public int getSeqNr() {
        return this.seqNr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null | getClass() != o.getClass()) return false;
        Message other = (Message) o;
        return this.seqNr == other.seqNr && 
        this.sender.getId() == other.sender.getId() &&
        this.isAck == other.isAck;

    }

    @Override
    public String toString() {
        return this.seqNr + " " + this.sender.getId() + " " + this.isAck;
    }

    // override for Set.contains()
    @Override
    public int hashCode() {
        return Objects.hash(seqNr, sender.getId(), isAck);
    }
}
