package felix.events.player;

import felix.events.Cancellable;
import felix.events.Event;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public class EventCollide extends Cancellable implements Event {

    private AxisAlignedBB axisalignedbb;
    private final Block block;
    private final Entity collidingEntity;
    private final int x;
    private final int y;
    private final int z;

    public EventCollide(Entity collidingEntity, int x, int y, int z, AxisAlignedBB axisalignedbb, Block block) {
        this.collidingEntity = collidingEntity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.axisalignedbb = axisalignedbb;
        this.block = block;
    }

    public AxisAlignedBB getBoundingBox() {
        return axisalignedbb;
    }

    public Entity getCollidingEntity() {
        return collidingEntity;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Block getBlock() {
        return block;
    }

    public void setBoundingBox(AxisAlignedBB object) {
        axisalignedbb = object;
    }

}