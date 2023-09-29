package me.r.touchgrass.injection.mixins.client;

import me.r.touchgrass.touchgrass;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.module.modules.combat.Reach;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Inject(method = "getBlockReachDistance", at = @At("HEAD"), cancellable = true)
    private void getReach(final CallbackInfoReturnable<Float> returnable) {
        final Module reach = touchgrass.getClient().moduleManager.getModule(Reach.class);
        if (reach.isEnabled()) {
            returnable.setReturnValue((float) touchgrass.getClient().settingsManager.getSettingByName(reach, "Distance").getValue());
        }
    }

}
