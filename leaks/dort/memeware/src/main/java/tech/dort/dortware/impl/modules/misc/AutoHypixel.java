package tech.dort.dortware.impl.modules.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.networking.PacketUtil;

public class AutoHypixel extends Module {

    private final EnumValue<Mode> mode = new EnumValue<>("Mode", this, AutoHypixel.Mode.values());
    private final BooleanValue autoGG = new BooleanValue("Auto GG", this, true);
    private final BooleanValue autoPlay = new BooleanValue("Auto Play", this, true);
    public static String ggMsg = "GG";
    private boolean sendPlay;

    public AutoHypixel(ModuleData moduleData) {
        super(moduleData);
        register(mode, autoGG, autoPlay);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (sendPlay) {
            PacketUtil.sendPacketNoEvent(new C01PacketChatMessage(String.format("/play %s", mode.getValue().name().toLowerCase())));
            sendPlay = false;
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S02PacketChat) {
            final S02PacketChat packetChat = event.getPacket();

            switch (packetChat.getChatComponent().getUnformattedText()) {
                case "You won! Want to play again? Click here! ":
                    sendPlay = autoPlay.getValue();

                    // TODO save gg message like module settings
                    if (autoGG.getValue()) {
                        mc.thePlayer.sendChatMessage(ggMsg);
                    }
                    break;

                case "You died! Want to play again? Click here! ":
                    sendPlay = autoPlay.getValue();
                    break;
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        sendPlay = false;
    }

    private enum Mode implements INameable {
        SOLO_INSANE("Solo Insane"), TEAMS_INSANE("Teams Insane"), SOLO_NORMAL("Solo Normal"), TEAMS_NORMAL("Teams Normal");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return this.name;
        }
    }
}
