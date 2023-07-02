package me.eldodebug.soar.mixin.mixins.particle;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.FPSBoostMod;
import net.minecraft.client.particle.EffectRenderer;

@Mixin(EffectRenderer.class)
public class MixinEffectRenderer {

	@Inject(method = "addBlockHitEffects", at = @At("HEAD"), cancellable = true)
	public void removeBlockHitEffects(CallbackInfo ci) {
		if(Soar.instance.modManager.getModByClass(FPSBoostMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(FPSBoostMod.class, "Remove Block Effects").getValBoolean()) {
			ci.cancel();
		}
	}
	
	@Inject(method = "addBlockDestroyEffects", at = @At("HEAD"), cancellable = true)
	public void removeBlockHitEffects2(CallbackInfo ci) {
		if(Soar.instance.modManager.getModByClass(FPSBoostMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(FPSBoostMod.class, "Remove Block Effects").getValBoolean()) {
			ci.cancel();
		}
	}
	
    @ModifyConstant(method = "addEffect", constant = @Constant(intValue = 4000))
    private int limitParticle(int original) {
    	
    	boolean limit = Soar.instance.modManager.getModByClass(FPSBoostMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(FPSBoostMod.class, "Limit Particle").getValBoolean();
    	
        return limit ? 100 : original;
    }
}
