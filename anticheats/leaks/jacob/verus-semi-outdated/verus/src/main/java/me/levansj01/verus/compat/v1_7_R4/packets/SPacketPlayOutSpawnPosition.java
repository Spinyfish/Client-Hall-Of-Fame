package me.levansj01.verus.compat.v1_7_R4.packets;

import me.levansj01.verus.compat.api.wrapper.BlockPosition;
import me.levansj01.verus.compat.packets.VPacketPlayOutSpawnPosition;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnPosition;

public class SPacketPlayOutSpawnPosition
extends VPacketPlayOutSpawnPosition {
    public void accept(PacketPlayOutSpawnPosition packetPlayOutSpawnPosition) {
        this.position = new BlockPosition(packetPlayOutSpawnPosition.x, packetPlayOutSpawnPosition.y, packetPlayOutSpawnPosition.z);
    }
}

