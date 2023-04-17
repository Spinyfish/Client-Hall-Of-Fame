package me.levansj01.verus.compat.v1_7_R4.packets;

import me.levansj01.verus.compat.packets.VPacketPlayOutAbilities;
import net.minecraft.server.v1_7_R4.PacketPlayOutAbilities;

public class SPacketPlayOutAbilities
extends VPacketPlayOutAbilities {
    public void accept(PacketPlayOutAbilities packetPlayOutAbilities) {
        this.isInvulnerable = packetPlayOutAbilities.c();
        this.isFlying = packetPlayOutAbilities.d();
        this.canFly = packetPlayOutAbilities.e();
        this.canInstantlyBuild = packetPlayOutAbilities.f();
        this.walkSpeed = packetPlayOutAbilities.g();
        this.flySpeed = packetPlayOutAbilities.h();
    }
}

