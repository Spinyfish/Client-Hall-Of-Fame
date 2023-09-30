package dev.africa.pandaware.impl.module.misc;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.game.TickEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.notification.Notification;
import lombok.AllArgsConstructor;
import net.minecraft.network.play.server.S02PacketChat;

@ModuleInfo(name = "AutoJoin")
public class AutoJoinModule extends Module {
    private final EnumSetting<JoinType> mode = new EnumSetting<>("Mode", JoinType.SOLO_INSANE);
    private final NumberSetting delay = new NumberSetting("Delay", 5000, 100, 1000, 100);
    private boolean startTimer;
    private long startDelay;

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof S02PacketChat && mc.thePlayer != null) {
            S02PacketChat packet = event.getPacket();

            String message = packet.getChatComponent().getUnformattedText();
            if (message.contains("1st Killer -") || message.contains("You died! Want to play again? Click here!") ||
                    message.contains("You won! Want to play again?")) {
                if (!startTimer) {
                    startTimer = true;
                    Client.getInstance().getNotificationManager().addNotification(Notification.Type.SUCCESS,"Playing Again!", "Joining a new Game", delay.getValue().longValue() / 1000F);
                    startDelay = System.currentTimeMillis();
                }
            }
            if (message.contains("1st Killer -")) {
                mc.thePlayer.sendChatMessage("/ac gg");
            }
        }
    };

    @EventHandler
    EventCallback<TickEvent> onUpdate = event -> {
        if (startTimer && mc.thePlayer != null) {
            if (System.currentTimeMillis() - startDelay >= delay.getValue().longValue()) {
                String text = "/play " + mode.getValue().label.replace(" ", "_").toLowerCase();
                mc.thePlayer.sendChatMessage(text);
                startTimer = false;
            }
        }
    };

    public AutoJoinModule() {
        this.registerSettings(
                this.mode,
                this.delay
        );
    }

    @AllArgsConstructor
    private enum JoinType {
        SOLO_INSANE("Solo Insane"),
        TEAMS_INSANE("Teams Insane"),
        SOLO_NORMAL("Solo Normal"),
        TEAMS_NORMAL("Teams Normal");

        private String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
