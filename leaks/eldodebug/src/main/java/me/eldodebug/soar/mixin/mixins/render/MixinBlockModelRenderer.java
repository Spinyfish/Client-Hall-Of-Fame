package me.eldodebug.soar.mixin.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;

@Mixin(targets = "net.minecraft.client.renderer.BlockModelRenderer$AmbientOcclusionFace")
public class MixinBlockModelRenderer {
	
    @Redirect(method = "updateVertexBrightness(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/block/Block;Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;[FLjava/util/BitSet;)V", at = @At(value = "INVOKE", target =  "Lnet/minecraft/block/Block;isTranslucent()Z"))
    private boolean betterSmoothLighting(Block block) {
        return !block.isVisuallyOpaque() || block.getLightOpacity() == 0;
    }
}
