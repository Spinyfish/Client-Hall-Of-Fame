package crispy.util.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import crispy.Crispy;
import crispy.features.hacks.impl.render.HUD;
import crispy.util.MinecraftUtil;
import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.DnsResolver;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;


public class IRC implements MinecraftUtil {

    private double hb;
    private Socket socket;
    private DnsResolver dnsResolver;

    public IRC() {

        try {
            connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new IRC();
    }

    public void sendMessage(String message) {
        JsonObject object = new JsonObject();
        object.addProperty("message", message);
        if (message.startsWith("/")) {
            String subMessage = message.substring(1);
            String[] split = subMessage.split(" ");
            String command = split[0];
            String arg = subMessage.substring(command.length()).trim();
            JsonObject obj = new JsonObject();
            obj.addProperty("cmd", command);
            obj.addProperty("args", arg);

            socket.emit("command", obj.toString());
        } else {
            socket.emit("chat", object);
        }
    }

    public void connect() throws URISyntaxException {

        IO.protocol = 4;
        IO.Options options = IO.Options.builder()
                .setReconnection(true)
                .setPath("/api/crispy/chatroom")
                .build();

        options.forceNew = true;

        socket = IO.socket(URI.create("https://crispyclient.herokuapp.com"), options);
        socket.connect();
        socket.on("connect", (data) -> {

            System.out.println("Logging in....");
            JsonObject json = new JsonObject();

            json.addProperty("hwid", Crispy.INSTANCE.getHwid());
            json.addProperty("dID", Crispy.INSTANCE.getDiscordID());

            socket.emit("login", json);
        });
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                socket.emit("keepAlive");
            }
        }).start();
        socket.on("UserCount", (amount) -> {
            if (Minecraft.theWorld != null) {
                Crispy.addChatMessage("Usercount: " + Arrays.toString(amount));
            }
        });
        socket.on("PM", (objects) -> {
            JsonObject object = (JsonObject) new JsonParser().parse(objects[0].toString());
            String user = object.get("sender").getAsString();
            String message = object.get("msg").getAsString();
            if (user != null && message != null) {
                if (Minecraft.theWorld != null) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\247b[PM]\2477» " + "\2473" + user + " \2477| " + StringUtils.normalizeSpace(message)));
                }
            }
        });
        socket.on("authfail", objects -> {
            //System.exit(0);
        });
        socket.on("chat", objects -> {
            HUD hud = Crispy.INSTANCE.getHackManager().getHack(HUD.class);
            if (hud.irc.getObject() && hud.isEnabled()) {
                JsonParser jsonParser = new JsonParser();
                String obj = String.valueOf(objects[0]);
                JsonObject jsonElement = (JsonObject) jsonParser.parse(obj);
                String message = jsonElement.get("message").getAsString();
                String user = jsonElement.get("user").getAsString();
                if (Minecraft.theWorld != null) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\247b[IRC]\2477  " + "\2473" + user + " \2477| " + StringUtils.normalizeSpace(message)));
                }
            }
        });
        socket.on("disconnect", data -> {
            System.out.println("Disconnected " + data[0]);
        });
        socket.io().on(Manager.EVENT_RECONNECT_ATTEMPT, args -> System.out.println("Attempting to reconnect to the irc"));

    }

}
