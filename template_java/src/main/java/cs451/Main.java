package cs451;


import java.util.List;

import cs451.links.PerfectLinks;
import cs451.parser.Parser;
import cs451.utils.HostManager;

public class Main {
    private static Process process;

    private static void handleSignal() {
        //immediately stop network packet processing
        System.out.println("Immediately stopping network packet processing.");
        process.stop();
        //write/flush output file if necessary
        System.out.println("Writing output.");
        process.writeLogs();
    }

    private static void initSignalHandlers() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                handleSignal();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        Parser parser = new Parser(args);
        parser.parse();

        initSignalHandlers();

        // example
        long pid = ProcessHandle.current().pid();
        System.out.println("My PID: " + pid + "\n");
        System.out.println("From a new terminal type `kill -SIGINT " + pid + "` or `kill -SIGTERM " + pid + "` to stop processing packets\n");

        System.out.println("My ID: " + parser.myId() + "\n");
        System.out.println("List of resolved hosts is:");
        System.out.println("==========================");
        for (Host host: parser.hosts()) {
            System.out.println(host.getId());
            System.out.println("Human-readable IP: " + host.getIp());
            System.out.println("Human-readable Port: " + host.getPort());
            System.out.println();
        }
        System.out.println();

        System.out.println("Path to output:");
        System.out.println("===============");
        System.out.println(parser.output() + "\n");

        System.out.println("Path to config:");
        System.out.println("===============");
        System.out.println(parser.config() + "\n");

        System.out.println("Doing some initialization\n");


        /////////// ADDED /////////////
        int myId = parser.myId();
        HostManager.init(parser.hosts());
        Host myHost = HostManager.getHostById(myId);
        String outPath = parser.output();

        // initialize process
        List<Integer> configs = parser.getConfig();
        int m = configs.get(0);
        int i = configs.get(1);

        
        Host desHost = HostManager.getHostById(i);
        PerfectLinks.init(); // create an empty set to store messages
        // if i == myId, this line will make the process start listening
        process = new Process(myHost, desHost, m, outPath);
        if (i != myId) {
            System.out.println("Sending messages from " + myId + "to" + desHost.getPort());
            process.startSending();
        }

        System.out.println("Finishing sending messages...");

        // After a process finishes broadcasting,
        // it waits forever for the delivery of messages.
        while (true) {
            // Sleep for 1 hour
            Thread.sleep(60 * 60 * 1000);
        }
    }
}
