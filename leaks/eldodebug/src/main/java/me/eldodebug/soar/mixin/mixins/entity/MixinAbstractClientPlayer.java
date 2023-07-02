package me.eldodebug.soar.mixin.mixins.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.impl.EventFovUpdate;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;

@Mixin(AbstractClientPlayer.class)
public class MixinAbstractClientPlayer{

	@Inject(method = "getFovModifier", at = @At("RETURN"), cancellable = true)
    public void getFovModifier(CallbackInfoReturnable<Float> cir) {
        EventFovUpdate event = new EventFovUpdate((AbstractClientPlayer) (Object)this, cir.getReturnValue());
        event.call();
        
        cir.setReturnValue(event.getFov());
    }
	
	@Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
	public void getLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
		if(Soar.instance.cosmeticManager == null) {
			return;
		}
		
		if(!Soar.instance.cosmeticManager.getCurrentCpae().equals("None")) {
			cir.setReturnValue(Soar.instance.cosmeticManager.getCosmeticByName(Soar.instance.cosmeticManager.getCurrentCpae()).getCape());
		}
	}
}
