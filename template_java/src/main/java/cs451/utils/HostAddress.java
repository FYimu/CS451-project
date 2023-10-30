package cs451.utils;

import cs451.Host;

public class HostAddress {
    private final Host from;
    private final Host to;

    public HostAddress(Host from, Host to) {
        this.from = from;
        this.to = to;
    }

    public Host getClient() {
        return from;
    }

    public Host getServer() {
        return to;
    }
}
