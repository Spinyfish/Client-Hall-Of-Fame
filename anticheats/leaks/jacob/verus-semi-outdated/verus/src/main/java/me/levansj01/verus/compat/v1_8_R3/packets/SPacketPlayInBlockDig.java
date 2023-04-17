package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.compat.api.wrapper.*;

public class SPacketPlayInBlockDig extends VPacketPlayInBlockDig
{
    public void accept(final PacketPlayInBlockDig packetPlayInBlockDig) {
        this.blockPosition = new BlockPosition(packetPlayInBlockDig.a().getX(), packetPlayInBlockDig.a().getY(), packetPlayInBlockDig.a().getZ());
        this.face = Direction.values()[packetPlayInBlockDig.b().ordinal()];
        this.type = PlayerDigType.values()[packetPlayInBlockDig.c().ordinal()];
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayInBlockDig)o);
    }
}
