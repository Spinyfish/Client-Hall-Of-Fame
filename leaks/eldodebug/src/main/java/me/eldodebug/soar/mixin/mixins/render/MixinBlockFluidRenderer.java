package me.eldodebug.soar.mixin.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.renderer.BlockFluidRenderer;

@Mixin(BlockFluidRenderer.class)
public class MixinBlockFluidRenderer {
	
    @ModifyConstant(method = "renderFluid", constant = @Constant(floatValue = 0.001F))
    private float renderFluid(float original) {
        return 0.0F;
    }
}
