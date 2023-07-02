package me.eldodebug.soar.mixin.mixins.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.management.events.impl.EventPreMotionUpdate;
import me.eldodebug.soar.management.events.impl.EventUpdate;
import net.minecraft.client.entity.EntityPlayerSP;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {

	@Inject(method = "onUpdate", at = @At("HEAD"))
	private void onUpdate(CallbackInfo ci) {
		EventUpdate event = new EventUpdate();
		event.call();
	}
	
	@Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"))
	private void onUpdateWalkingPlayer(CallbackInfo ci) {
		EventPreMotionUpdate event = new EventPreMotionUpdate();
		event.call();
	}
}
