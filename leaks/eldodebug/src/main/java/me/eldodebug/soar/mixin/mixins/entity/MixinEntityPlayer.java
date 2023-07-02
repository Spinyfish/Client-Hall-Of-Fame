package me.eldodebug.soar.mixin.mixins.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.management.events.impl.EventAttackEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@Mixin(EntityPlayer.class)
public class MixinEntityPlayer {
	
	@Inject(method = "attackTargetEntityWithCurrentItem", at = @At("HEAD"))
	public void attackEntity(Entity entity, CallbackInfo ci) {
		if(entity.canAttackWithItem()) {
			EventAttackEntity event = new EventAttackEntity(entity);
			event.call();
		}
	}
}
