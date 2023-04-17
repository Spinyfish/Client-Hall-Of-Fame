package me.levansj01.verus.compat.packets;

import lombok.Getter;
import me.levansj01.verus.compat.VPacket;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.api.wrapper.BlockPosition;
import me.levansj01.verus.compat.api.wrapper.Direction;

public abstract class VPacketPlayInBlockDig extends VPacket {

    @Getter protected BlockPosition blockPosition;
    private static final int count = count();
    @Getter protected PlayerDigType type;
    @Getter protected Direction face;

    public void handle(PacketHandler paramPacketHandler) {
        paramPacketHandler.handle(this);
    }

    public int ordinal() {
        return count;
    }

    public enum PlayerDigType {
        START_DESTROY_BLOCK, DROP_ITEM, SWAP_HELD_ITEMS, STOP_DESTROY_BLOCK, RELEASE_USE_ITEM, ABORT_DESTROY_BLOCK, DROP_ALL_ITEMS;
    }
}
