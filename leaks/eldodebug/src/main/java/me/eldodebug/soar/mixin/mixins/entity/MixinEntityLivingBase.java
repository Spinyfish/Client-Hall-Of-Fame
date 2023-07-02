package me.eldodebug.soar.mixin.mixins.entity;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.impl.EventLivingUpdate;
import me.eldodebug.soar.management.mods.impl.SlowSwingMod;
import me.eldodebug.soar.utils.interfaces.IMixinEntityLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase implements IMixinEntityLivingBase{

	@Shadow
	public abstract int getArmSwingAnimationEnd();

	@Inject(method = "getArmSwingAnimationEnd", at = @At("HEAD"), cancellable = true)
	public void changeSwingSpeed(CallbackInfoReturnable<Integer> cir) {
    	if(Soar.instance.modManager.getModByClass(SlowSwingMod.class).isToggled()) {
    		cir.setReturnValue((int) Soar.instance.settingsManager.getSettingByClass(SlowSwingMod.class, "Delay").getValDouble());
    	}
	}
	
    @Inject(method = "updatePotionEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionEffect;onUpdate(Lnet/minecraft/entity/EntityLivingBase;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void updatePotionEffects(CallbackInfo ci, Iterator<Integer> iterator, Integer integer, PotionEffect potioneffect) {
        if (potioneffect == null) {
            ci.cancel();
        }
    }
    
    @Inject(method = "onEntityUpdate", at = @At("TAIL"))
    public void onEntityUpdate(CallbackInfo ci) {
    	EventLivingUpdate event = new EventLivingUpdate((EntityLivingBase) (Object) this);
    	event.call();
    }
    
	@Override
	public int accessArmSwingAnimationEnd() {
		return getArmSwingAnimationEnd();
	}
}