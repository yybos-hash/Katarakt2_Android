package yybos.hash.katarakt2.Socket;

import static yybos.hash.katarakt2.Socket.Objects.Media.MediaFile.MediaProcessType.DOWNLOAD;

import android.graphics.Color;
import android.os.Looper;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.Anime;
import yybos.hash.katarakt2.Socket.Objects.Login;
import yybos.hash.katarakt2.Socket.Objects.Media.Directory;
import yybos.hash.katarakt2.Socket.Objects.Media.MediaFile;
import yybos.hash.katarakt2.Socket.Objects.Message.Chat;
import yybos.hash.katarakt2.Socket.Objects.Message.Command;
import yybos.hash.katarakt2.Socket.Objects.Message.Message;
import yybos.hash.katarakt2.Socket.Objects.Message.User;
import yybos.hash.katarakt2.Socket.Objects.PacketObject;
import yybos.hash.katarakt2.Socket.Objects.Server;

public class Client {
    private final List<ClientInterface> listeners = new ArrayList<>();

    private boolean isConnected = false;
    private boolean isConnecting = false;

    private String ipAddress;
    private int port;

    private Utils messageUtils;
    private Utils mediaUtils;
    private final MainActivity mainActivity;

    public User user;

    public Client (MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.user = new User();
    }

    public boolean isConnected () {
        return this.isConnected;
    }
    public boolean isConnecting () {
        return this.isConnecting;
    }

    public void tryConnection (Server server) {
        if (this.isConnecting)
            return;

        if (this.isConnected)
            this.close();

        if (server.email.equals(" ") || server.password.equals(" "))
            return;

        this.ipAddress = server.serverIp;
        this.port = server.serverPort;

        this.user.setEmail(server.email);
        this.user.setPassword(server.password);

        Thread t1 = new Thread(this::connect);
        t1.start();
    }

    private void connect () {
        try {
            Socket messageManagerSocket = new Socket();
            Socket mediaManagerSocket = new Socket();

            // sets the ip and port
            SocketAddress managerAddress = new InetSocketAddress(this.ipAddress, this.port);

            // connect the client

            this.isConnecting = true;
            messageManagerSocket.connect(managerAddress, 4000);
            mediaManagerSocket.connect(managerAddress, 4000);
            this.isConnecting = false;

            if (!messageManagerSocket.isConnected()) {
                this.notifyMessageToListeners(Message.toMessage("Failed to connect to the server", User.toUser(0, "Socket")), false);
                return;
            }

            new Thread(() -> this.handleMessage(messageManagerSocket)).start();
            new Thread(() -> this.handleMedia(mediaManagerSocket)).start();
        } catch (Exception e) {
            Log.i("Client Connection", "Deu meme");
            this.mainActivity.showCustomToast("Could not connect to " + this.ipAddress, Color.GRAY);

            this.isConnected = false;
            this.isConnecting = false;
        }
    }

    // server handlers

