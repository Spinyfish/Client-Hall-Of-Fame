package me.eldodebug.soar.mixin.mixins.tileentity;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.FPSBoostMod;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;

@Mixin(TileEntitySkullRenderer.class)
public class MixinTileEntitySkullRenderer {

    @Inject(method = "renderSkull", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    private void enableBlending(CallbackInfo ci) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
    }
    
    @Inject(method = "renderTileEntityAt(Lnet/minecraft/tileentity/TileEntitySkull;DDDFI)V", at = @At("HEAD"), cancellable = true)
    private void disableSkulls(CallbackInfo ci) {
        if (Soar.instance.modManager.getModByClass(FPSBoostMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(FPSBoostMod.class, "Disable Skulls").getValBoolean()) {
            ci.cancel();
        }
    }
}
