package yybos.hash.katarakt2.Socket.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;

public class Chat {
    private int id;
    private int user;
    private String nm;

    public int getId () {
        return this.id;
    }
    public int getUser () {
        return this.user;
    }
    public String getName () {
        return this.nm;
    }

    public void setId (int id) {
        this.id = id;
    }
    public void setUser (int id) {
        this.user = id;
    }
    public void setName (String name) {
        this.nm = name;
    }

    public static Chat toChat (int id, int user, String name) {
        Chat chat = new Chat();
        chat.setId(id);
        chat.setUser(user);
        chat.setName(name);

        return chat;
    }
    public static Chat fromString (String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new ObjectDateDeserializer());
        //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

        Gson chatParser = gsonBuilder.create();

        return chatParser.fromJson(json, Chat.class);
    }
}


