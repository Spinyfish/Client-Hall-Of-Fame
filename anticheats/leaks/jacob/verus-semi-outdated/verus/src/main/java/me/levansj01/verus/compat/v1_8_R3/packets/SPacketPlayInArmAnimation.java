package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import net.minecraft.server.v1_8_R3.*;

public class SPacketPlayInArmAnimation extends VPacketPlayInArmAnimation
{
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayInArmAnimation)o);
    }
    
    public void accept(final PacketPlayInArmAnimation packetPlayInArmAnimation) {
    }
}
