package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockDirectional {
   public static final PropertyBool OPEN = PropertyBool.create("open");
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   public static final PropertyBool IN_WALL = PropertyBool.create("in_wall");

   public BlockFenceGate(BlockPlanks.EnumType p_i46394_1_) {
      super(Material.wood, p_i46394_1_.func_181070_c());
      this.setDefaultState(
         this.blockState.getBaseState().withProperty(OPEN, Boolean.FALSE).withProperty(POWERED, Boolean.FALSE).withProperty(IN_WALL, Boolean.FALSE)
      );
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      EnumFacing.Axis enumfacing$axis = state.getValue(FACING).getAxis();
      if (enumfacing$axis == EnumFacing.Axis.Z
            && (
               worldIn.getBlockState(pos.west()).getBlock() == Blocks.cobblestone_wall
                  || worldIn.getBlockState(pos.east()).getBlock() == Blocks.cobblestone_wall
            )
         || enumfacing$axis == EnumFacing.Axis.X
            && (
               worldIn.getBlockState(pos.north()).getBlock() == Blocks.cobblestone_wall
                  || worldIn.getBlockState(pos.south()).getBlock() == Blocks.cobblestone_wall
            )) {
         state = state.withProperty(IN_WALL, Boolean.TRUE);
      }

      return state;
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).getBlock().getMaterial().isSolid() && super.canPlaceBlockAt(worldIn, pos);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      if (state.getValue(OPEN)) {
         return null;
      } else {
         EnumFacing.Axis enumfacing$axis = state.getValue(FACING).getAxis();
         return enumfacing$axis == EnumFacing.Axis.Z
            ? new AxisAlignedBB(
               (double)pos.getX(),
               (double)pos.getY(),
               (double)((float)pos.getZ() + 0.375F),
               (double)(pos.getX() + 1),
               (double)((float)pos.getY() + 1.5F),
               (double)((float)pos.getZ() + 0.625F)
            )
            : new AxisAlignedBB(
               (double)((float)pos.getX() + 0.375F),
               (double)pos.getY(),
               (double)pos.getZ(),
               (double)((float)pos.getX() + 0.625F),
               (double)((float)pos.getY() + 1.5F),
               (double)(pos.getZ() + 1)
            );
      }
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      EnumFacing.Axis enumfacing$axis = worldIn.getBlockState(pos).getValue(FACING).getAxis();
      if (enumfacing$axis == EnumFacing.Axis.Z) {
         this.setBlockBounds(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
      } else {
         this.setBlockBounds(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
      }
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return !worldIn.getBlockState(pos).getValue(OPEN);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState()
         .withProperty(FACING, placer.getHorizontalFacing())
         .withProperty(OPEN, Boolean.FALSE)
         .withProperty(POWERED, Boolean.FALSE)
         .withProperty(IN_WALL, Boolean.FALSE);
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (state.getValue(OPEN)) {
         state = state.withProperty(OPEN, Boolean.FALSE);
         worldIn.setBlockState(pos, state, 2);
      } else {
         EnumFacing enumfacing = EnumFacing.fromAngle((double)playerIn.rotationYaw);
         if (state.getValue(FACING) == enumfacing.getOpposite()) {
            state = state.withProperty(FACING, enumfacing);
         }

         state = state.withProperty(OPEN, Boolean.TRUE);
         worldIn.setBlockState(pos, state, 2);
      }

      worldIn.playAuxSFXAtEntity(playerIn, state.getValue(OPEN) ? 1003 : 1006, pos, 0);
      return true;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote) {
         boolean flag = worldIn.isBlockPowered(pos);
         if (flag || neighborBlock.canProvidePower()) {
            if (flag && !state.getValue(OPEN) && !state.getValue(POWERED)) {
               worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.TRUE).withProperty(POWERED, Boolean.TRUE), 2);
               worldIn.playAuxSFXAtEntity(null, 1003, pos, 0);
            } else if (!flag && state.getValue(OPEN) && state.getValue(POWERED)) {
               worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.FALSE).withProperty(POWERED, Boolean.FALSE), 2);
               worldIn.playAuxSFXAtEntity(null, 1006, pos, 0);
            } else if (flag != state.getValue(POWERED)) {
               worldIn.setBlockState(pos, state.withProperty(POWERED, flag), 2);
            }
         }
      }
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return true;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(FACING, EnumFacing.getHorizontal(meta))
         .withProperty(OPEN, (meta & 4) != 0)
         .withProperty(POWERED, (meta & 8) != 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i |= state.getValue(FACING).getHorizontalIndex();
      if (state.getValue(POWERED)) {
         i |= 8;
      }

      if (state.getValue(OPEN)) {
         i |= 4;
      }

      return i;
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, FACING, OPEN, POWERED, IN_WALL);
   }
}
