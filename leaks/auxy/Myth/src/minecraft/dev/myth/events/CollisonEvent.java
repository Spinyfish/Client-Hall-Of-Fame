/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 11.09.22, 12:13
 */
package dev.myth.events;

import dev.codeman.eventbus.Event;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public class CollisonEvent extends Event {

    @Getter
    @Setter
    private AxisAlignedBB axisAlignedBB;

    @Getter private final Block block;
    @Getter private final Entity collidingEntity;

    @Getter private final int x, z;
    @Getter public int y;

    public CollisonEvent(Entity collidingEntity, int x, int y, int z, AxisAlignedBB axisAlignedBB, Block block) {
        this.collidingEntity = collidingEntity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.axisAlignedBB = axisAlignedBB;
        this.block = block;
    }

}
