package me.levansj01.verus.compat.v1_7_R4.packets;

import me.levansj01.verus.compat.packets.VPacketPlayInWindowClick;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.PacketPlayInWindowClick;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;

public class SPacketPlayInWindowClick
extends VPacketPlayInWindowClick {
    public void accept(PacketPlayInWindowClick packetPlayInWindowClick) {
        this.slot = packetPlayInWindowClick.d();
        this.window = packetPlayInWindowClick.c();
        this.button = packetPlayInWindowClick.e();
        this.mode = packetPlayInWindowClick.h();
        this.itemStack = CraftItemStack.asBukkitCopy((ItemStack)packetPlayInWindowClick.g());
    }

    @Override
    public boolean isChest() {
        return this.window != 0;
    }

    @Override
    public boolean isItem() {
        return this.itemStack != null;
    }

    @Override
    public boolean isShiftClick() {
        return this.isChest() && this.mode == 1 && this.button < 2;
    }
}

