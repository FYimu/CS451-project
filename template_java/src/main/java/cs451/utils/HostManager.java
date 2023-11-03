package cs451.utils;

import java.util.HashMap;
import java.util.List;

import cs451.Host;

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

    public static HashMap<Integer, Host> getAllHosts() {
        return HostManager.manager;
    }

}
