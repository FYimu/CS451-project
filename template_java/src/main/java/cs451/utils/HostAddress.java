package cs451.utils;

import java.util.Objects;

import cs451.Host;

public class HostAddress {
    private final int msgIndex;
    private final Host receiver;

    public HostAddress(Host to, int msgIndex) {
        this.msgIndex = msgIndex;
        this.receiver = to;
    }

    public int getMsgIndex() {
        return this.msgIndex;
    }

    public Host getServer() {
        return receiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null | getClass() != o.getClass()) return false;
        HostAddress other = (HostAddress) o;
        return this.receiver.equals(other.receiver) &&
        this.msgIndex == other.msgIndex;

    }

    // override for Set.contains()
    @Override
    public int hashCode() {
        return Objects.hash(receiver, msgIndex);
    }
}
