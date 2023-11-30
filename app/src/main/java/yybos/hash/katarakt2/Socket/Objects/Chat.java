package yybos.hash.katarakt2.Socket.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;

public class Chat extends PacketObject {
    public String getName () {
        return this.e;
    }

    public static Chat toChat (int id, int user, String name) {
        Chat chat = new Chat();
        chat.type = Type.Chat;
        chat.id = id;
        chat.a = user;
        chat.e = name;

        return chat;
    }
    public static Chat fromString (String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new ObjectDateDeserializer());
        //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

        Gson parser = gsonBuilder.serializeNulls().create();

        return parser.fromJson(json, Chat.class);
    }
}


