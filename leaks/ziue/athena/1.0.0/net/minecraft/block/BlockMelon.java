package net.minecraft.block;

import net.minecraft.block.material.*;
import net.minecraft.creativetab.*;
import net.minecraft.block.state.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.init.*;

public class BlockMelon extends Block
{
    protected BlockMelon() {
        super(Material.gourd, MapColor.limeColor);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    
    @Override
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Items.melon;
    }
    
    @Override
    public int quantityDropped(final Random random) {
        return 3 + random.nextInt(5);
    }
    
    @Override
    public int quantityDroppedWithBonus(final int fortune, final Random random) {
        return Math.min(9, this.quantityDropped(random) + random.nextInt(1 + fortune));
    }
}
