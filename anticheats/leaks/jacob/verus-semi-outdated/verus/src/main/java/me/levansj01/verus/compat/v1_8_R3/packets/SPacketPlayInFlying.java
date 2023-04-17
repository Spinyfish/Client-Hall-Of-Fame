package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import net.minecraft.server.v1_8_R3.*;

public class SPacketPlayInFlying extends VPacketPlayInFlying
{
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayInFlying)o);
    }
    
    public void accept(final PacketPlayInFlying packetPlayInFlying) {
        this.x = packetPlayInFlying.a();
        this.y = packetPlayInFlying.b();
        this.z = packetPlayInFlying.c();
        this.yaw = packetPlayInFlying.d();
        this.pitch = packetPlayInFlying.e();
        this.ground = packetPlayInFlying.f();
        this.pos = packetPlayInFlying.g();
        this.look = packetPlayInFlying.h();
    }
}
