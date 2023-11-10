package yybos.hash.katarakt2.Socket;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import yybos.hash.katarakt2.Socket.Objects.Message;

public class Utils {
    public Socket client;
    public OutputStream out;
    public InputStream in;


    public Utils (Socket client) {
        try {
            this.client = client;
            this.out = client.getOutputStream();
            this.in = client.getInputStream();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Exiting");
        }
    }

    public void sendMessage (Message message) {
        if (message == null)
            return;

        Gson messageParser = new Gson();
        String text = messageParser.toJson(message) + '\0';

        try {
            send(text);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Returning");
        }
    }
    public void sendRawMessage(String message) {
        if (message == null)
            return;

        try {
            send(message);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Returning");
        }
    }
    private void send (String message) throws IOException {
        if (message == null)
            return;

        if (message.trim().isEmpty())
            return;

        out.write(message.getBytes(Constants.encoding));
        out.flush();
    }

    public void close () {
        try {
            in.close();
            out.close();

            client.close();
            client.shutdownOutput();
            client.shutdownInput();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Returning");
        }
    }
}

