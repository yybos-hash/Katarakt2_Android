package yybos.hash.katarakt2.Socket.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Timestamp;

public class Login extends PacketObject {
    private String version;
    private String email;
    private String password;
    private String username;
    private int server;

    public Login () {
        super.setType(PacketObject.Type.Login.getValue());
    }

    public String getVersion () {
        return this.version;
    }
    public int getServer () {
        return this.server;
    }
    public String getEmail () {
        return this.email;
    }
    public String getPassword () {
        return this.password;
    }
    public String getUsername() {
        return this.username;
    }

    public static Login toLogin (String version, int server, String email, String password) {
        Login from = new Login();
        from.server = server;
        from.version = version;
        from.email = email;
        from.password = password;

        return from;
    }
    public static Login fromString (String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Timestamp.class, new ObjectDateDeserializer());
        //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

        Gson parser = gsonBuilder.serializeNulls().create();

        return parser.fromJson(json, Login.class);
    }
}