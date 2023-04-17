package me.levansj01.verus.compat.packets;

import lombok.Getter;
import me.levansj01.verus.compat.VPacket;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.api.wrapper.BlockPosition;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class VPacketPlayInBlockPlace extends VPacket {

    protected long timestamp;
    protected float blockZ;
    private static final int count = count();
    protected float blockX;
    protected int face;
    protected int hand = 0;
    protected float blockY;
    protected ItemStack itemStack;
    protected BlockPosition position;
    protected boolean empty;

    public boolean isUse() {
        return (this.position != null && this.position.getX() == -1 && (this.position.getY() == -1 || this.position.getY() == 255)
                && this.position.getZ() == -1 && this.blockX == 0.0F && this.blockY == 0.0F && this.blockZ == 0.0F && this.face == 255);
    }

    public void handle(PacketHandler paramPacketHandler) {
        paramPacketHandler.handle(this);
    }

    public boolean isItem() {
        return (this.itemStack != null && isUse());
    }

    public int ordinal() {
        return count;
    }
}