package me.levansj01.verus.compat.v1_7_R4.packets;

import me.levansj01.verus.compat.api.wrapper.BlockPosition;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockPlace;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockPlace;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;

public class SPacketPlayInBlockPlace
extends VPacketPlayInBlockPlace {
    public void accept(PacketPlayInBlockPlace packetPlayInBlockPlace) {
        this.position = new BlockPosition(packetPlayInBlockPlace.c(), packetPlayInBlockPlace.d(), packetPlayInBlockPlace.e());
        this.face = packetPlayInBlockPlace.getFace();
        this.itemStack = CraftItemStack.asBukkitCopy((ItemStack)packetPlayInBlockPlace.getItemStack());
        this.blockX = packetPlayInBlockPlace.h();
        this.blockY = packetPlayInBlockPlace.i();
        this.blockZ = packetPlayInBlockPlace.j();
        this.timestamp = packetPlayInBlockPlace.timestamp;
    }
}

