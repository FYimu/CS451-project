package cs451.links;

import cs451.utils.Message;
import cs451.Host;

public interface Links {
    void send(Message message, Host receiver);

    void startReceiving();
    void stopReceiving();
    
}
