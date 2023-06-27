package net.minecraft.block;

import net.minecraft.block.material.*;
import net.minecraft.creativetab.*;
import net.minecraft.world.*;
import net.minecraft.block.state.*;
import net.minecraft.block.properties.*;
import java.util.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;

public abstract class BlockLog extends BlockRotatedPillar
{
    public static final PropertyEnum<EnumAxis> LOG_AXIS;
    
    public BlockLog() {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(2.0f);
        this.setStepSound(BlockLog.soundTypeWood);
    }
    
    @Override
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        final int i = 4;
        final int j = i + 1;
        if (worldIn.isAreaLoaded(pos.add(-j, -j, -j), pos.add(j, j, j))) {
            for (final BlockPos blockpos : BlockPos.getAllInBox(pos.add(-i, -i, -i), pos.add(i, i, i))) {
                final IBlockState iblockstate = worldIn.getBlockState(blockpos);
                if (iblockstate.getBlock().getMaterial() == Material.leaves && !iblockstate.getValue((IProperty<Boolean>)BlockLeaves.CHECK_DECAY)) {
                    worldIn.setBlockState(blockpos, iblockstate.withProperty((IProperty<Comparable>)BlockLeaves.CHECK_DECAY, true), 4);
                }
            }
        }
    }
    
    @Override
    public IBlockState onBlockPlaced(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(BlockLog.LOG_AXIS, EnumAxis.fromFacingAxis(facing.getAxis()));
    }
    
    static {
        LOG_AXIS = PropertyEnum.create("axis", EnumAxis.class);
    }
    
    public enum EnumAxis implements IStringSerializable
    {
        X("x"), 
        Y("y"), 
        Z("z"), 
        NONE("none");
        
        private final String name;
        
        private EnumAxis(final String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        public static EnumAxis fromFacingAxis(final EnumFacing.Axis axis) {
            switch (axis) {
                case X: {
                    return EnumAxis.X;
                }
                case Y: {
                    return EnumAxis.Y;
                }
                case Z: {
                    return EnumAxis.Z;
                }
                default: {
                    return EnumAxis.NONE;
                }
            }
        }
        
        @Override
        public String getName() {
            return this.name;
        }
    }
}
