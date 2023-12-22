package yybos.hash.katarakt2.Socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

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

    public void sendObject(Object obj) {
        if (obj == null)
            return;

        //
        SimpleDateFormat customDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, (JsonSerializer<Date>) (src, typeOfSrc, context) -> context.serialize(customDateFormat.format(src)));
        //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

        Gson objParser = gsonBuilder.create();

        String text = objParser.toJson(obj) + '\0';

        try {
            send(text);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Returning");
        }
    }
    public void sendRawMessage(String message) {
        if (message == null)
            return;

        try {
            send(message.replace("\0", ""));
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
        }
    }
}

