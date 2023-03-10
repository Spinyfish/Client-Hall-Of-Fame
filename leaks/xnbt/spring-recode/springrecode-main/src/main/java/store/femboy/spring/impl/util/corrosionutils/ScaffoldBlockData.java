package store.femboy.spring.impl.util.corrosionutils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;


public class ScaffoldBlockData {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private final Block block;
    private final BlockPos blockPos;
    private final EnumFacing direction;

    public ScaffoldBlockData(Block block, BlockPos blockPos, EnumFacing direction) {
        this.block = block;
        this.blockPos = blockPos;
        this.direction = direction;
    }

    public static ScaffoldBlockData getBlockInfo(BlockPos blockPos) {
        blockPos = blockPos.down();
        ScaffoldBlockData blockInfo = null;

        for (EnumFacing facing : EnumFacing.values()) {
            Block block = mc.theWorld.getBlockState(blockPos.offset(facing)).getBlock();

            if (ScaffoldUtil.isBlockValid(block)) {
                blockInfo = new ScaffoldBlockData(block, blockPos.offset(facing), facing.getOpposite());
            }
        }

        return blockInfo;
    }

    public Block getBlock() {
        return this.block;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public EnumFacing getDirection() {
        return this.direction;
    }
}
