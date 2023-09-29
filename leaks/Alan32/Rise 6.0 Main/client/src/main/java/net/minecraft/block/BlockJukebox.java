package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockJukebox extends BlockContainer {
    public static final PropertyBool HAS_RECORD = PropertyBool.create("has_record");

    protected BlockJukebox() {
        super(Material.wood, MapColor.dirtColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty(HAS_RECORD, Boolean.valueOf(false)));
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public boolean onBlockActivated(final World worldIn, final BlockPos pos, IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (state.getValue(HAS_RECORD).booleanValue()) {
            this.dropRecord(worldIn, pos, state);
            state = state.withProperty(HAS_RECORD, Boolean.valueOf(false));
            worldIn.setBlockState(pos, state, 2);
            return true;
        } else {
            return false;
        }
    }

    public void insertRecord(final World worldIn, final BlockPos pos, final IBlockState state, final ItemStack recordStack) {
        if (!worldIn.isRemote) {
            final TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof BlockJukebox.TileEntityJukebox) {
                ((BlockJukebox.TileEntityJukebox) tileentity).setRecord(new ItemStack(recordStack.getItem(), 1, recordStack.getMetadata()));
                worldIn.setBlockState(pos, state.withProperty(HAS_RECORD, Boolean.valueOf(true)), 2);
            }
        }
    }

    private void dropRecord(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (!worldIn.isRemote) {
            final TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof BlockJukebox.TileEntityJukebox) {
                final BlockJukebox.TileEntityJukebox blockjukebox$tileentityjukebox = (BlockJukebox.TileEntityJukebox) tileentity;
                final ItemStack itemstack = blockjukebox$tileentityjukebox.getRecord();

                if (itemstack != null) {
                    worldIn.playAuxSFX(1005, pos, 0);
                    worldIn.playRecord(pos, null);
                    blockjukebox$tileentityjukebox.setRecord(null);
                    final float f = 0.7F;
                    final double d0 = (double) (worldIn.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                    final double d1 = (double) (worldIn.rand.nextFloat() * f) + (double) (1.0F - f) * 0.2D + 0.6D;
                    final double d2 = (double) (worldIn.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                    final ItemStack itemstack1 = itemstack.copy();
                    final EntityItem entityitem = new EntityItem(worldIn, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, itemstack1);
                    entityitem.setDefaultPickupDelay();
                    worldIn.spawnEntityInWorld(entityitem);
                }
            }
        }
    }

    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        this.dropRecord(worldIn, pos, state);
        super.breakBlock(worldIn, pos, state);
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     *
     * @param chance  The chance that each Item is actually spawned (1.0 = always, 0.0 = never)
     * @param fortune The player's fortune level
     */
    public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state, final float chance, final int fortune) {
        if (!worldIn.isRemote) {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return new BlockJukebox.TileEntityJukebox();
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(final World worldIn, final BlockPos pos) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof BlockJukebox.TileEntityJukebox) {
            final ItemStack itemstack = ((BlockJukebox.TileEntityJukebox) tileentity).getRecord();

            if (itemstack != null) {
                return Item.getIdFromItem(itemstack.getItem()) + 1 - Item.getIdFromItem(Items.record_13);
            }
        }

        return 0;
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType() {
        return 3;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(HAS_RECORD, Boolean.valueOf(meta > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(HAS_RECORD).booleanValue() ? 1 : 0;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, HAS_RECORD);
    }

    public static class TileEntityJukebox extends TileEntity {
        private ItemStack record;

        public void readFromNBT(final NBTTagCompound compound) {
            super.readFromNBT(compound);

            if (compound.hasKey("RecordItem", 10)) {
                this.setRecord(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("RecordItem")));
            } else if (compound.getInteger("Record") > 0) {
                this.setRecord(new ItemStack(Item.getItemById(compound.getInteger("Record")), 1, 0));
            }
        }

        public void writeToNBT(final NBTTagCompound compound) {
            super.writeToNBT(compound);

            if (this.getRecord() != null) {
                compound.setTag("RecordItem", this.getRecord().writeToNBT(new NBTTagCompound()));
            }
        }

        public ItemStack getRecord() {
            return this.record;
        }

        public void setRecord(final ItemStack recordStack) {
            this.record = recordStack;
            this.markDirty();
        }
    }
}
