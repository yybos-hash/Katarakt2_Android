package yybos.hash.katarakt2.Socket;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Constants {
    public static final String version = "1.0.0";

    // this is not used anymore. It's only here because yes
    public static final String server = "10.0.2.2";
    public static final int managerPort = 5135;
    public static final int messagePort = managerPort + 1;
    public static final int mediaPort = managerPort + 2;

    public static byte[] buffer = new byte[2048];
    public static Charset encoding = StandardCharsets.UTF_8;

    public static final String serversListFilename = "servers_list.json";
}
