package me.levansj01.verus.compat.v1_7_R4.packets;

import me.levansj01.verus.compat.packets.VPacketPlayInHeldItemSlot;
import net.minecraft.server.v1_7_R4.PacketPlayInHeldItemSlot;

public class SPacketPlayInHeldItemSlot
extends VPacketPlayInHeldItemSlot {
    public void accept(PacketPlayInHeldItemSlot packetPlayInHeldItemSlot) {
        this.slot = packetPlayInHeldItemSlot.c();
    }
}

