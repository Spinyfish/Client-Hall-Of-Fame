package me.eldodebug.soar.mixin.mixins.layer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.OldAnimationsMod;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;

@Mixin(LayerArmorBase.class)
public class MixinLayerArmorBase {
	@Inject(method = "shouldCombineTextures", at = @At("HEAD"), cancellable = true)
	public void shouldCombineTextures(CallbackInfoReturnable<Boolean> cir) {
		if(Soar.instance.modManager.getModByClass(OldAnimationsMod.class).isToggled() && 
				Soar.instance.settingsManager.getSettingByClass(OldAnimationsMod.class, "Armor Damage").getValBoolean()) {
			cir.setReturnValue(true);
		}
	}
}
