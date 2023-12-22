package yybos.hash.katarakt2.Socket.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;

public class PacketObject {
    protected Type type;
    protected int id;

    // these are like 'arguments' (Should work, right?)
    protected int a;
    protected int b;
    protected int c;
    protected int d;
    protected String e;
    protected String f;
    protected String g;
    protected String h;
    protected Date date;

    public int getId() {
        return this.id;
    }
    public Type getType () {
        return this.type;
    }
    public Date getDate () {
        return this.date;
    }

    public enum Type {
        Message(0),
        Command(1),
        Version(2),
        User(3),
        Login(4),
        Chat(5);

        private final int value;

        Type (int value) {
            this.value = value;
        }

        public int getValue () {
            return value;
        }
        public static yybos.hash.katarakt2.Socket.Objects.Message.Type getEnumByValue (int value) {
            for (yybos.hash.katarakt2.Socket.Objects.Message.Type enumValue : yybos.hash.katarakt2.Socket.Objects.Message.Type.values()) {
                if (enumValue.getValue() == value) {
                    return enumValue;
                }
            }
            throw new IllegalArgumentException("No type with value " + value);
        }
    }

    public static PacketObject fromString (String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new ObjectDateDeserializer());
        //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

        Gson parser = gsonBuilder.serializeNulls().create();

        return parser.fromJson(json, PacketObject.class);
    }
}
