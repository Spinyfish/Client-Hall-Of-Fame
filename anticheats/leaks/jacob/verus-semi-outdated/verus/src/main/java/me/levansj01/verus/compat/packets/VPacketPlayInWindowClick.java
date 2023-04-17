package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import org.bukkit.inventory.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayInWindowClick extends VPacket
{
    protected Integer window;
    private static final int count;
    protected ItemStack itemStack;
    protected Integer mode;
    protected Integer slot;
    protected Integer button;
    
    public Integer getButton() {
        return this.button;
    }
    
    public Integer getMode() {
        return this.mode;
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayInWindowClick.count;
    }
    
    public abstract boolean isItem();
    
    public Integer getSlot() {
        return this.slot;
    }
    
    public Integer getWindow() {
        return this.window;
    }
    
    public abstract boolean isShiftClick();
    
    public abstract boolean isChest();
    
    public ItemStack getItemStack() {
        return this.itemStack;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    static {
        count = VPacket.count();
    }
}
