package yybos.hash.katarakt2.Socket.Interfaces;

import yybos.hash.katarakt2.Socket.Objects.Message;

public interface ClientInterface {
    void onMessageReceived(Message message);
}
