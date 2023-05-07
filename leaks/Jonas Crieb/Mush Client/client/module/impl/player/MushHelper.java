package client.module.impl.player;
import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.packet.PacketReceiveEvent;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;
import client.util.chat.ChatUtil;
import net.minecraft.network.play.server.S02PacketChat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.ScheduledExecutorService;

@ModuleInfo(name = "MushHelper", description = "", category = Category.OTHER)
public class MushHelper extends Module {

    @EventLink()
    public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
        if (event.getPacket() instanceof S02PacketChat) {
            final S02PacketChat packet = (S02PacketChat) event.getPacket();
            String message = packet.getChatComponent().getUnformattedText();

            if (message.contains("AVISO:")) {

               
                mc.thePlayer.sendChatMessage("str3am! https://youtube.com/live/QVPjtS6UVAM");
            }
        }

    };
}