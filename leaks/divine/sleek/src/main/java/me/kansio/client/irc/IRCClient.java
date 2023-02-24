package me.kansio.client.irc;

import me.kansio.client.Client;
import me.kansio.client.modules.impl.player.IRC;
import me.kansio.client.utils.chat.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.LogManager;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class IRCClient extends WebSocketClient {


    public static char SPLIT = '\u0000';

    public IRCClient() throws URISyntaxException {
        super(new URI("ws://zerotwoclient.xyz:1337"));
        this.setAttachment(Client.getInstance().getRank().getColor().toString().replace("§", "&") + Client.getInstance().getUsername());
        this.addHeader("name", this.getAttachment());
        this.addHeader("uid", Client.getInstance().getUid());
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("IRC Connected");
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§7[§bIRC§7] §f" + "Connected"));
    }

    @Override
    public void onMessage(String s) {
        System.out.println(s);

        if (s.contains(Character.toString(SPLIT))) {

            String split[] = s.split(Character.toString(SPLIT));
            if (split.length != 3) {
                return;
            }
            String username = split[0];
            String uid = split[1];
            String message = split[2];
            uid = uid.replace("(", "§7(§b").replace(")", "§7)");

            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ChatUtil.translateColorCodes("§7[§bIRC§7] §b" + username + uid + " " + "§f: " + message)));
        } else {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§7[§bIRC§7] " + s));
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§7[§bIRC§7] §f" + "Disconnected"));

        IRC irc = (IRC) Client.getInstance().getModuleManager().getModuleByName("IRC");

        if (irc.isToggled()) {
            irc.toggle();
        }
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

}
