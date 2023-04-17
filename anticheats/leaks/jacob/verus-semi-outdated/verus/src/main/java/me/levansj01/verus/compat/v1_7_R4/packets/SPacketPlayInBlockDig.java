package me.levansj01.verus.compat.v1_7_R4.packets;

import me.levansj01.verus.compat.api.wrapper.BlockPosition;
import me.levansj01.verus.compat.api.wrapper.Direction;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockDig;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockDig;

public class SPacketPlayInBlockDig
extends VPacketPlayInBlockDig {
    public void accept(PacketPlayInBlockDig packetPlayInBlockDig) {
        this.blockPosition = new BlockPosition(packetPlayInBlockDig.c(), packetPlayInBlockDig.d(), packetPlayInBlockDig.e());
        this.face = Direction.values()[packetPlayInBlockDig.f() % Direction.values().length];
        this.type = VPacketPlayInBlockDig.PlayerDigType.values()[packetPlayInBlockDig.g()];
    }
}

