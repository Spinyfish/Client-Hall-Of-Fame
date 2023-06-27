package net.minecraft.block;

import net.minecraft.block.properties.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.block.state.*;

public class BlockRail extends BlockRailBase
{
    public static final PropertyEnum<EnumRailDirection> SHAPE;
    
    protected BlockRail() {
        super(false);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockRail.SHAPE, EnumRailDirection.NORTH_SOUTH));
    }
    
    @Override
    protected void onNeighborChangedInternal(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (neighborBlock.canProvidePower() && new Rail(worldIn, pos, state).countAdjacentRails() == 3) {
            this.func_176564_a(worldIn, pos, state, false);
        }
    }
    
    @Override
    public IProperty<EnumRailDirection> getShapeProperty() {
        return BlockRail.SHAPE;
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.byMetadata(meta));
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(BlockRail.SHAPE).getMetadata();
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { BlockRail.SHAPE });
    }
    
    static {
        SHAPE = PropertyEnum.create("shape", EnumRailDirection.class);
    }
}
