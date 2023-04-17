package me.levansj01.verus.util;

import me.levansj01.verus.compat.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.material.*;

public interface IBlockPosition {
    default boolean isWaterLogged(World world) {
        return NMSManager.getInstance().isWaterLogged(world, this);
    }

    default Material getType(Player player, World world) {
        return NMSManager.getInstance().getType(player, world, this);
    }

    int getZ();

    default boolean sameChunkXZ(IBlockPosition blockPosition) {
        return this.getX() >> 4 == blockPosition.getX() >> 4 && this.getZ() >> 4 == blockPosition.getZ() >> 4;
    }

    default boolean sameChunkY(IBlockPosition blockPosition) {
        return this.getY() >> 4 == blockPosition.getY() >> 4;
    }

    int getY();

    @Deprecated
    default Block getBlock(World world) {
        return world.getBlockAt(this.getX(), this.getY(), this.getZ());
    }

    int getX();

    default MaterialData getTypeAndData(Player player, World world) {
        return NMSManager.getInstance().getTypeAndData(player, world, this);
    }

    default float getFrictionFactor(Player player, World world) {
        return NMSManager.getInstance().getFrictionFactor(player, world, this);
    }
}
