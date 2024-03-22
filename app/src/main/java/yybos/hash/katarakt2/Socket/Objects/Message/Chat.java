package yybos.hash.katarakt2.Socket.Objects.Message;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Timestamp;

import yybos.hash.katarakt2.Socket.Objects.ObjectDateDeserializer;
import yybos.hash.katarakt2.Socket.Objects.PacketObject;

public class Chat extends PacketObject {
    private String name;
    private int userId;

    public Chat () {
        super.setType(PacketObject.Type.Chat.getValue());
    }

    public void setName (String name) {
        this.name = name;
    }
    public void setUser (int id) {
        this.userId = id;
    }

    public String getName () {
        return this.name;
    }

    public static Chat toChat (int id, int user, String name) {
        Chat chat = new Chat();
        chat.id = id;
        chat.userId = user;
        chat.name = name;

        return chat;
    }
    public static Chat toChat (int id, int user, long date, String name) {
        Chat chat = new Chat();
        chat.id = id;
        chat.userId = user;
        chat.name = name;
        chat.date = date;

        return chat;
    }
    public static Chat fromString (String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Timestamp.class, new ObjectDateDeserializer());
        //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

        Gson parser = gsonBuilder.serializeNulls().create();

        return parser.fromJson(json, Chat.class);
    }

    @NonNull
    @Override
    public String toString () {
        return this.getName() != null ? this.getName() : "Error";
    }
}
