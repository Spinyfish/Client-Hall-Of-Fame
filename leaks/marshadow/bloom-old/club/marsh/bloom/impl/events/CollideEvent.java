package club.marsh.bloom.impl.events;

import club.marsh.bloom.impl.events.Event;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class CollideEvent extends Event {

	public AxisAlignedBB getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(AxisAlignedBB boundingBox) {
		this.boundingBox = boundingBox;
	}

	public Entity getEntity() {
		return entity;
	}

	public BlockPos getPos() {
		return pos;
	}


	public Block getBlock() {
		return block;
	}

	private final Entity entity;
    private final BlockPos pos;
    private AxisAlignedBB boundingBox;
    private final Block block;

    public CollideEvent(Entity entity, BlockPos pos, AxisAlignedBB boundingBox, Block block) {
        this.entity = entity;
        this.pos = pos;
        this.boundingBox = boundingBox;
        this.block = block;
    }

}
