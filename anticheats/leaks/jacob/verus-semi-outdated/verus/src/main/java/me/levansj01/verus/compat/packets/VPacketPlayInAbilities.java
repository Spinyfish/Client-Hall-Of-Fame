package me.levansj01.verus.compat.packets;

import lombok.Getter;
import me.levansj01.verus.compat.VPacket;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.api.wrapper.PlayerAbilities;

public abstract class VPacketPlayInAbilities extends VPacket {

    @Getter protected PlayerAbilities abilities;
    private static final int count = count();

    public void handle(PacketHandler paramPacketHandler) {
        paramPacketHandler.handle(this);
    }

    public int ordinal() {
        return count;
    }
}