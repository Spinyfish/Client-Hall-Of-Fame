package me.eldodebug.soar.mixin.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import me.eldodebug.soar.hooks.RenderEntityItemHook;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

@Mixin(RenderEntityItem.class)
public abstract class MixinRenderEntityItem {

	@Shadow
    public abstract int func_177078_a(ItemStack stack);
	
	@Overwrite
	private int func_177077_a(EntityItem itemIn, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_) {
		return RenderEntityItemHook.func_177077_a(itemIn, p_177077_2_, p_177077_4_, p_177077_6_, p_177077_8_, p_177077_9_, func_177078_a(itemIn.getEntityItem()));
	}
}
