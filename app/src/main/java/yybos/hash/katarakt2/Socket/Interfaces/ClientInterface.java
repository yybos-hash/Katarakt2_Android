package yybos.hash.katarakt2.Socket.Interfaces;

import yybos.hash.katarakt2.Socket.Objects.Anime;
import yybos.hash.katarakt2.Socket.Objects.Media.Directory;
import yybos.hash.katarakt2.Socket.Objects.Message.Chat;
import yybos.hash.katarakt2.Socket.Objects.Message.Command;
import yybos.hash.katarakt2.Socket.Objects.Media.MediaFile;
import yybos.hash.katarakt2.Socket.Objects.Message.Message;

public interface ClientInterface {
    void onCommandReceived (Command command);
    void onMessageReceived (Message message);
    void onChatReceived (Chat chat);
    void onAnimeReceived (Anime anime);

    void onFileReceived (MediaFile mediaFile);
    void onDirectoryReceived (Directory directory);
}
