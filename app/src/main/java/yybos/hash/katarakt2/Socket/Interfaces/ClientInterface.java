package yybos.hash.katarakt2.Socket.Interfaces;

import yybos.hash.katarakt2.Socket.Objects.Chat;
import yybos.hash.katarakt2.Socket.Objects.Command;
import yybos.hash.katarakt2.Socket.Objects.Message;

public interface ClientInterface {
    void onCommandReceived (Command command);
    void onMessageReceived (Message message);
    void onChatReceived (Chat chat);
}
