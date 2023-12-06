package yybos.hash.katarakt2.Socket.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;

public class Login extends PacketObject {
    public String getVersion () {
        return this.e;
    }
    public int getServer () {
        return this.a;
    }
    public String getEmail () {
        return this.f;
    }
    public String getPassword () {
        return this.g;
    }

    public static Login toLogin (String version, int server, String email, String password) {
        Login from = new Login();
        from.type = Type.Login;
        from.a = server;
        from.e = version;
        from.f = email;
        from.g = password;

        return from;
    }
    public static Login fromString (String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new ObjectDateDeserializer());
        //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

        Gson parser = gsonBuilder.serializeNulls().create();

        return parser.fromJson(json, Login.class);
    }
}