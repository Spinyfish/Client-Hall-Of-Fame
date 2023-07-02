package me.eldodebug.soar.mixin.mixins.layer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerWitherAura;

@Mixin(LayerWitherAura.class)
public class MixinLayerWitherAura {

    @Inject(method = "doRenderLayer(Lnet/minecraft/entity/boss/EntityWither;FFFFFFF)V", at = @At("TAIL"))
    private void fixDepth(CallbackInfo ci) {
        GlStateManager.depthMask(true);
    }
}
