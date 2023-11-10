package yybos.hash.katarakt2.Socket;

import android.util.Log;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.Socket.Interfaces.ClientInterface;
import yybos.hash.katarakt2.Socket.Objects.Message;

public class Client {
    private final List<Message> history;
    private final List<ClientInterface> listeners = new ArrayList<>();

    private Utils messageUtils;
    private Utils mediaUtils;
    private Utils logUtils;

    public Client (List<Message> history) {
        this.history = history;
    }

    public void connect() {
        try {
            Socket messageSocket = new Socket();
            Socket mediaSocket = new Socket();
            Socket logSocket = new Socket();

            // sets the ip and port of the servers
            SocketAddress messageAddress = new InetSocketAddress(Constants.server, Constants.messagePort);
            SocketAddress mediaAddress = new InetSocketAddress(Constants.server, Constants.mediaPort);
            SocketAddress logAddress = new InetSocketAddress(Constants.server, Constants.logPort);

            messageSocket.connect(messageAddress, 4500);
//            mediaSocket.connect(downloadAddress, 4500);
//            logSocket.connect(logAddress, 4500);
            // connect the clients

            // set the threads
            Thread messageThread = new Thread(() -> handleMessage(messageSocket));
//            Thread mediaThread = new Thread(() -> handleDownload(downloadSocket));
//            Thread logThread = new Thread(() -> handleLog(logSocket));

            // start the threads
            messageThread.start();
//            mediaThread.start();
//            logThread.start();
        } catch (Exception e) {
            Log.i("Client Connection", "Deu meme");
        }
    }

    private void handleMessage(Socket client) {
        this.messageUtils = new Utils(client);

        try {
            this.messageUtils.sendRawMessage(Constants.version);

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

                        packet = this.messageUtils.in.read(Constants.buffer);
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

                    // parse raw message
                    message = Message.fromString(rawMessage.toString().replace("\0", ""));

                    // append message to history
                    this.history.add(message);
                    this.notifyMessageToListeners(message);
                    // notify message to listeners (chatFragment)
                }
            }
            catch (Exception e) {
                this.messageUtils.close();

                e.printStackTrace();
                System.out.println("Exception in client: " + client.getInetAddress().toString());
                System.out.println(e.getMessage());
                System.out.println("Returning");
            }
        }
        catch (Exception e) {
            this.messageUtils.close();

            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Returning");
        }
    }
    private void handleMedia(Socket client) {

    }
    private void handleLog(Socket client) {

    }

    public void sendMessage (Message message) {
        if (this.messageUtils == null || this.messageUtils.client.isClosed())
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
