package yybos.hash.katarakt2.Socket.Objects;

/*
 *   The message will run through json
 *   Ex: {
 *       "message": "message bla bla bla",
 *       "type": "Message"
 *       "size": "19"
 *   /0}
 */

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;

public class Message extends PacketObject {
    public int getId () {
        return this.id;
    }
    public String getMessage () {
        return this.e;
    }
    public int getChat () {
        return this.a;
    }
    public String getUsername () {
        return this.f;
    }
    public int getUserId () {
        return this.b;
    }
    public Date getDate () {
        return this.date;
    }

    public static Message toMessage (String message, int chat, String username, int userId) {
        Message from = new Message();
        from.type = Type.Message;
        from.e = message;
        from.a = chat;
        from.f = username;
        from.b = userId;
        from.date = new Date(System.currentTimeMillis());

        return from;
    }
    public static Message toMessage (String message, String username) {
        Message from = new Message();
        from.type = Type.Message;
        from.e = message;
        from.f = username;
        from.date = new Date(System.currentTimeMillis());

        return from;
    }
    public static Message fromString (String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new ObjectDateDeserializer());
        //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

        Gson parser = gsonBuilder.serializeNulls().create();

        return parser.fromJson(json, Message.class);
    }

    @NonNull
    @Override
    public String toString() {
        return "{\nid: " + this.id +
                ",\ntype: '" + this.type +
                "',\nmessage: '" + this.e +
                ",\ndate: " + this.date +
                ",\nchat: " + this.a +
                ",\nuser: " + this.b + "\n}\n";
    }
}
