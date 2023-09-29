package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.viamcp.utils.FixedSoundEngine;
import net.minecraft.world.World;

import java.util.List;

public class ItemBlock extends Item {
    protected final Block block;

    public ItemBlock(final Block block) {
        this.block = block;
    }

    /**
     * Sets the unlocalized name of this item to the string passed as the parameter, prefixed by "item."
     */
    public ItemBlock setUnlocalizedName(final String unlocalizedName) {
        super.setUnlocalizedName(unlocalizedName);
        return this;
    }

    /**
     * Called when a Block is right-clicked with this Item
     *
     * @param pos  The block being right-clicked
     * @param side The side being right-clicked
     */
    public boolean onItemUse(final ItemStack stack, final EntityPlayer playerIn, final World worldIn, BlockPos pos, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        return FixedSoundEngine.onItemUse(this, stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
        //        final IBlockState iblockstate = worldIn.getBlockState(pos);
//        final Block block = iblockstate.getBlock();
//
//        if (!block.isReplaceable(worldIn, pos)) {
//            pos = pos.offset(side);
//        }
//
//        if (stack.stackSize == 0) {
//            return false;
//        } else if (!playerIn.canPlayerEdit(pos, side, stack)) {
//            return false;
//        } else if (worldIn.canBlockBePlaced(this.block, pos, false, side, null, stack)) {
//            final int i = this.getMetadata(stack.getMetadata());
//            IBlockState iblockstate1 = this.block.onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, i, playerIn);
//
//            if (worldIn.setBlockState(pos, iblockstate1, 3)) {
//                iblockstate1 = worldIn.getBlockState(pos);
//
//                if (iblockstate1.getBlock() == this.block) {
//                    setTileEntityNBT(worldIn, playerIn, pos, stack);
//                    this.block.onBlockPlacedBy(worldIn, pos, iblockstate1, playerIn, stack);
//                }
//
//                worldIn.playSoundEffect((float) pos.getX() + 0.5F, (float) pos.getY() + 0.5F, (float) pos.getZ() + 0.5F, this.block.stepSound.getPlaceSound(), (this.block.stepSound.getVolume() + 1.0F) / 2.0F, this.block.stepSound.getFrequency() * 0.8F);
//                --stack.stackSize;
//            }
//
//            return true;
//        } else {
//            return false;
//        }
    }

    public static boolean setTileEntityNBT(final World worldIn, final EntityPlayer pos, final BlockPos stack, final ItemStack p_179224_3_) {
        final MinecraftServer minecraftserver = MinecraftServer.getServer();

        if (minecraftserver == null) {
            return false;
        } else {
            if (p_179224_3_.hasTagCompound() && p_179224_3_.getTagCompound().hasKey("BlockEntityTag", 10)) {
                final TileEntity tileentity = worldIn.getTileEntity(stack);

                if (tileentity != null) {
                    if (!worldIn.isRemote && tileentity.func_183000_F() && !minecraftserver.getConfigurationManager().canSendCommands(pos.getGameProfile())) {
                        return false;
                    }

                    final NBTTagCompound nbttagcompound = new NBTTagCompound();
                    final NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttagcompound.copy();
                    tileentity.writeToNBT(nbttagcompound);
                    final NBTTagCompound nbttagcompound2 = (NBTTagCompound) p_179224_3_.getTagCompound().getTag("BlockEntityTag");
                    nbttagcompound.merge(nbttagcompound2);
                    nbttagcompound.setInteger("x", stack.getX());
                    nbttagcompound.setInteger("y", stack.getY());
                    nbttagcompound.setInteger("z", stack.getZ());

                    if (!nbttagcompound.equals(nbttagcompound1)) {
                        tileentity.readFromNBT(nbttagcompound);
                        tileentity.markDirty();
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean canPlaceBlockOnSide(final World worldIn, BlockPos pos, EnumFacing side, final EntityPlayer player, final ItemStack stack) {
        final Block block = worldIn.getBlockState(pos).getBlock();

        if (block == Blocks.snow_layer) {
            side = EnumFacing.UP;
        } else if (!block.isReplaceable(worldIn, pos)) {
            pos = pos.offset(side);
        }

        return worldIn.canBlockBePlaced(this.block, pos, false, side, null, stack);
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(final ItemStack stack) {
        return this.block.getUnlocalizedName();
    }

    /**
     * Returns the unlocalized name of this item.
     */
    public String getUnlocalizedName() {
        return this.block.getUnlocalizedName();
    }

    /**
     * gets the CreativeTab this item is displayed on
     */
    public CreativeTabs getCreativeTab() {
        return this.block.getCreativeTabToDisplayOn();
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     *
     * @param subItems The List of sub-items. This is a List of ItemStacks.
     */
    public void getSubItems(final Item itemIn, final CreativeTabs tab, final List<ItemStack> subItems) {
        this.block.getSubBlocks(itemIn, tab, subItems);
    }

    public Block getBlock() {
        return this.block;
    }
}
