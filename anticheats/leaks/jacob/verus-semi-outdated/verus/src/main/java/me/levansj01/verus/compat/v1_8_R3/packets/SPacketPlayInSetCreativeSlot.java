package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import net.minecraft.server.v1_8_R3.*;

public class SPacketPlayInSetCreativeSlot extends VPacketPlayInSetCreativeSlot
{
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayInSetCreativeSlot)o);
    }
    
    public void accept(final PacketPlayInSetCreativeSlot packetPlayInSetCreativeSlot) {
    }
}
