package me.eldodebug.soar.mixin.mixins.tileentity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.utils.interfaces.ICullable;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;

@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRendererDispatcher {

    @Inject(method = "renderTileEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getCombinedLight(Lnet/minecraft/util/BlockPos;I)I"))
    private void enableLighting(CallbackInfo ci) {
        RenderHelper.enableStandardItemLighting();
    }
    
	@Inject(method = "renderTileEntity", at = @At("HEAD"), cancellable = true)
	public void renderTileEntity(TileEntity tileentityIn, float partialTicks, int destroyStage, CallbackInfo callback) {
		if(((ICullable) tileentityIn).isCulled()) {
			callback.cancel();
		}
	}
}
