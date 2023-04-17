package me.levansj01.verus.compat.v1_7_R4.packets;

import me.levansj01.verus.compat.api.wrapper.PlayerAbilities;
import me.levansj01.verus.compat.packets.VPacketPlayInAbilities;
import net.minecraft.server.v1_7_R4.PacketPlayInAbilities;

public class SPacketPlayInAbilities
extends VPacketPlayInAbilities {
    public void accept(PacketPlayInAbilities packetPlayInAbilities) {
        this.abilities = new PlayerAbilities(packetPlayInAbilities.c(), packetPlayInAbilities.isFlying(), packetPlayInAbilities.e(), packetPlayInAbilities.f(), packetPlayInAbilities.g(), packetPlayInAbilities.h());
    }
}

