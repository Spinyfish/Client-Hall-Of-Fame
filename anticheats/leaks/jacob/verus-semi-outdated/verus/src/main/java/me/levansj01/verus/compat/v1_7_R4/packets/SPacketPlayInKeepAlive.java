package me.levansj01.verus.compat.v1_7_R4.packets;

import java.math.BigInteger;
import me.levansj01.verus.compat.packets.VPacketPlayInKeepAlive;
import net.minecraft.server.v1_7_R4.PacketPlayInKeepAlive;

public class SPacketPlayInKeepAlive
extends VPacketPlayInKeepAlive {
    public void accept(PacketPlayInKeepAlive packetPlayInKeepAlive) {
        this.id = BigInteger.valueOf(packetPlayInKeepAlive.c());
    }
}

