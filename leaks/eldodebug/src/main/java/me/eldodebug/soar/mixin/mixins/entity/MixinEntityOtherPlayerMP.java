package me.eldodebug.soar.mixin.mixins.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.entity.EntityOtherPlayerMP;

@Mixin(EntityOtherPlayerMP.class)
public class MixinEntityOtherPlayerMP {

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityOtherPlayerMP;updateArmSwingProgress()V", shift = At.Shift.AFTER), cancellable = true)
    private void onLivingUpdate(CallbackInfo ci) {
        ci.cancel();
    }
}
