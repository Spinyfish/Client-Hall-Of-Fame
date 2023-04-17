package me.levansj01.verus.compat.v1_7_R4.packets;

import me.levansj01.verus.compat.packets.VPacketPlayInTransaction;
import net.minecraft.server.v1_7_R4.PacketPlayInTransaction;

public class SPacketPlayInTransaction
extends VPacketPlayInTransaction {
    public void accept(PacketPlayInTransaction packetPlayInTransaction) {
        this.id = packetPlayInTransaction.d();
    }
}

