package me.eldodebug.soar.mixin.mixins.render;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.impl.EventTransformFirstPersonItem;
import me.eldodebug.soar.management.mods.impl.ClearWaterMod;
import me.eldodebug.soar.management.mods.impl.OldAnimationsMod;
import me.eldodebug.soar.management.mods.impl.OverlayEditorMod;
import me.eldodebug.soar.management.mods.impl.SmallHeldItemsMod;
import me.eldodebug.soar.utils.interfaces.IMixinMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

	@Shadow 
	private ItemStack itemToRender;
	
	@Shadow 
	protected abstract void transformFirstPersonItem(float equipProgress, float swingProgress);

	@Shadow 
	@Final 
	private Minecraft mc;
	

	@Inject(method = "transformFirstPersonItem", at = @At("HEAD"))
	public void transformFirstPersonItem(float equipProgress, float swingProgress, CallbackInfo callback) {
		EventTransformFirstPersonItem event = new EventTransformFirstPersonItem(itemToRender, equipProgress, swingProgress);
		event.call();
	}
	
    @Inject(method = "renderWaterOverlayTexture", at = @At("HEAD"), cancellable = true)
    private void renderWaterOverlayTexture(CallbackInfo ci) {
        if (Soar.instance.modManager.getModByClass(ClearWaterMod.class).isToggled()) {
            ci.cancel();
        }
    }
    
	@Inject(method = "renderItemInFirstPerson", at = @At("HEAD"))
	public void renderItemInFirstPerson(CallbackInfo ci) {
    	if(Soar.instance.modManager.getModByClass(SmallHeldItemsMod.class).isToggled()) {
    		
    		double x = Soar.instance.settingsManager.getSettingByClass(SmallHeldItemsMod.class, "X").getValDouble();
    		double y = Soar.instance.settingsManager.getSettingByClass(SmallHeldItemsMod.class, "Y").getValDouble();
    		double z = Soar.instance.settingsManager.getSettingByClass(SmallHeldItemsMod.class, "Z").getValDouble();
    		
    		GlStateManager.translate(x, y, z);
    	}
	}
	
	@Redirect(method = "renderItemInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;transformFirstPersonItem(FF)V"))
	public void allowUseAndSwing(ItemRenderer itemRenderer, float equipProgress, float swingProgress) {
		transformFirstPersonItem(equipProgress, swingProgress == 0.0F && Soar.instance.modManager.getModByClass(OldAnimationsMod.class).isToggled() &&
				Soar.instance.settingsManager.getSettingByClass(OldAnimationsMod.class, "Block Hit").getValBoolean() ?
				mc.thePlayer.getSwingProgress(((IMixinMinecraft)mc).getTimer().renderPartialTicks) : swingProgress);
	}
	
    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"))
    private void renderFireInFirstPersonPre(float partialTicks, CallbackInfo ci) {
        GlStateManager.pushMatrix();
    	if(Soar.instance.modManager.getModByClass(OverlayEditorMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(OverlayEditorMod.class, "Fire").getValBoolean()) {
            GlStateManager.translate(0, Soar.instance.settingsManager.getSettingByClass(OverlayEditorMod.class, "Fire Height").getValFloat(), 0);
    	}
    }

    @Inject(method = "renderFireInFirstPerson", at = @At("RETURN"))
    private void renderFireInFirstPersonPost(float partialTicks, CallbackInfo ci) {
        GlStateManager.popMatrix();
    }
}
