package me.eldodebug.soar.mixin.mixins.model;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.FPSBoostMod;
import me.eldodebug.soar.utils.interfaces.IMixinWorldRenderer;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;

@Mixin(TexturedQuad.class)
public class MixinTexturedQuad {
	
    @Unique
    private boolean drawOnSelf;
 
    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;begin(ILnet/minecraft/client/renderer/vertex/VertexFormat;)V"))
    private void patcher$beginDraw(WorldRenderer renderer, int glMode, VertexFormat format) {
    	
    	boolean batchRendering = Soar.instance.modManager.getModByClass(FPSBoostMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(FPSBoostMod.class, "Batch Rendering").getValBoolean();
    	
        this.drawOnSelf = !((IMixinWorldRenderer) renderer).isDrawing();
        if (this.drawOnSelf || !batchRendering) {
            renderer.begin(glMode, DefaultVertexFormats.POSITION_TEX_NORMAL);
        }
    }

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()V"))
    private void patcher$endDraw(Tessellator tessellator) {
    	
    	boolean batchRendering = Soar.instance.modManager.getModByClass(FPSBoostMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(FPSBoostMod.class, "Batch Rendering").getValBoolean();
    	
        if (this.drawOnSelf || !batchRendering) {
            tessellator.draw();
        }
    }
}
