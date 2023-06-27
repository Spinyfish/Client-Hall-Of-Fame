package net.minecraft.item;

import net.minecraft.block.*;
import com.google.common.base.*;

public class ItemMultiTexture extends ItemBlock
{
    protected final Block theBlock;
    protected final Function<ItemStack, String> nameFunction;
    
    public ItemMultiTexture(final Block block, final Block block2, final Function<ItemStack, String> nameFunction) {
        super(block);
        this.theBlock = block2;
        this.nameFunction = nameFunction;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }
    
    public ItemMultiTexture(final Block block, final Block block2, final String[] namesByMeta) {
        this(block, block2, (Function<ItemStack, String>)new Function<ItemStack, String>() {
            public String apply(final ItemStack p_apply_1_) {
                int i = p_apply_1_.getMetadata();
                if (i < 0 || i >= namesByMeta.length) {
                    i = 0;
                }
                return namesByMeta[i];
            }
        });
    }
    
    @Override
    public int getMetadata(final int damage) {
        return damage;
    }
    
    @Override
    public String getUnlocalizedName(final ItemStack stack) {
        return super.getUnlocalizedName() + "." + (String)this.nameFunction.apply((Object)stack);
    }
}
