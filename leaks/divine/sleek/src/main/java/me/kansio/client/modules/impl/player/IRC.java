package me.kansio.client.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.Client;
import me.kansio.client.event.impl.ChatEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.irc.IRCClient;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.utils.math.Stopwatch;
import me.kansio.client.utils.chat.ChatUtil;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ModuleData(
        name = "IRC",
        category = ModuleCategory.PLAYER,
        description = "Let's you chat with other client users"
)
public class IRC extends Module {

    public IRC() {
        super("IRC", ModuleCategory.PLAYER);
    }

    private IRCClient client;
    Stopwatch time = new Stopwatch();


    public void onEnable() {
        time.resetTime();

        try {
            client = new IRCClient();
            client.connectBlocking();

        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onDisable() {
        client.close();
        client = null;
    }

    @Subscribe
    public void onChat(ChatEvent event) {
        String message = event.getMessage();

        if (message.startsWith("-") || message.startsWith("- ")) {
            event.setCancelled(true);
            client.send(event.getMessage().replace("-", ""));
        }
    }
}
