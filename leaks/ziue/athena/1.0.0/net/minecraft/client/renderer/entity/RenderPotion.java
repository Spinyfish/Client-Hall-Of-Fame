package net.minecraft.client.renderer.entity;

import net.minecraft.entity.projectile.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.entity.*;

public class RenderPotion extends RenderSnowball<EntityPotion>
{
    public RenderPotion(final RenderManager renderManagerIn, final RenderItem itemRendererIn) {
        super(renderManagerIn, Items.potionitem, itemRendererIn);
    }
    
    @Override
    public ItemStack func_177082_d(final EntityPotion entityIn) {
        return new ItemStack(this.field_177084_a, 1, entityIn.getPotionDamage());
    }
}
