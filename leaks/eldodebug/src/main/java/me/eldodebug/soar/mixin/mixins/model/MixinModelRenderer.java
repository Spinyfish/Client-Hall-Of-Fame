package me.eldodebug.soar.mixin.mixins.model;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.FPSBoostMod;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

@Mixin(ModelRenderer.class)
public class MixinModelRenderer {

	@Shadow
	private boolean compiled;
	
	private boolean compiledState;
	
    @Inject(method = "render", at = @At("HEAD"))
    private void resetCompiled(float j, CallbackInfo ci) {
    	
    	boolean batchRendering = Soar.instance.modManager.getModByClass(FPSBoostMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(FPSBoostMod.class, "Batch Rendering").getValBoolean();
    	
        if (compiledState != batchRendering) {
            this.compiled = false;
        }
    }

    @Inject(method = "compileDisplayList", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/renderer/Tessellator;getWorldRenderer()Lnet/minecraft/client/renderer/WorldRenderer;"))
    private void beginRendering(CallbackInfo ci) {
    	
    	boolean batchRendering = Soar.instance.modManager.getModByClass(FPSBoostMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(FPSBoostMod.class, "Batch Rendering").getValBoolean();
    	
        this.compiledState = batchRendering;
        if (batchRendering) {
            Tessellator.getInstance().getWorldRenderer().begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
        }
    }

    @Inject(method = "compileDisplayList", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEndList()V", remap = false))
    private void draw(CallbackInfo ci) {
    	
    	boolean batchRendering = Soar.instance.modManager.getModByClass(FPSBoostMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(FPSBoostMod.class, "Batch Rendering").getValBoolean();
    	
        if (batchRendering) {
            Tessellator.getInstance().draw();
        }
    }
}
