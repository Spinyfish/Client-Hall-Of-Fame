package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import net.minecraft.server.v1_8_R3.*;

public class SPacketPlayInSteerVehicle extends VPacketPlayInSteerVehicle
{
    public void accept(final PacketPlayInSteerVehicle packetPlayInSteerVehicle) {
        this.forward = packetPlayInSteerVehicle.a();
        this.strafe = packetPlayInSteerVehicle.b();
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayInSteerVehicle)o);
    }
}
