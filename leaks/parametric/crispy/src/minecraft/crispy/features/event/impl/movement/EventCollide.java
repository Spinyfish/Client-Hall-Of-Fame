package crispy.features.event.impl.movement;

import crispy.features.event.Event;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

@Getter
public class EventCollide extends Event<EventCollide> {
    private final Entity entity;
    private final double posX;
    private final double posY;
    private final double posZ;
    private AxisAlignedBB boundingBox;
    private final Block block;

    public EventCollide(Entity entity, double posX, double posY, double posZ, AxisAlignedBB boundingBox, Block block) {
        this.entity = entity;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.boundingBox = boundingBox;
        this.block = block;
    }

    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }

    public Entity getEntity() {
        return entity;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public Block getBlock() {
        return block;
    }
}
