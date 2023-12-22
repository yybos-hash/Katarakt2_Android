package yybos.hash.katarakt2.Socket;

import android.graphics.Color;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.MainActivity;
import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.Chat;
import yybos.hash.katarakt2.Socket.Objects.Command;
import yybos.hash.katarakt2.Socket.Objects.Login;
import yybos.hash.katarakt2.Socket.Objects.Message;
import yybos.hash.katarakt2.Socket.Objects.PacketObject;
import yybos.hash.katarakt2.Socket.Objects.User;

public class Client {
    private final List<ClientInterface> listeners = new ArrayList<>();

    private boolean isConnected = false;
    private boolean isConnecting = false;

    private String ipAddress;
    private int port;

    private Utils messageUtils;
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

    public void tryConnection () {
        if (this.isConnected || this.isConnecting)
            return;

        if (this.mainActivity.getLoginEmail().equals(" ") || this.mainActivity.getLoginPassword().equals(" "))
            return;

        this.ipAddress = this.mainActivity.getIpAddress();
        this.port = this.mainActivity.getPort();

        this.user.setEmail(this.mainActivity.getLoginEmail());
        this.user.setPassword(this.mainActivity.getLoginPassword());

        Thread t1 = new Thread(this::connect);
        t1.start();
    }

    private void connect () {
        try {
            Socket managerSocket = new Socket();

            // sets the ip and port of the servers
            SocketAddress managerAddress = new InetSocketAddress(this.ipAddress, this.port);

            this.isConnecting = true;
            managerSocket.connect(managerAddress, 4000);
            this.isConnecting = false;

            // connect the clients

            if (!managerSocket.isConnected()) {
                this.notifyMessageToListeners(Message.toMessage("Failed to connect to the server", "Katarakt"), false);
                return;
            }

            this.handleMessage(managerSocket);
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
            this.user.setUsername("Redmi");

            // send alllejdnaejkdklad
            messageUtils.sendObject(Login.toLogin(Constants.version, Constants.messagePort, this.user.getEmail(), this.user.getPassword()));

            this.isConnected = true;
            this.mainActivity.showCustomToast("Connected", Color.argb(90, 85, 209, 63));

            int packet;
            PacketObject packetObject;

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

                        packet = messageUtils.in.read(Constants.buffer);
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

                    // parse rawMessage
                    packetObject = PacketObject.fromString(rawMessage.toString());

                    if (packetObject.getType() == PacketObject.Type.Message) {
                        Message message = Message.fromString(rawMessage.toString());
                        message.setUser(this.user);

                        this.notifyMessageToListeners(message, true);
                    }
                    else if (packetObject.getType() == PacketObject.Type.Chat) {
                        Chat chat = Chat.fromString(rawMessage.toString());

                        // huehuehuehuehue
                        this.notifyChatToListeners(chat);
                    }
                    else if (packetObject.getType() == PacketObject.Type.User) {
                        // notify message to listeners (chatFragment)
                        User user = User.fromString(rawMessage.toString());

                        // yeah, the login info
                        this.user = user;
                    }
                    else if (packetObject.getType() == PacketObject.Type.Command) {
                        Command command = Command.fromString(rawMessage.toString());

                        switch (command.getCommand()) {
                            case "askUsername": {
                                this.mainActivity.getChatFragmentInstance().displayInputPopup();

                                break;
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                messageUtils.close();

                System.out.println("Exception in client: " + client.getInetAddress().toString());
                System.out.println(e.getMessage());

                this.mainActivity.showCustomToast("Disconnected", Color.argb(90, 235, 64, 52));

                // just tell the user that the connection was closed
                Message conMessage = Message.toMessage("Connection abruptly interrupted by the server", "Socket");
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

    }

    // methods to send objects

    private void sendPacketObject(PacketObject packet) {
        if (this.messageUtils == null || !this.isConnected)
            return;

        // checks wheter its on the main thread or not
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // You are on the main thread/UI thread
            new Thread(() -> this.messageUtils.sendObject(packet)).start();
        } else {
            // You are not on the main thread
            this.messageUtils.sendObject(packet);
        }
    }

    public void sendMessage (Message message) {
        if (!this.isConnected)
            return;

        this.sendPacketObject(message);
    }
    public void sendCommand (Command command) {
        if (!this.isConnected)
            return;

        this.sendPacketObject(command);
    }

    public void getChatHistory (int chatId) {
        this.sendPacketObject(Command.getChatHistory(chatId));
    }
    public void getChats () {
        this.mainActivity.getChatsHistory().clear();
        this.sendPacketObject(Command.getChats());
    }
    public void setUsername (String username) {
        this.sendPacketObject(Command.setUsername(username));
    }

    public void downloadFile (String filePath) {
        // file path is actually the path of the file in the server os

    }
    public void uploadFile (File file) {

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
}
