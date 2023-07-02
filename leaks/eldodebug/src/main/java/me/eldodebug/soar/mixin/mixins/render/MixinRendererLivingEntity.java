package me.eldodebug.soar.mixin.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.impl.EventHitOverlay;
import me.eldodebug.soar.management.mods.impl.FPSBoostMod;
import me.eldodebug.soar.management.mods.impl.NametagMod;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;

@Mixin(RendererLivingEntity.class)
public class MixinRendererLivingEntity <T extends EntityLivingBase>{
	
	private float red;
	private float green;
	private float blue;
	private float alpha;

	@Inject(method = "doRender", at = @At("HEAD"), cancellable = true)
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
		if(Soar.instance.modManager.getModByClass(FPSBoostMod.class).isToggled()) {
			
			if(Soar.instance.settingsManager.getSettingByClass(FPSBoostMod.class, "Disable Armor Stand").getValBoolean() & entity instanceof EntityArmorStand) {
				ci.cancel();
			}
			
			if(Soar.instance.settingsManager.getSettingByClass(FPSBoostMod.class, "Disable Invisible Player").getValBoolean() & entity.isInvisible() && entity instanceof EntityPlayer) {
				ci.cancel();
			}
		}
	}
	
	@Redirect(method = "canRenderName", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/RenderManager;livingPlayer:Lnet/minecraft/entity/Entity;"))
	public Entity canRenderName(RenderManager renderManager) {
		if(Soar.instance.modManager.getModByClass(NametagMod.class).isToggled()) {
			return null;
		}
		return renderManager.livingPlayer;
	}
	
	@Inject(method = "setBrightness", at = @At("HEAD"))
	public void hitColor(T entitylivingbaseIn, float partialTicks, boolean combineTextures, CallbackInfoReturnable<Boolean> cir) {
		EventHitOverlay event = new EventHitOverlay(1, 0, 0, 0.3F);
		event.call();

		red = event.getRed();
		green = event.getGreen();
		blue = event.getBlue();
		alpha = event.getAlpha();
	}

	@ModifyConstant(method = "setBrightness", constant = @Constant(floatValue = 1, ordinal = 0))
	public float setBrightnessRed(float original) {
		return red;
	}

	@ModifyConstant(method = "setBrightness", constant = @Constant(floatValue = 0, ordinal = 0))
	public float setBrightnessGreen(float original) {
		return green;
	}

	@ModifyConstant(method = "setBrightness", constant = @Constant(floatValue = 0, ordinal = 1))
	public float setBrightnessBlue(float original) {
		return blue;
	}

	@ModifyConstant(method = "setBrightness", constant = @Constant(floatValue = 0.3F, ordinal = 0))
	public float setBrightnessAlpha(float original) {
		return alpha;
	}
}
