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
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Message {
    private int id;
    private Type type;

    private String message;
    private int size;
    private Date dt;
    private int chat;
    private String user;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private int userId;

    public int getId () {
        return id;
    }
    public Type getType () {
        return type;
    }
    public String getContent() {
        return message;
    }
    public int getSize () {
        return size;
    }
    public Date getDate () {
        return dt;
    }
    public int getChat () {
        return chat;
    }
    public String getUser () {
        return user;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setType(int type) {
        this.type = Type.getEnumByValue(type);
    }
    public void setContent(String message) {
        this.message = message;
        this.size = message.length();
    }
    public void setDate(Date dt) {
        this.dt = dt;
    }
    public void setChat(int chat) {
        this.chat = chat;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public static Message toMessage (Message.Type type, String message, int chat, String user, int userId) {
        Message from = new Message();
        from.type = type;
        from.message = message;
        from.size = message.length();
        from.chat = chat;
        from.user = user;
        from.userId = userId;
        from.dt = new Date(System.currentTimeMillis());

        return from;
    }
    public static Message fromString (String json) {
        //
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Date.class, new CustomDateDeserializer());
        //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

        Gson messageParser = gsonBuilder.create();

        return messageParser.fromJson(json, Message.class);
    }

    public enum Type {
        Message(0),
        Command(1),
        Version(2);

        private final int value;

        Type (int value) {
            this.value = value;
        }

        public int getValue () {
            return value;
        }
        public static Type getEnumByValue (int value) {
            for (Type enumValue : Type.values()) {
                if (enumValue.getValue() == value) {
                    return enumValue;
                }
            }
            throw new IllegalArgumentException("No type with value " + value);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "{\nid: " + this.id +
                ",\ntype: '" + this.type +
                "',\nmessage: '" + this.message +
                "',\nsize: " + this.size +
                ",\ndate: " + this.dt +
                ",\nchat: " + this.chat +
                ",\nuser: " + this.user + "\n}\n";
    }
}

// ignore this. This is here because of that date parser shit on the gson
class CustomDateDeserializer implements JsonDeserializer<java.sql.Date> {
    private final SimpleDateFormat customDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String dateStr = json.getAsString();
        try {
            java.util.Date utilDate = customDateFormat.parse(dateStr);
            return new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }
}