package yybos.hash.katarakt2.Socket.Objects.Message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.sql.Timestamp;

import yybos.hash.katarakt2.Socket.Objects.ObjectDateDeserializer;
import yybos.hash.katarakt2.Socket.Objects.PacketObject;

public class Command extends PacketObject {
    private String command;
    private final JsonObject args = new JsonObject();

    public Command () {
        super.setType(PacketObject.Type.Command.getValue());
    }

    public String getCommand () {
        return this.command;
    }

    public String getString (String key) {
        return args.get(key).getAsString();
    }
    public int getInt (String key) {
        return args.get(key).getAsInt();
    }

    public static Command getChats () {
        Command from = new Command();
        from.type = Type.Command.getValue();
        from.command = "getChats";

        return from;
    }
    public static Command getChatHistory (int chatId) {
        Command from = new Command();
        from.type = Type.Command.getValue();
        from.command = "getChatHistory";
        from.args.addProperty("chatId", chatId);

        return from;
    }
    public static Command setUsername (String username) {
        Command from = new Command();
        from.type = Type.Command.getValue();
        from.command = "setUsername";
        from.args.addProperty("username", username);

        return from;
    }
    public static Command createChat (String newChat) {
        Command from = new Command();
        from.type = Type.Command.getValue();
        from.command = "createChat";
        from.args.addProperty("chat", newChat);

        return from;
    }
    public static Command deleteChat (int id) {
        Command from = new Command();
        from.type = Type.Command.getValue();
        from.command = "deleteChat";
        from.args.addProperty("chat", id);

        return from;
    }
    public static Command prompt (String prompt) {
        Command from = new Command();
        from.type = Type.Command.getValue();
        from.command = "cmd";
        from.args.addProperty("prompt", prompt);

        return from;
    }

    public static Command getDirectories (String path) {
        Command from = new Command();
        from.type = Type.Command.getValue();
        from.command = "getSubs";
        from.args.addProperty("path", path);

        return from;
    }

    // otaku

    public static Command getAnimeList () {
        Command from = new Command();
        from.type = Type.Command.getValue();
        from.command = "getAnimeList";

        return from;
    }

    public static Command toCommand (String command) {
        Command from = new Command();
        from.type = Type.Command.getValue();
        from.command = command;

        return from;
    }

    public static Command fromString (String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Timestamp.class, new ObjectDateDeserializer());
        //  Basically when gson formats a Date in the sql.Date format it changes the format, so this keeps the it as it should

        Gson parser = gsonBuilder.serializeNulls().create();

        return parser.fromJson(json, Command.class);
    }
}
