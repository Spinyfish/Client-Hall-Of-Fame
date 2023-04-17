package me.levansj01.verus.compat.v1_7_R4.packets;

import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;

public class SPacketPlayInFlying
extends VPacketPlayInFlying {
    public void accept(PacketPlayInFlying packetPlayInFlying) {
        this.x = packetPlayInFlying.c();
        this.y = packetPlayInFlying.d();
        this.z = packetPlayInFlying.e();
        this.yaw = packetPlayInFlying.g();
        this.pitch = packetPlayInFlying.h();
        this.ground = packetPlayInFlying.i();
        this.pos = packetPlayInFlying.j();
        this.look = packetPlayInFlying.k();
    }
}

