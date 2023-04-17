package me.levansj01.verus.compat.v1_7_R4.packets;

import me.levansj01.verus.compat.packets.VPacketPlayInSteerVehicle;
import net.minecraft.server.v1_7_R4.PacketPlayInSteerVehicle;

public class SPacketPlayInSteerVehicle
extends VPacketPlayInSteerVehicle {
    public void accept(PacketPlayInSteerVehicle packetPlayInSteerVehicle) {
        this.forward = packetPlayInSteerVehicle.c();
        this.strafe = packetPlayInSteerVehicle.d();
    }
}

