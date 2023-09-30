/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.server.S02PacketChat;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventUpdate;
import wtf.monsoon.impl.ui.notification.NotificationType;

public class ChatSpammer
extends Module {
    private String username = "";
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.HYPIXEL).describedBy("The manner in which to spam users.");
    private final Setting<Long> delay = new Setting<Long>("Delay", 100L).minimum(0L).maximum(5000L).incrementation(50L).describedBy("The manner in which to spam users.");
    private final Timer timer = new Timer();
    private int stage;
    @EventLink
    private final Listener<EventUpdate> eventUpdateListener = e -> {
        switch (this.mode.getValue()) {
            case HYPIXEL: {
                if (this.username.equals("")) {
                    Wrapper.getNotifManager().notify(NotificationType.ERROR, "Spammer", "Please do .spam <username> to set a user to spam.");
                    this.toggle();
                }
                if (!this.timer.hasTimeElapsed(this.delay.getValue(), true)) break;
                switch (this.stage) {
                    case 0: {
                        this.mc.thePlayer.sendChatMessage("/party " + this.getUsername());
                        ++this.stage;
                        break;
                    }
                    case 1: {
                        this.mc.thePlayer.sendChatMessage("/party disband");
                        this.stage = 0;
                    }
                }
                break;
            }
            case JIBBERISH: {
                if (!this.timer.hasTimeElapsed(this.delay.getValue(), true)) break;
                this.mc.thePlayer.sendChatMessage(StringUtil.getRandomString(10));
            }
        }
    };
    @EventLink
    private final Listener<EventPacket> eventPacketListener = e -> {
        String[] badStrings = new String[]{"-----------------------------------------------------", " to the party! They have 60 seconds to accept.", " has disbanded the party!"};
        if (e.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat)e.getPacket();
            for (String s : badStrings) {
                if (!packet.getChatComponent().getUnformattedText().contains("-----------------------------------------------------")) continue;
                e.setCancelled(true);
            }
            if (packet.getChatComponent().getUnformattedText().contains("You cannot invite that player since they're not online.") || packet.getChatComponent().getUnformattedText().contains("You are not in a party right now.")) {
                Wrapper.getNotifManager().notify(NotificationType.ERROR, "Spammer", "Couldn't spam player, toggling...");
                this.username = "";
                this.toggle();
            }
        }
    };

    public ChatSpammer() {
        super("Chat Spammer", "Automatically chat", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.timer.reset();
        this.stage = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Setting<Mode> getMode() {
        return this.mode;
    }

    public Setting<Long> getDelay() {
        return this.delay;
    }

    public static enum Mode {
        HYPIXEL,
        JIBBERISH;

    }
}

