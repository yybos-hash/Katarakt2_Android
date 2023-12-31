package yybos.hash.katarakt2.Socket.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;

public class Command extends PacketObject {
    public String getCommand () {
        return this.e;
    }
    public int getA () {
        return this.a;
    }
    public int getB () {
        return this.b;
    }
    public int getC () {
        return this.c;
    }
    public int getD () {
        return this.d;
    }
    public String getE () {
        return this.e;
    }
    public String getF () {
        return this.f;
    }
    public String getG () {
        return this.g;
    }
    public String getH () {
        return this.h;
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
    public static Command createChat (String newChat) {
        Command from = new Command();
        from.type = Type.Command;
        from.e = "createChat";
        from.f = newChat;

        return from;
    }
    public static Command deleteChat(int id) {
        Command from = new Command();
        from.type = Type.Command;
        from.e = "deleteChat";
        from.a = id;

        return from;
    }

    public static Command toCommand (String command) {
        Command from = new Command();
        from.type = Type.Command;
        from.e = command;

        return from;
    }
    public static Command toCommand (String command, String args) {
        Command from = new Command();
        from.type = Type.Command;
        from.e = command;
        from.f = args;

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
