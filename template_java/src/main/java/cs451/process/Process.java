package cs451.process;

import cs451.utils.Message;

public interface Process {
    public void stop();

    public void start();

    public void writeLogs();

    public void deliver(Message message);
}

