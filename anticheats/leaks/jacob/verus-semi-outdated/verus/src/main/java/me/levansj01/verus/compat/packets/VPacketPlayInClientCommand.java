package me.levansj01.verus.compat.packets;

import lombok.Getter;
import me.levansj01.verus.compat.VPacket;
import me.levansj01.verus.compat.api.PacketHandler;

public abstract class VPacketPlayInClientCommand extends VPacket {

    private static final int count = count();
    @Getter protected ClientCommand command;

    public int ordinal() {
        return count;
    }

    public void handle(PacketHandler var1) {
        var1.handle(this);
    }

    public enum ClientCommand {
        REQUEST_STATS,
        PERFORM_RESPAWN,
        OPEN_INVENTORY_ACHIEVEMENT;
    }
}