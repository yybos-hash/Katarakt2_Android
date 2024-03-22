package yybos.hash.katarakt2.Socket.Objects.Message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Timestamp;

import yybos.hash.katarakt2.Socket.Objects.ObjectDateDeserializer;
import yybos.hash.katarakt2.Socket.Objects.PacketObject;

public class User extends PacketObject {
    private String username;
    private String email;
    private String password;

    public User () {
        super.setType(PacketObject.Type.User.getValue());
    }

    public String getUsername () {
        return this.username;
    }
    public String getEmail () {
        return this.email;
    }
    public String getPassword () {
        return this.password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setEmail (String email) {
        this.email = email;
    }
    public void setPassword (String password) {
        this.password = password;
    }

    public static User toUser (int id, String username, String email, String password) {
        User from = new User();
        from.id = id;

        from.username = username;
        from.email = email;
        from.password = password;

        return from;
    }
    public static User toUser (int id, String username) {
        User from = new User();
        from.id = id;

        from.username = username;

        return from;
    }
    public static User fromString (String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Timestamp.class, new ObjectDateDeserializer());
        //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

        Gson parser = gsonBuilder.serializeNulls().create();

        return parser.fromJson(json, User.class);
    }
}
