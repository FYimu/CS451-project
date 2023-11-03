package cs451.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class ConfigParser {
    private List<Integer> plConfig = new ArrayList<Integer>();

    private String path;

    public boolean populate(String value) {
        File file = new File(value);
        path = file.getPath();
        try {
            BufferedReader br = new BufferedReader(new FileReader(value));
            String line = br.readLine();
            String[] configs = line.split(" ");
            // m: how many messages each process should send
            plConfig.add(Integer.parseInt(configs[0]));
            // i: index of the process that should receive this message
            plConfig.add(Integer.parseInt(configs[1]));
            br.close();
        } catch (IOException error) {
            System.out.println("Error in config file.");
            return false;
        }
        return true;
    }

    public List<Integer> getConfig() {
        return plConfig;
    }

    public String getPath() {
        return path;
    }

}
