package net.minecraft.block;

import com.google.common.collect.Lists;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.List;

public class BlockNote extends BlockContainer {
    private static final List<String> INSTRUMENTS = Lists.newArrayList("harp", "bd", "snare", "hat", "bassattack");

    public BlockNote() {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        final boolean flag = worldIn.isBlockPowered(pos);
        final TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityNote) {
            final TileEntityNote tileentitynote = (TileEntityNote) tileentity;

            if (tileentitynote.previousRedstoneState != flag) {
                if (flag) {
                    tileentitynote.triggerNote(worldIn, pos);
                }

                tileentitynote.previousRedstoneState = flag;
            }
        }
    }

    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else {
            final TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityNote) {
                final TileEntityNote tileentitynote = (TileEntityNote) tileentity;
                tileentitynote.changePitch();
                tileentitynote.triggerNote(worldIn, pos);
                playerIn.triggerAchievement(StatList.field_181735_S);
            }

            return true;
        }
    }

    public void onBlockClicked(final World worldIn, final BlockPos pos, final EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            final TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityNote) {
                ((TileEntityNote) tileentity).triggerNote(worldIn, pos);
                playerIn.triggerAchievement(StatList.field_181734_R);
            }
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return new TileEntityNote();
    }

    private String getInstrument(int id) {
        if (id < 0 || id >= INSTRUMENTS.size()) {
            id = 0;
        }

        return INSTRUMENTS.get(id);
    }

    /**
     * Called on both Client and Server when World#addBlockEvent is called
     */
    public boolean onBlockEventReceived(final World worldIn, final BlockPos pos, final IBlockState state, final int eventID, final int eventParam) {
        final float f = (float) Math.pow(2.0D, (double) (eventParam - 12) / 12.0D);
        worldIn.playSoundEffect((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, "note." + this.getInstrument(eventID), 3.0F, f);
        worldIn.spawnParticle(EnumParticleTypes.NOTE, (double) pos.getX() + 0.5D, (double) pos.getY() + 1.2D, (double) pos.getZ() + 0.5D, (double) eventParam / 24.0D, 0.0D, 0.0D);
        return true;
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType() {
        return 3;
    }
}
