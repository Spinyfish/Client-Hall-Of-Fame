package net.minecraft.block;

import net.minecraft.creativetab.*;
import java.util.*;
import net.minecraft.util.*;
import net.minecraft.block.state.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.block.material.*;

public class BlockGlowstone extends Block
{
    public BlockGlowstone(final Material materialIn) {
        super(materialIn);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    
    @Override
    public int quantityDroppedWithBonus(final int fortune, final Random random) {
        return MathHelper.clamp_int(this.quantityDropped(random) + random.nextInt(fortune + 1), 1, 4);
    }
    
    @Override
    public int quantityDropped(final Random random) {
        return 2 + random.nextInt(3);
    }
    
    @Override
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Items.glowstone_dust;
    }
    
    @Override
    public MapColor getMapColor(final IBlockState state) {
        return MapColor.sandColor;
    }
}
