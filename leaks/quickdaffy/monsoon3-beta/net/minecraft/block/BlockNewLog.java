/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 */
package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockNewLog
extends BlockLog {
    public static final PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class, new Predicate<BlockPlanks.EnumType>(){

        public boolean apply(BlockPlanks.EnumType p_apply_1_) {
            return p_apply_1_.getMetadata() >= 4;
        }
    });

    public BlockNewLog() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.ACACIA).withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
    }

    @Override
    public MapColor getMapColor(IBlockState state) {
        BlockPlanks.EnumType blockplanks$enumtype = state.getValue(VARIANT);
        switch ((BlockLog.EnumAxis)state.getValue(LOG_AXIS)) {
            default: {
                switch (blockplanks$enumtype) {
                    default: {
                        return MapColor.stoneColor;
                    }
                    case DARK_OAK: 
                }
                return BlockPlanks.EnumType.DARK_OAK.func_181070_c();
            }
            case Y: 
        }
        return blockplanks$enumtype.func_181070_c();
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.ACACIA.getMetadata() - 4));
        list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, BlockPlanks.EnumType.byMetadata((meta & 3) + 4));
        switch (meta & 0xC) {
            case 0: {
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
                break;
            }
            case 4: {
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
                break;
            }
            case 8: {
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
                break;
            }
            default: {
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
            }
        }
        return iblockstate;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i |= state.getValue(VARIANT).getMetadata() - 4;
        switch ((BlockLog.EnumAxis)state.getValue(LOG_AXIS)) {
            case X: {
                i |= 4;
                break;
            }
            case Z: {
                i |= 8;
                break;
            }
            case NONE: {
                i |= 0xC;
            }
        }
        return i;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, VARIANT, LOG_AXIS);
    }

    @Override
    protected ItemStack createStackedBlock(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(VARIANT).getMetadata() - 4);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getMetadata() - 4;
    }
}

