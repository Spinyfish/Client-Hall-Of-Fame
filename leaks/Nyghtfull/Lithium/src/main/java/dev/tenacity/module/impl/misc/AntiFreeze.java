package dev.tenacity.module.impl.misc;

import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

public final class AntiFreeze extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Normal", "Normal", "Teleport");

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S2DPacketOpenWindow
                && ((S2DPacketOpenWindow) event.getPacket()).getWindowTitle().getUnformattedText().contains("frozen")) {
            event.cancel();
        } else if (event.getPacket() instanceof S02PacketChat
                && ((S02PacketChat) event.getPacket()).getChatComponent().getUnformattedText().contains("frozen")) {
            if (mode.is("Teleport")) {
                mc.thePlayer.posY = -999;
            }
            event.cancel();
        }
    }

    public AntiFreeze() {
        super("AntiFreeze", Category.MISC, "prevents server plugins from freezing you");
    }

}
