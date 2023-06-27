package net.minecraft.block;

import java.util.*;
import net.minecraft.block.material.*;
import net.minecraft.creativetab.*;
import net.minecraft.world.*;
import net.minecraft.block.state.*;
import net.minecraft.tileentity.*;
import net.minecraft.entity.player.*;
import net.minecraft.stats.*;
import net.minecraft.util.*;
import com.google.common.collect.*;

public class BlockNote extends BlockContainer
{
    private static final List<String> INSTRUMENTS;
    
    public BlockNote() {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }
    
    @Override
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        final boolean flag = worldIn.isBlockPowered(pos);
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityNote) {
            final TileEntityNote tileentitynote = (TileEntityNote)tileentity;
            if (tileentitynote.previousRedstoneState != flag) {
                if (flag) {
                    tileentitynote.triggerNote(worldIn, pos);
                }
                tileentitynote.previousRedstoneState = flag;
            }
        }
    }
    
    @Override
    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityNote) {
            final TileEntityNote tileentitynote = (TileEntityNote)tileentity;
            tileentitynote.changePitch();
            tileentitynote.triggerNote(worldIn, pos);
            playerIn.triggerAchievement(StatList.field_181735_S);
        }
        return true;
    }
    
    @Override
    public void onBlockClicked(final World worldIn, final BlockPos pos, final EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            final TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityNote) {
                ((TileEntityNote)tileentity).triggerNote(worldIn, pos);
                playerIn.triggerAchievement(StatList.field_181734_R);
            }
        }
    }
    
    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return new TileEntityNote();
    }
    
    private String getInstrument(int id) {
        if (id < 0 || id >= BlockNote.INSTRUMENTS.size()) {
            id = 0;
        }
        return BlockNote.INSTRUMENTS.get(id);
    }
    
    @Override
    public boolean onBlockEventReceived(final World worldIn, final BlockPos pos, final IBlockState state, final int eventID, final int eventParam) {
        final float f = (float)Math.pow(2.0, (eventParam - 12) / 12.0);
        worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, "note." + this.getInstrument(eventID), 3.0f, f);
        worldIn.spawnParticle(EnumParticleTypes.NOTE, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, eventParam / 24.0, 0.0, 0.0, new int[0]);
        return true;
    }
    
    @Override
    public int getRenderType() {
        return 3;
    }
    
    static {
        INSTRUMENTS = Lists.newArrayList((Object[])new String[] { "harp", "bd", "snare", "hat", "bassattack" });
    }
}
