package yybos.hash.katarakt2.Socket.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;

public class Command extends PacketObject {
    public String getCommand () {
        return this.e;
    }

    public static Command getChats () {
        Command from = new Command();
        from.type = Type.Command;
        from.e = "getChats";

        return from;
    }
    public static Command getChatHistory (int chatId) {
        Command from = new Command();
        from.type = Type.Command;
        from.e = "getChatHistory";
        from.a = chatId;

        return from;
    }
    public static Command setUsername (String username) {
        Command from = new Command();
        from.type = Type.Command;
        from.e = "setUsername";
        from.f = username;

        return from;
    }
    public static Command fromString (String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new ObjectDateDeserializer());
        //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

        Gson parser = gsonBuilder.serializeNulls().create();

        return parser.fromJson(json, Command.class);
    }
}
