package ooo.cpacket.ruby.api.event.events.blocks;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import ooo.cpacket.ruby.api.event.events.AbstractSkippableEvent;

public class EventAABB extends AbstractSkippableEvent {

	private AxisAlignedBB boundingBox;
	private Block block;
	private BlockPos pos;

	public EventAABB(Block block, BlockPos pos, AxisAlignedBB aabb) {
		this.boundingBox = aabb;
		this.pos = pos;
		this.block = block;
	}

	public AxisAlignedBB getBoundingBox() {
		return boundingBox;
	}

	public BlockPos getPos() {
		return pos;
	}

	public Block getBlock() {
		return block;
	}

	public void setBoundingBox(AxisAlignedBB boundingBox) {
		this.boundingBox = boundingBox;
	}

	public void setPos(BlockPos pos) {
		this.pos = pos;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

}
