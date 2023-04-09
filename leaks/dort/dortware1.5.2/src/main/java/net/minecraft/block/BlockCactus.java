package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.Random;

public class BlockCactus extends Block {
    public static final PropertyInteger AGE_PROP = PropertyInteger.create("age", 0, 15);
    // private static final String __OBFID = "CL_00000210";

    protected BlockCactus() {
        super(Material.cactus);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE_PROP, 0));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        BlockPos var5 = pos.offsetUp();

        if (worldIn.isAirBlock(var5)) {
            int var6;

            for (var6 = 1; worldIn.getBlockState(pos.offsetDown(var6)).getBlock() == this; ++var6) {
                ;
            }

            if (var6 < 3) {
                int var7 = (Integer) state.getValue(AGE_PROP);

                if (var7 == 15) {
                    worldIn.setBlockState(var5, this.getDefaultState());
                    IBlockState var8 = state.withProperty(AGE_PROP, 0);
                    worldIn.setBlockState(pos, var8, 4);
                    this.onNeighborBlockChange(worldIn, var5, var8, this);
                } else {
                    worldIn.setBlockState(pos, state.withProperty(AGE_PROP, var7 + 1), 4);
                }
            }
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        float var4 = 0.0625F;
        return new AxisAlignedBB((float) pos.getX() + var4, pos.getY(), (float) pos.getZ() + var4, (float) (pos.getX() + 1) - var4, (float) (pos.getY() + 1) - var4, (float) (pos.getZ() + 1) - var4);
    }

    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
        float var3 = 0.0625F;
        return new AxisAlignedBB((float) pos.getX() + var3, pos.getY(), (float) pos.getZ() + var3, (float) (pos.getX() + 1) - var3, pos.getY() + 1, (float) (pos.getZ() + 1) - var3);
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos);
    }

    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!this.canBlockStay(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    public boolean canBlockStay(World worldIn, BlockPos p_176586_2_) {
        for (Object o : EnumFacing.Plane.HORIZONTAL) {
            EnumFacing var4 = (EnumFacing) o;

            if (worldIn.getBlockState(p_176586_2_.offset(var4)).getBlock().getMaterial().isSolid()) {
                return false;
            }
        }

        Block var5 = worldIn.getBlockState(p_176586_2_.offsetDown()).getBlock();
        return var5 == Blocks.cactus || var5 == Blocks.sand;
    }

    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        entityIn.attackEntityFrom(DamageSource.cactus, 1.0F);
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AGE_PROP, meta);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return (Integer) state.getValue(AGE_PROP);
    }

    protected BlockState createBlockState() {
        return new BlockState(this, AGE_PROP);
    }
}
