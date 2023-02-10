/**
 * @project Myth
 * @author CodeMan
 * @at 27.10.22, 12:06
 */
package dev.myth.features.player;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.feature.Feature;
import dev.myth.events.PacketEvent;
import dev.myth.settings.NumberSetting;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

@Feature.Info(
        name = "AutoPlay",
        description = "Automatically joins the next game",
        category = Feature.Category.PLAYER
)
public class AutoPlayFeature extends Feature {

    public final NumberSetting delay = new NumberSetting("Delay", 0, 0, 3000, 100).addValueAlias(0, "Instant");

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = event.getPacket();
            IChatComponent chatComponent = packet.getChatComponent();
            if (chatComponent == null) return;
            String message = chatComponent.getFormattedText();
            if (message == null) return;
            if (message.endsWith(" §r§eWant to play again?§r§b§l Click here! §r") || message.contains("AQUI")) {
                ChatStyle chatStyle = chatComponent.getSiblings().get(chatComponent.getSiblings().size() - 1).getChatStyle();
                if (chatStyle == null) return;
                ClickEvent clickEvent = chatStyle.getChatClickEvent();
                if (clickEvent == null) return;
                if (clickEvent.getAction() == null) return;
                if (clickEvent.getValue() == null) return;
                if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    if(delay.getValue() == 0) {
                        getPlayer().sendChatMessage(clickEvent.getValue());
                    } else {
                        new Thread(() -> {
                            try {
                                Thread.sleep(delay.getValue().longValue());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            getPlayer().sendChatMessage(clickEvent.getValue());
                        }).start();
                    }
                }
            }
        }
    };

}
