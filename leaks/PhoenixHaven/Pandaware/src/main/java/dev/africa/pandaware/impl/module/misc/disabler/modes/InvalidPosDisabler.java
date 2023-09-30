package dev.africa.pandaware.impl.module.misc.disabler.modes;

import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.module.misc.disabler.DisablerModule;
import net.minecraft.network.play.client.C03PacketPlayer;

public class InvalidPosDisabler extends ModuleMode<DisablerModule> {
    public InvalidPosDisabler(String name, DisablerModule parent) {
        super(name, parent);
    }

    @Override
    public void onEnable() {
        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
                Float.MAX_VALUE, 69, Float.MIN_VALUE, true
        ));
    }
}
