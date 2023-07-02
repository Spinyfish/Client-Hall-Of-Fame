package me.eldodebug.soar.mixin.mixins.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.SkinProtectMod;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;

@Mixin(NetworkPlayerInfo.class)
public class MixinNetworkPlayerInfo {

	@Inject(method = "getLocationSkin", at = @At("HEAD"), cancellable = true)
    public void getLocationSkin(CallbackInfoReturnable<ResourceLocation> cir) {
    	if(Soar.instance.modManager.getModByClass(SkinProtectMod.class).isToggled()) {
    		cir.setReturnValue(new ResourceLocation("soar/mods/skinprotect/skin.png"));
    	}
    }
}