    private void handleMessage (Socket client) {
        Utils messageUtils = new Utils(client);
        this.messageUtils = messageUtils;

        try {
            // send alllejdnaejkdklad
            messageUtils.sendObject(Login.toLogin(Constants.version, Constants.messagePort, this.user.getEmail(), this.user.getPassword()));

            this.isConnected = true;
            this.mainActivity.showCustomToast("Connected", Color.argb(90, 85, 209, 63));

            String bucket = "";
            StringBuilder rawMessage;

            try {
                while (true) {
                    rawMessage = new StringBuilder();

                    // receive packetObject
                    do {
                        // rawMessage will be the parsed packetObject and the bucket will be the next packetObject. Break the loop and parse :Sex_penis:
                        if (!bucket.isEmpty()) {
                            if (bucket.contains("\0")) {
                                rawMessage = new StringBuilder(bucket.substring(0, bucket.indexOf('\0') + 1));
                                bucket = bucket.substring(bucket.indexOf('\0') + 1);

                                break;
                            }
                            else
                                rawMessage.append(bucket);
                        }

                        int packet = messageUtils.in.read(Constants.buffer);
                        if (packet <= 0) // if the packet bytes count is less or equal to 0 then the client has disconnected, which means that the thread should be terminated
                            throw new IOException("Client connection was abruptly interrupted");

                        String temp = new String(Constants.buffer, 0, packet, Constants.encoding);

                        // checks for the \0 in the temp
                        int i = temp.indexOf('\0');
                        if (i != -1) {
                            rawMessage.append(temp, 0, i + 1);
                            bucket = temp.substring(i + 1);

                            break;
                        }

                        // tem que ter
                        rawMessage.append(temp);
                    } while (true);
                    rawMessage = new StringBuilder(rawMessage.toString().replace("\0", ""));

                    // get packet type
                    PacketObject.Type packetType = this.getPacketType(rawMessage.toString());

                    if (packetType == PacketObject.Type.Message) {
                        Message message = Message.fromString(rawMessage.toString());
                        message.setUser(message.getUser());

                        // notify message to listeners (chatFragment)
                        this.notifyMessageToListeners(message, true);
                    }
                    else if (packetType == PacketObject.Type.Chat) {
                        Chat chat = Chat.fromString(rawMessage.toString());

                        // huehuehuehuehue
                        this.notifyChatToListeners(chat);
                    }
                    else if (packetType == PacketObject.Type.User) {
                        User user = User.fromString(rawMessage.toString());

                        // yeah, the login info
                        this.user = user;
                        this.mainActivity.setLoginUsername(user.getUsername());
                    }
                    else if (packetType == PacketObject.Type.Command) {
                        Command command = Command.fromString(rawMessage.toString());

                        switch (command.getCommand()) {
                            case "askUsername": {
                                this.mainActivity.getChatFragmentInstance().displayUsernameInputPopup();

                                break;
                            }
                            case "errorToast": {
                                this.mainActivity.showCustomToast(command.getString("message"), Color.argb(90, 235, 64, 52));

                                break;
                            }
                            case "output": {
                                this.notifyCommandToListeners(command);

                                break;
                            }
                        }
                    }
                    else if (packetType == PacketObject.Type.Anime) {
                        Anime anime = Anime.fromString(rawMessage.toString());

                        this.notifyAnimeToListeners(anime);
                    }
                }
            }
            catch (Exception e) {
                messageUtils.close();

                System.out.println("Exception in client");
                System.out.println(e.getMessage());
                e.printStackTrace();

                this.user.setUsername("");
                this.mainActivity.setLoginUsername("");
                this.mainActivity.currentChatId = 0;

                this.mainActivity.showCustomToast("Disconnected", Color.argb(90, 235, 64, 52));
                this.mainActivity.getChatFragmentInstance().clearChat();

                // just tell the user that the connection was closed
                Message conMessage = Message.toMessage("Connection abruptly interrupted by the server", User.toUser(0, "Socket"));
                this.notifyMessageToListeners(conMessage, false);

                this.isConnected = false;
            }
        }
        catch (Exception e) {
            messageUtils.close();

            System.out.println(e.getMessage());

            this.isConnected = false;
        }
    }
    private void handleMedia (Socket client) {
        this.mediaUtils = new Utils(client);

        try {
            // send alllejdnaejkdklad
            this.mediaUtils.sendObject(Login.toLogin(Constants.version, Constants.mediaPort, this.user.getEmail(), this.user.getPassword()));

            this.isConnected = true;

            String bucket = "";
            StringBuilder rawMessage;

            try {
                while (true) {
                    rawMessage = new StringBuilder();

                    // receive packetObject
                    do {
                        // rawMessage will be the parsed packetObject and the bucket will be the next packetObject. Break the loop and parse :Sex_penis:
                        if (!bucket.isEmpty()) {
                            if (bucket.contains("\0")) {
                                rawMessage = new StringBuilder(bucket.substring(0, bucket.indexOf('\0') + 1));
                                bucket = bucket.substring(bucket.indexOf('\0') + 1);

                                break;
                            }
                            else
                                rawMessage.append(bucket);
                        }

                        int packet = this.mediaUtils.in.read(Constants.buffer);
                        if (packet <= 0) // if the packet bytes count is less or equal to 0 then the client has disconnected, which means that the thread should be terminated
                            throw new IOException("Client connection was abruptly interrupted");

                        String temp = new String(Constants.buffer, 0, packet, Constants.encoding);

                        // checks for the \0 in the temp
                        int i = temp.indexOf('\0');
                        if (i != -1) {
                            rawMessage.append(temp, 0, i + 1);
                            bucket = temp.substring(i + 1);

                            break;
                        }

                        // tem que ter
                        rawMessage.append(temp);
                    } while (true);
                    rawMessage = new StringBuilder(rawMessage.toString().replace("\0", ""));

                    // get packet type
                    PacketObject.Type packetType = this.getPacketType(rawMessage.toString());

                    if (packetType == PacketObject.Type.File) {
                        MediaFile media = MediaFile.fromString(rawMessage.toString());

                        switch (media.getProcessType()) {
                            case UPLOAD: {
                                this.mediaUtils.out.write(new byte[1]); // separate the json from the actual data
                                break;
                            }
                            case DOWNLOAD: {
                                this.mediaUtils.out.write(new byte[1]); // separate the json from the actual data

                                long progress = 0;
                                int packet;

                                System.out.println("filesize " + media.getSize());
                                do {
                                    packet = this.mediaUtils.in.read(Constants.buffer);
                                    progress += packet; // increase progress with each packet size

//                                outputStream.write(packet);
                                } while (progress < media.getSize());

                                System.out.println("done");

//                              outputStream.close();
                                break;
                            }
                            case PREVIEW: {
                                // notify file to listeners
                                this.notifyFileToListeners(media, true);
                                break;
                            }
                        }
                    }
                    else if (packetType == PacketObject.Type.User) {
                        User user = User.fromString(rawMessage.toString());

                        // why would I need to redefine it?
                    }
                    else if (packetType == PacketObject.Type.Directory) {
                        Directory directory = Directory.fromString(rawMessage.toString());

                        this.notifyDirectoryToListeners(directory);
                    }
                }
            }
            catch (Exception e) {
                this.mediaUtils.close();

                System.out.println("Exception in client");
                System.out.println(e.getMessage());
                e.printStackTrace();

                this.user.setUsername("");
                this.mainActivity.setLoginUsername("");
                this.mainActivity.currentChatId = 0;

                this.mainActivity.showCustomToast("Disconnected", Color.argb(90, 235, 64, 52));
                this.mainActivity.getChatFragmentInstance().clearChat();

                // just tell the user that the connection was closed
                Message conMessage = Message.toMessage("Connection abruptly interrupted by the server", User.toUser(0, "Socket"));
                this.notifyMessageToListeners(conMessage, false);

                this.isConnected = false;
            }
        }
        catch (Exception e) {
            this.mediaUtils.close();

            System.out.println("error");
            System.out.println(e.getMessage());

            this.isConnected = false;
        }
    }

