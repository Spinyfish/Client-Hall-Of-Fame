package dev.tenacity.module.impl.misc;

import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.utils.server.PacketUtils;
import net.minecraft.network.play.client.C16PacketClientStatus;

public final class AutoRespawn extends Module {

    @Override
    public void onTickEvent(TickEvent event) {
        if (mc.thePlayer != null && mc.thePlayer.isDead) {
            PacketUtils.sendPacketNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
        }
    }

    public AutoRespawn() {
        super("AutoRespawn", Category.MISC, "automatically respawn");
    }

}
