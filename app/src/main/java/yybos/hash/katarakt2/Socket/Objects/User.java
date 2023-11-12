package yybos.hash.katarakt2.Socket.Objects;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;

public class User {
    private int id;
    private String name;
    private String pass;

    public int getId () {
        return id;
    }
    public String getName () {
        return name;
    }
    public String getPass () {
        return this.pass;
    }

    public void setId (int id) {
        this.id = id;
    }
    public void setName (String name) {
        this.name = name;
    }
    public void setPass (String pass) {
        this.pass = pass;
    }

    @NonNull
    public static User fromString (String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new ObjectDateDeserializer());
        //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

        Gson userParser = gsonBuilder.create();

        return userParser.fromJson(json, User.class);
    }
}
