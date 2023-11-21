package cs451.utils;

import java.util.Objects;

import cs451.Host;

public class MessageIdentification {
    private final int id;
    private final Host originalSender;
    private final Message message;
    private final int msgIndex;

    public MessageIdentification(Message message) {
        this.id = message.getSeqNr();
        this.originalSender = message.getOriginalSender();
        this.message = new Message(message.getByteMessage(originalSender));
        this.msgIndex = message.getMsgIndex();
    }

    public int getSeqNr() {
        return id;
    }

    public Host getOriginalSender() {
        return originalSender;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null | getClass() != o.getClass()) return false;
        MessageIdentification other = (MessageIdentification) o;
        return this.id == other.id &&
        this.originalSender.equals(other.originalSender) &&
        this.message.equals(other.message) &&
        this.msgIndex == other.msgIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, originalSender, message, msgIndex);
    }

}
