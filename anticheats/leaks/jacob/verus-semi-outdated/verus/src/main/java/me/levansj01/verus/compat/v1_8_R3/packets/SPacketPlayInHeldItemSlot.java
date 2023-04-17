package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import net.minecraft.server.v1_8_R3.*;

public class SPacketPlayInHeldItemSlot extends VPacketPlayInHeldItemSlot
{
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayInHeldItemSlot)o);
    }
    
    public void accept(final PacketPlayInHeldItemSlot packetPlayInHeldItemSlot) {
        this.slot = packetPlayInHeldItemSlot.a();
    }
}
