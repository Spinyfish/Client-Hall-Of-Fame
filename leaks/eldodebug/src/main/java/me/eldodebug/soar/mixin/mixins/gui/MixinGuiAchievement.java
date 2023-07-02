package me.eldodebug.soar.mixin.mixins.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.OverlayEditorMod;
import net.minecraft.client.gui.achievement.GuiAchievement;

@Mixin(GuiAchievement.class)
public class MixinGuiAchievement {

    @Inject(method = "updateAchievementWindow", at = @At("HEAD"), cancellable = true)
    private void disableAchievement(CallbackInfo ci) {
    	if(Soar.instance.modManager.getModByClass(OverlayEditorMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(OverlayEditorMod.class, "Disable Achievements").getValBoolean()) {
    		ci.cancel();
    	}
    }
}
