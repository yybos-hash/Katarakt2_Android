package yybos.hash.katarakt2.Socket.Objects;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;

// this one wont extend packetObject because it wont be sent over socket
public class Server {
    public String serverIp;
    public int serverPort;

    public String email;
    public String password;

    public boolean isDefault;

    public void set (Server server) {
        this.serverIp = server.serverIp;
        this.serverPort = server.serverPort;

        this.email = server.email;
        this.password = server.password;

        this.isDefault = server.isDefault;
    }

    @NonNull
    public static Server fromString (String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new ObjectDateDeserializer());
        //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

        Gson parser = gsonBuilder.serializeNulls().create();

        return parser.fromJson(json, Server.class);
    }
}
