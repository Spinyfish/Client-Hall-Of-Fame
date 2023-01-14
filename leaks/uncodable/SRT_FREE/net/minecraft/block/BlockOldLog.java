package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockOldLog extends BlockLog {
   public static final PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.create(
      "variant", BlockPlanks.EnumType.class, p_apply_1_ -> p_apply_1_.getMetadata() < 4
   );

   public BlockOldLog() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.OAK).withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      BlockPlanks.EnumType blockplanks$enumtype = state.getValue(VARIANT);
      switch((BlockLog.EnumAxis)state.getValue(LOG_AXIS)) {
         case X:
         case Z:
         case NONE:
         default:
            switch(blockplanks$enumtype) {
               case OAK:
               case JUNGLE:
               default:
                  return BlockPlanks.EnumType.SPRUCE.func_181070_c();
               case SPRUCE:
                  return BlockPlanks.EnumType.DARK_OAK.func_181070_c();
               case BIRCH:
                  return MapColor.quartzColor;
            }
         case Y:
            return blockplanks$enumtype.func_181070_c();
      }
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.OAK.getMetadata()));
      list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()));
      list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.BIRCH.getMetadata()));
      list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.JUNGLE.getMetadata()));
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, BlockPlanks.EnumType.byMetadata((meta & 3) % 4));
      switch(meta & 12) {
         case 0:
            iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
            break;
         case 4:
            iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
            break;
         case 8:
            iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
            break;
         default:
            iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
      }

      return iblockstate;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i |= state.getValue(VARIANT).getMetadata();
      switch((BlockLog.EnumAxis)state.getValue(LOG_AXIS)) {
         case X:
            i |= 4;
            break;
         case Z:
            i |= 8;
            break;
         case NONE:
            i |= 12;
      }

      return i;
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, VARIANT, LOG_AXIS);
   }

   @Override
   protected ItemStack createStackedBlock(IBlockState state) {
      return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(VARIANT).getMetadata());
   }

   @Override
   public int damageDropped(IBlockState state) {
      return state.getValue(VARIANT).getMetadata();
   }
}
