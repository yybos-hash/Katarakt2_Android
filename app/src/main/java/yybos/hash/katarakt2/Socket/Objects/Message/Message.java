package yybos.hash.katarakt2.Socket.Objects.Message;

/*
 *   The message will run through json
 *   Ex: {
 *       "message": "message bla bla bla",
 *       "type": "Message"
 *       "size": "19"
 *   /0}
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Timestamp;

import yybos.hash.katarakt2.Socket.Objects.ObjectDateDeserializer;
import yybos.hash.katarakt2.Socket.Objects.PacketObject;

public class Message extends PacketObject {
    private String content;
    private int chat;

    private User user = new User();

    public Message () {
        super.setType(PacketObject.Type.Message.getValue());
    }

    public int getId () {
        return this.id;
    }
    public String getMessage () {
        return this.content;
    }
    public int getChat () {
        return this.chat;
    }
    public String getUsername () {
        return this.user.getUsername();
    }
    public User getUser () {
        return this.user;
    }

    public void setMessage (String message) {
        this.content = message;
    }
    public void setChat (int id) {
        this.chat = id;
    }
    public void setUsername (String username) {
        this.user.setUsername(username);
    }

    public void setUser (User user) {
        this.user = user;
    }

    public static Message toMessage (String message, int chat, String username, int userId) {
        Message from = new Message();
        from.content = message;
        from.id = -1;
        from.chat = chat;
        from.user.setUsername(username);
        from.user.setId(userId);
        from.date = System.currentTimeMillis();

        return from;
    }
    public static Message toMessage (String message, int chat, String username) {
        Message from = new Message();
        from.content = message;
        from.id = -1;
        from.chat = chat;
        from.user.setUsername(username);
        from.date = System.currentTimeMillis();

        return from;
    }
    public static Message toMessage (String message, User user) {
        Message from = new Message();
        from.content = message;
        from.id = -1;
        from.user = user;
        from.date = System.currentTimeMillis();

        return from;
    }
    public static Message toMessage (String message, String username) {
        Message from = new Message();
        from.content = message;
        from.chat = 0;
        from.id = -1;
        from.user.setUsername(username);
        from.user.setId(0);
        from.date = System.currentTimeMillis();

        return from;
    }
    public static Message fromString (String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Timestamp.class, new ObjectDateDeserializer());
        //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

        Gson parser = gsonBuilder.serializeNulls().create();

        return parser.fromJson(json, Message.class);
    }

    @Override
    public String toString() {
        return "{\nid: " + this.id +
                ",\ntype: '" + this.type +
                "',\nmessage: '" + this.content +
                ",\ndate: " + this.date +
                ",\nchat: " + this.chat +
                ",\nuser: " + this.user.getId() + "\n}\n";
    }
}
