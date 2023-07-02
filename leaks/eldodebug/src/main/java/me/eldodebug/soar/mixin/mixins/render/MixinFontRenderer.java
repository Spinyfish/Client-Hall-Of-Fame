package me.eldodebug.soar.mixin.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.impl.EventText;
import me.eldodebug.soar.management.mods.impl.FPSBoostMod;
import net.minecraft.client.gui.FontRenderer;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer {
	
	@Shadow
    public abstract int drawString(String text, float x, float y, int color, boolean dropShadow);
    
    @Shadow 
    protected abstract void resetStyles();
    
    @Overwrite
    public int drawStringWithShadow(String text, float x, float y, int color) {
    	if(Soar.instance.modManager.getModByClass(FPSBoostMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(FPSBoostMod.class, "Remove Font Shadow").getValBoolean()) {
            return this.drawString(text, (float)x, (float)y, color, false);
    	}
        return this.drawString(text, (float)x, (float)y, color, true);
    }
    
    @ModifyVariable(method = "renderString", at = @At("HEAD"), ordinal = 0)
    private String renderString(String text) {
    	if(text == null || Soar.instance.eventManager == null) {
    		return text;
    	}
    	
    	EventText event = new EventText(text);
    	event.call();
    	
    	return event.getOutputText();
    }
    
    @ModifyVariable(method = "getStringWidth", at = @At("HEAD"), ordinal = 0)
    private String getStringWidth(String text) {
    	if(text == null || Soar.instance.eventManager == null) {
    		return text;
    	}
    	
    	EventText event = new EventText(text);
    	event.call();
    	
    	return event.getOutputText();
    }
    
    @Inject(method = "drawString(Ljava/lang/String;FFIZ)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;renderString(Ljava/lang/String;FFIZ)I", ordinal = 0, shift = At.Shift.AFTER))
    private void drawString(CallbackInfoReturnable<Integer> ci) {
        this.resetStyles();
    }
}
