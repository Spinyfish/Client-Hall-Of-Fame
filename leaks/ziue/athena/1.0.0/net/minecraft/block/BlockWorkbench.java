package net.minecraft.block;

import net.minecraft.block.material.*;
import net.minecraft.creativetab.*;
import net.minecraft.block.state.*;
import net.minecraft.world.*;
import net.minecraft.stats.*;
import net.minecraft.init.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;

public class BlockWorkbench extends Block
{
    protected BlockWorkbench() {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }
    
    @Override
    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        playerIn.displayGui(new InterfaceCraftingTable(worldIn, pos));
        playerIn.triggerAchievement(StatList.field_181742_Z);
        return true;
    }
    
    public static class InterfaceCraftingTable implements IInteractionObject
    {
        private final World world;
        private final BlockPos position;
        
        public InterfaceCraftingTable(final World worldIn, final BlockPos pos) {
            this.world = worldIn;
            this.position = pos;
        }
        
        @Override
        public String getName() {
            return null;
        }
        
        @Override
        public boolean hasCustomName() {
            return false;
        }
        
        @Override
        public IChatComponent getDisplayName() {
            return new ChatComponentTranslation(Blocks.crafting_table.getUnlocalizedName() + ".name", new Object[0]);
        }
        
        @Override
        public Container createContainer(final InventoryPlayer playerInventory, final EntityPlayer playerIn) {
            return new ContainerWorkbench(playerInventory, this.world, this.position);
        }
        
        @Override
        public String getGuiID() {
            return "minecraft:crafting_table";
        }
    }
}
