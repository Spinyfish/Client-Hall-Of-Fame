package me.levansj01.verus.compat.packets;

import lombok.Getter;
import me.levansj01.verus.compat.VPacket;
import me.levansj01.verus.compat.api.PacketHandler;

public abstract class VPacketPlayInArmAnimation extends VPacket {

    private static final int count = count();
    @Getter protected int hand = 0;

    public void handle(PacketHandler paramPacketHandler) {
        paramPacketHandler.handle(this);
    }

    public int ordinal() {
        return count;
    }
}
