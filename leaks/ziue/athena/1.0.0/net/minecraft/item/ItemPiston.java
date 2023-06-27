package net.minecraft.item;

import net.minecraft.block.*;

public class ItemPiston extends ItemBlock
{
    public ItemPiston(final Block block) {
        super(block);
    }
    
    @Override
    public int getMetadata(final int damage) {
        return 7;
    }
}
