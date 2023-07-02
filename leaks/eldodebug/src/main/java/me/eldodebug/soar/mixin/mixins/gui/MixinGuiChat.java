package me.eldodebug.soar.mixin.mixins.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.ChatMod;
import me.eldodebug.soar.utils.GlUtils;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import net.minecraft.client.gui.GuiChat;

@Mixin(GuiChat.class)
public class MixinGuiChat {

	@Unique
	private SimpleAnimation animation = new SimpleAnimation(0.0F);
	
	@Inject(method = "drawScreen", at = @At("HEAD"))
	public void drawScreenPre(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
		
		if(Soar.instance.modManager.getModByClass(ChatMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(ChatMod.class, "Bar Animation").getValBoolean()) {
			animation.setAnimation(30, 20);
			GlUtils.startTranslate(0, 29 - (int) animation.getValue());
		}
	}
	
	@Inject(method = "drawScreen", at = @At("TAIL"))
	public void drawScreenPost(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
		if(Soar.instance.modManager.getModByClass(ChatMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(ChatMod.class, "Bar Animation").getValBoolean()) {
			GlUtils.stopTranslate();
		}
	}
}
