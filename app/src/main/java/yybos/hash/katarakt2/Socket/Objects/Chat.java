package yybos.hash.katarakt2.Socket.Objects;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;

public class Chat extends PacketObject {
    public String getName () {
        return this.e;
    }

    public static Chat toChat (int id, String name) {
        Chat chat = new Chat();
        chat.type = Type.Chat;
        chat.id = id;
        chat.e = name;
        chat.date = new Date(System.currentTimeMillis());

        return chat;
    }
    public static Chat fromString (String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new ObjectDateDeserializer());
        //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

        Gson parser = gsonBuilder.serializeNulls().create();

        return parser.fromJson(json, Chat.class);
    }

    @NonNull
    @Override
    public String toString() {
        return this.getName() != null ? this.getName() : "Error";
    }
}


