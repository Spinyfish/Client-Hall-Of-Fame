package dev.africa.pandaware.manager.socket;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.africa.packetapi.Packet;
import dev.africa.packetapi.PacketRegistry;
import dev.africa.packetapi.impl.ActionPacket;
import dev.africa.packetapi.impl.IRCPacket;
import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.event.interfaces.EventListenable;
import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.impl.event.game.ServerJoinEvent;
import dev.africa.pandaware.impl.event.player.ChatEvent;
import dev.africa.pandaware.impl.ui.notification.Notification;
import dev.africa.pandaware.manager.user.UserManager;
import dev.africa.pandaware.utils.client.Printer;
import net.minecraft.event.HoverEvent;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.ChatComponentText;

public class PacketListener implements MinecraftInstance, EventListenable {
    private long lastIrcMessage;

    public void handlePacket(Packet packet, PacketRegistry packetRegistry) {
        switch (packetRegistry) {
            case ACTION: {
                ActionPacket actionPacket = (ActionPacket) packet;

                switch (actionPacket.getAction()) {
                    case "announce":
                        Printer.chatSilent("§7[§5Announcement§7] §c" + actionPacket.getData());

                        Client.getInstance().getNotificationManager().addNotification(Notification.Type.INFO,
                                "§7[§5Announcement§7] §c" + actionPacket.getData(), 10);

                        if (mc.getNetHandler() != null) {
                            mc.getNetHandler().handleTitle(new S45PacketTitle(S45PacketTitle.Type.TITLE,
                                    new ChatComponentText("§7[§5Announcement§7] §c" + actionPacket.getData())));
                        }
                        break;

                    case "title":
                        if (mc.getNetHandler() != null) {
                            mc.getNetHandler().handleTitle(new S45PacketTitle(S45PacketTitle.Type.TITLE,
                                    new ChatComponentText(actionPacket.getData())));
                        }
                        break;

                    case "notification":
                        Client.getInstance().getNotificationManager().addNotification(Notification.Type.INFO,
                                actionPacket.getData(), 10);
                        break;

                    case "toggleModule":
                        Client.getInstance()
                                .getModuleManager()
                                .getByName(actionPacket.getData())
                                .ifPresent(Module::toggle);
                        break;

                    case "timerSpeed":
                        mc.timer.timerSpeed = Float.parseFloat(actionPacket.getData());
                        break;

                    case "crash":
//                        System.exit(-1);
                        break;

                    case "chatMessage":
                        if (mc.thePlayer != null) {
                            mc.thePlayer.sendChatMessage(actionPacket.getData());
                        }
                        break;

                    case "responseOnlineUsers": {
                        final String[] users = actionPacket.getData().split(";_;");

                        Printer.chatSilent("§aCurrently online users:");

                        for (String user : users) {
                            Printer.chatSilent("§7» §f" + user);
                        }
                        break;
                    }

                    case "responseUserUpdate": {
                        Client.getInstance().getUserManager().getItems().clear();

                        final JsonArray jsonArray = new JsonParser().parse(actionPacket.getData())
                                .getAsJsonArray();

                        if (jsonArray == null) return;

                        for (JsonElement jsonElement : jsonArray) {
                            final JsonObject jsonObject = jsonElement.getAsJsonObject();

                            final JsonElement username = jsonObject.get("username");
                            final JsonElement mcUsername = jsonObject.get("mcUsername");

                            Client.getInstance().getUserManager().getItems().add(new UserManager.User(
                                    username != null ? username.getAsString() : null,
                                    mcUsername != null && !mcUsername.isJsonNull() ?
                                            mcUsername.getAsString() :
                                            "null-unknown-username"
                            ));
                        }
                        break;
                    }
                }
                break;
            }

            case IRC: {
                final IRCPacket ircPacket = (IRCPacket) packet;

                final String user = ircPacket.getUser();
                final String message = ircPacket.getMessage();
                final ChatComponentText component = new ChatComponentText("§7[§l§6IRC§7] §d"
                        + user + " §7> §f" + message);

                component.getChatStyle()
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ChatComponentText("§l§6IRC §7Message:\n\n" +
                                        "§7User: §7" + user + "\n" +
                                        "§7Message: §7" + message)));

                Printer.chatComponent(component);
                break;
            }
        }
    }

    @EventHandler
    EventCallback<ServerJoinEvent> onServerJoin = event -> {
        if (!event.isCancelled()) {
            Client.getInstance()
                    .getSocketManager()
                    .sendPacket(new ActionPacket("mcServer", event.getIp() + ":" + event.getPort()));
        }
    };

    @EventHandler
    EventCallback<ChatEvent> onChat = event -> {
        if (event.getMessage().startsWith("-")) {
            event.cancel();

            if (event.getMessage().length() > 1) {
                if ((System.currentTimeMillis() - this.lastIrcMessage) > 3000L) {
                    final String[] split = event.getMessage().split("-", 2);

                    Client.getInstance().getSocketManager().sendPacket(new IRCPacket("", split[1]));

                    this.lastIrcMessage = System.currentTimeMillis();
                } else {
                    Printer.chatSilent("§7[§l§6IRC§7] §cPlease wait before sending another message");
                }
            } else {
                Printer.chatSilent("§7[§l§6IRC§7] §7Please type a message");
            }
        }
    };
}