    private void close () {
        if (this.messageUtils != null)
            this.messageUtils.close();
    }

    public String getClientIp () {
        if (this.messageUtils == null)
            return "localhost";

        return this.messageUtils.client.getInetAddress().getHostAddress();
    }

    // methods to send objects

    private void sendPacketObject (PacketObject packet, boolean mediaServer) {
        Utils utils;

        if (mediaServer)
            utils = this.mediaUtils;
        else
            utils = this.messageUtils;

        if (utils == null || !this.isConnected)
            return;

        // checks wheter its on the main thread or not
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // You are on the main thread/UI thread
            new Thread(() -> utils.sendObject(packet)).start();
        } else {
            // You are not on the main thread
            utils.sendObject(packet);
        }
    }

    public void sendMessage (Message message) {
        if (!this.isConnected)
            return;

        this.sendPacketObject(message, false);
    }
    public void sendCommand (Command command) {
        if (!this.isConnected)
            return;

        this.sendPacketObject(command, false);
    }
    public void sendCommand (Command command, boolean mediaServer) {
        if (!this.isConnected)
            return;

        if (mediaServer)
            this.sendPacketObject(command, true);
    }

    public void getChatHistory (int chatId) {
        this.mainActivity.getChatFragmentInstance().clearChat();
        this.sendPacketObject(Command.getChatHistory(chatId), false);
    }
    public void getChats () {
        this.sendPacketObject(Command.getChats(), false);
    }
    public void updateUsername (String username) {
        this.sendPacketObject(Command.setUsername(username), false);
    }

    public void downloadFile (MediaFile file) {
        file.setProcessType(DOWNLOAD);

        // checks wheter its on the main thread or not
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // You are on the main thread/UI thread
            new Thread(() -> this.mediaUtils.sendObject(file)).start();
        } else {
            // You are not on the main thread
            this.mediaUtils.sendObject(file);
        }
    }
    public void uploadFile (MediaFile file) {

    }

    // listeners

    public void addEventListener(ClientInterface clientInterface) {
        if (!this.listeners.contains(clientInterface))
            this.listeners.add(clientInterface);
    }
    public void removeEventListener(ClientInterface clientInterface) {
        this.listeners.remove(clientInterface);
    }

    private void notifyMessageToListeners (Message message, boolean addToHistory) {
        if (addToHistory)
            // append message to history
            this.mainActivity.getMessageHistory().add(message);

        for (ClientInterface listener : this.listeners)
            listener.onMessageReceived(message);
    }
    private void notifyChatToListeners (Chat chat) {
        for (ClientInterface listener : this.listeners)
            listener.onChatReceived(chat);
    }
    private void notifyCommandToListeners (Command command) {
        for (ClientInterface listener : this.listeners)
            listener.onCommandReceived(command);
    }
    private void notifyAnimeToListeners (Anime anime) {
        for (ClientInterface listener : this.listeners)
            listener.onAnimeReceived(anime);
    }
    private void notifyFileToListeners (MediaFile mediaFile, boolean addToHistory) {
        if (addToHistory)
            // append message to history
            this.mainActivity.getMessageHistory().add(mediaFile);

        for (ClientInterface listener : this.listeners)
            listener.onFileReceived(mediaFile);
    }
    private void notifyDirectoryToListeners (Directory directory) {
        for (ClientInterface listener : this.listeners)
            listener.onDirectoryReceived(directory);
    }

    private PacketObject.Type getPacketType (String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        return PacketObject.Type.getEnumByValue(jsonObject.get("type").getAsInt());
    }
}
