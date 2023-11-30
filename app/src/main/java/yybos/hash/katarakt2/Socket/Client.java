package yybos.hash.katarakt2.Socket;

import android.os.Looper;
import android.util.Log;

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
import yybos.hash.katarakt2.Socket.Objects.Message;
import yybos.hash.katarakt2.Socket.Objects.PacketObject;
import yybos.hash.katarakt2.Socket.Objects.User;

public class Client {
    private final List<ClientInterface> listeners = new ArrayList<>();

    private boolean isConnected = false;

    private Utils messageUtils;
    private final MainActivity mainActivity;

    public User user;

    public Client (MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public boolean isConnected () {
        return isConnected;
    }

    public void tryConnection () {
        if (mainActivity.getLoginEmail().equals(" ") || mainActivity.getLoginPassword().equals(" "))
            return;

        Thread t1 = new Thread(this::connect);
        t1.start();
    }

    private void connect () {
        try {
            Socket messageSocket = new Socket();
            Socket mediaSocket = new Socket();

            // sets the ip and port of the servers
            SocketAddress messageAddress = new InetSocketAddress(Constants.server, Constants.messagePort);
            SocketAddress mediaAddress = new InetSocketAddress(Constants.server, Constants.mediaPort);

            messageSocket.connect(messageAddress, 4500);
//            mediaSocket.connect(downloadAddress, 4500);
            // connect the clients

            if (!messageSocket.isConnected())
                return;

            // set the threads
            Thread messageThread = new Thread(() -> handleMessage(messageSocket));
//            Thread mediaThread = new Thread(() -> handleMedia(mediaSocket));

            // start the threads
            messageThread.start();
//            mediaThread.start();
        } catch (Exception e) {
            Log.i("Client Connection", "Deu meme");

            this.isConnected = false;
        }
    }

    private void handleMessage (Socket client) {
        Utils messageUtils = new Utils(client);
        this.messageUtils = messageUtils;

        try {
            // send alllejdnaejkdklad
            messageUtils.sendRawMessage(Constants.version + ';' + mainActivity.getLoginEmail() + ';' + mainActivity.getLoginPassword() + ';' +  mainActivity.getLoginUsername());

            this.isConnected = true;

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
                }
            }
            catch (Exception e) {
                messageUtils.close();

                System.out.println("Exception in client: " + client.getInetAddress().toString());
                System.out.println(e.getMessage());

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
        this.sendPacketObject(message);
    }

    public void getChatHistory (int chatId) {
        this.sendPacketObject(Command.getChatHistory(chatId));
    }
    public void getChats () {
        this.sendPacketObject(Command.getChats());
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
            this.mainActivity.getHistory().add(message);

        for (ClientInterface listener : this.listeners)
            listener.onMessageReceived(message);
    }
    private void notifyChatToListeners (Chat chat) {
        for (ClientInterface listener : this.listeners)
            listener.onChatReceived(chat);
    }
}
