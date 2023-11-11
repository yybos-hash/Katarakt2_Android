package yybos.hash.katarakt2.Socket;

import android.util.Log;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.Chat;
import yybos.hash.katarakt2.Socket.Objects.Message;

public class Client {
    private final List<Message> history;
    private final List<Chat> chats;
    private final List<ClientInterface> listeners = new ArrayList<>();

    private boolean isConnected = false;

    private Utils messageUtils;

    private String loginUsername;
    private String loginPassword;

    public Client (List<Chat> chats, List<Message> history, String username, String password) {
        this.history = history;
        this.chats = chats;

        // define login info
        this.loginUsername = username == null ? " " : username;
        this.loginPassword = password == null ? " " : password;
    }

    public boolean isConnected () {
        return isConnected;
    }

    public void tryConnection () {
        Thread t1 = new Thread(this::connect);
        t1.start();
    }

    public void connect() {
        try {
            Socket messageSocket = new Socket();
            Socket mediaSocket = new Socket();
            Socket chatsSocket = new Socket();

            // sets the ip and port of the servers
            SocketAddress messageAddress = new InetSocketAddress(Constants.server, Constants.messagePort);
            SocketAddress mediaAddress = new InetSocketAddress(Constants.server, Constants.mediaPort);
            SocketAddress chatsAddress = new InetSocketAddress(Constants.server, Constants.chatsPort);

            messageSocket.connect(messageAddress, 4500);
//            mediaSocket.connect(downloadAddress, 4500);
            chatsSocket.connect(chatsAddress, 4500);
            // connect the clients

            if (!messageSocket.isConnected() || !chatsSocket.isConnected())
                return;

            // set the threads
            Thread messageThread = new Thread(() -> handleMessage(messageSocket));
//            Thread mediaThread = new Thread(() -> handleMedia(mediaSocket));
            Thread chatsThread = new Thread(() -> handleChats(chatsSocket));

            // start the threads
            messageThread.start();
//            mediaThread.start();
            chatsThread.start();
        } catch (Exception e) {
            Log.i("Client Connection", "Deu meme");

            this.isConnected = false;
        }
    }

    private void handleMessage(Socket client) {
        Utils messageUtils = new Utils(client);
        this.messageUtils = messageUtils;

        try {
            messageUtils.sendRawMessage(Constants.version + ';' + this.loginUsername + ';' + this.loginPassword);

            this.isConnected = true;

            int packet;
            Message message;

            String temp;
            String bucket = "";
            StringBuilder rawMessage = new StringBuilder();

            boolean receiving;

            try {
                while (true) {
                    // receive message
                    do {
                        receiving = true;

                        // rawMessage will be the parsed message and the bucket will be the next message. Break the loop and parse :Sex_penis:
                        if (!bucket.isEmpty()) {
                            rawMessage = new StringBuilder(bucket.substring(0, bucket.indexOf('\0')));
                            bucket = bucket.substring(bucket.indexOf('\0') + 1);

                            break;
                        }

                        packet = messageUtils.in.read(Constants.buffer);
                        if (packet <= 0) // if the packet bytes count is less or equal to 0 then the client has disconnected, which means that the thread should be terminated
                            return;

                        temp = new String(Constants.buffer, 0, packet, Constants.encoding);

                        // checks for the \0 in the temp
                        for (int i = 0; i < temp.length(); i++) {
                            if (temp.charAt(i) == '\0') {
                                receiving = false;

                                // bucket will store the beginning of the other message ( ...}/0{... )
                                bucket = temp.substring(i + 1);
                                rawMessage = new StringBuilder(temp.substring(0, i));

                                break;
                            }
                        }
                    } while (receiving);

                    System.out.println("messageClient: " + rawMessage);

                    // parse raw message
                    message = Message.fromString(rawMessage.toString().replace("\0", ""));

                    // append message to history
                    this.history.add(message);
                    this.notifyMessageToListeners(message);
                    // notify message to listeners (chatFragment)
                }
            }
            catch (Exception e) {
                messageUtils.close();

                e.printStackTrace();
                System.out.println("Exception in client: " + client.getInetAddress().toString());
                System.out.println(e.getMessage());
                System.out.println("Returning");

                this.isConnected = false;
            }
        }
        catch (Exception e) {
            messageUtils.close();

            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Returning");

            this.isConnected = false;
        }
    }
    private void handleMedia(Socket client) {

    }
    private void handleChats(Socket client) {
        Utils chatsUtils = new Utils(client);

        try {
            chatsUtils.sendRawMessage(Constants.version + ';' + this.loginUsername + ';' + this.loginPassword);

            int packet;
            Chat chat;

            String temp;
            String bucket = "";
            StringBuilder rawMessage = new StringBuilder();

            boolean receiving;

            try {
                while (true) {
                    // receive chat
                    do {
                        receiving = true;

                        // rawMessage will be the parsed chat and the bucket will be the next chat. Break the loop and parse :Sex_penis:
                        if (!bucket.isEmpty()) {
                            rawMessage = new StringBuilder(bucket.substring(0, bucket.indexOf('\0')));
                            bucket = bucket.substring(bucket.indexOf('\0') + 1);

                            break;
                        }

                        packet = chatsUtils.in.read(Constants.buffer);
                        if (packet <= 0) // if the packet bytes count is less or equal to 0 then the client has disconnected, which means that the thread should be terminated
                            return;

                        temp = new String(Constants.buffer, 0, packet, Constants.encoding);

                        // checks for the \0 in the temp
                        for (int i = 0; i < temp.length(); i++) {
                            if (temp.charAt(i) == '\0') {
                                receiving = false;

                                // bucket will store the beginning of the other chat ( ...}/0{... )
                                bucket = temp.substring(i + 1);
                                rawMessage = new StringBuilder(temp.substring(0, i));

                                break;
                            }
                        }
                    } while (receiving);

                    // parse raw chat
                    chat = Chat.fromString(rawMessage.toString().replace("\0", ""));

                    // huehuehuehuehue
                    this.chats.add(chat);
                }
            }
            catch (Exception e) {
                chatsUtils.close();

                e.printStackTrace();
                System.out.println("Exception in client: " + client.getInetAddress().toString());
                System.out.println(e.getMessage());
                System.out.println("Returning");
            }
        }
        catch (Exception e) {
            chatsUtils.close();

            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Returning");
        }
    }

    public void sendMessage (Message message) {
        if (this.messageUtils == null || !this.isConnected)
            return;

        this.messageUtils.sendMessage(message);
    }

    // listeners

    public void addMessageListener (ClientInterface clientInterface) {
        this.listeners.add(clientInterface);
    }
    public void removeMessageListener (ClientInterface clientInterface) {
        this.listeners.remove(clientInterface);
    }

    private void notifyMessageToListeners (Message message) {
        for (ClientInterface listener : this.listeners)
            listener.onMessageReceived(message);
    }
}
