package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public abstract class BlockLog extends BlockRotatedPillar {
    public static final PropertyEnum AXIS_PROP = PropertyEnum.create("axis", BlockLog.EnumAxis.class);
    // private static final String __OBFID = "CL_00000266";

    public BlockLog() {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(2.0F);
        this.setStepSound(soundTypeWood);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        byte var4 = 4;
        int var5 = var4 + 1;

        if (worldIn.isAreaLoaded(pos.add(-var5, -var5, -var5), pos.add(var5, var5, var5))) {

            for (Object o : BlockPos.getAllInBox(pos.add(-var4, -var4, -var4), pos.add(var4, var4, var4))) {
                BlockPos var7 = (BlockPos) o;
                IBlockState var8 = worldIn.getBlockState(var7);

                if (var8.getBlock().getMaterial() == Material.leaves && !(Boolean) var8.getValue(BlockLeaves.field_176236_b)) {
                    worldIn.setBlockState(var7, var8.withProperty(BlockLeaves.field_176236_b, true), 4);
                }
            }
        }
    }

    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(AXIS_PROP, BlockLog.EnumAxis.func_176870_a(facing.getAxis()));
    }

    public enum EnumAxis implements IStringSerializable {
        X("x"),
        Y("y"),
        Z("z"),
        NONE("none");
        private final String field_176874_e;

        // private static final String __OBFID = "CL_00002100";

        EnumAxis(String p_i45708_3_) {
            this.field_176874_e = p_i45708_3_;
        }

        public String toString() {
            return this.field_176874_e;
        }

        public static BlockLog.EnumAxis func_176870_a(EnumFacing.Axis p_176870_0_) {
            switch (BlockLog.SwitchAxis.field_180167_a[p_176870_0_.ordinal()]) {
                case 1:
                    return X;

                case 2:
                    return Y;

                case 3:
                    return Z;

                default:
                    return NONE;
            }
        }

        public String getName() {
            return this.field_176874_e;
        }
    }

    static final class SwitchAxis {
        static final int[] field_180167_a = new int[EnumFacing.Axis.values().length];
        // private static final String __OBFID = "CL_00002101";

        static {
            try {
                field_180167_a[EnumFacing.Axis.X.ordinal()] = 1;
            } catch (NoSuchFieldError var3) {
                ;
            }

            try {
                field_180167_a[EnumFacing.Axis.Y.ordinal()] = 2;
            } catch (NoSuchFieldError var2) {
                ;
            }

            try {
                field_180167_a[EnumFacing.Axis.Z.ordinal()] = 3;
            } catch (NoSuchFieldError var1) {
                ;
            }
        }
    }
}
