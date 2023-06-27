package net.minecraft.block;

import net.minecraft.block.material.*;
import net.minecraft.creativetab.*;
import java.util.*;

public class BlockPackedIce extends Block
{
    public BlockPackedIce() {
        super(Material.packedIce);
        this.slipperiness = 0.98f;
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    
    @Override
    public int quantityDropped(final Random random) {
        return 0;
    }
}
