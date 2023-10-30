package cs451.utils;

import cs451.Host;
import java.util.HashMap;
import java.util.List;

public class HostManager {
    private static HashMap<Integer, Host> manager = new HashMap<Integer, Host>();

    public static void init(List<Host> hosts) {
        for (Host host : hosts) {
            manager.put(host.getId(), host);
        }
    }

    public static Host getHostById(int id) {
        return manager.get(id);
    }

}
