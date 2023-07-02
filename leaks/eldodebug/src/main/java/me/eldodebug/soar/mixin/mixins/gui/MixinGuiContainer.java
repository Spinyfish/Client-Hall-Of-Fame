package me.eldodebug.soar.mixin.mixins.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.InventoryMod;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.render.ClickEffect;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends GuiScreen{
	
    @Shadow
    protected abstract boolean checkHotbarKeys(int keyCode);
    
    @Shadow 
    private int dragSplittingButton;
    
    @Shadow 
    private int dragSplittingRemnant;
    
    private SimpleAnimation animation;
    private SimpleAnimation animation2;
    
    private List<ClickEffect> clickEffects = new ArrayList<>();
    
    @Inject(method = "initGui", at = @At("RETURN"))
    public void initGui(CallbackInfo ci) {
        animation = new SimpleAnimation(0.0F);
        animation2 = new SimpleAnimation(0.0F);
    }
    
    @Inject(method = "drawScreen", at = @At("HEAD"))
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
    	
    	animation.setAnimation(this.width, (int) Soar.instance.settingsManager.getSettingByClass(InventoryMod.class, "Speed").getValDouble());
    	animation2.setAnimation(this.height, (int) Soar.instance.settingsManager.getSettingByClass(InventoryMod.class, "Speed").getValDouble());
        
        if((Soar.instance.modManager.getModByClass(InventoryMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(InventoryMod.class, "Transparent background").getValBoolean())) {
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
    		GlStateManager.disableDepth();
    		GlStateManager.disableLighting();
        }
        
    	if(!Soar.instance.modManager.getModByClass(InventoryMod.class).isToggled() || (Soar.instance.modManager.getModByClass(InventoryMod.class).isToggled() && !Soar.instance.settingsManager.getSettingByClass(InventoryMod.class, "Transparent background").getValBoolean())) {
            this.drawDefaultBackground();
    	}
    	
		GlStateManager.enableDepth();
		
    	double xmod = this.width/2-(animation.getValue()/2);
    	double ymod = this.height/2-(animation2.getValue()/2);
    	
    	if(Soar.instance.modManager.getModByClass(InventoryMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(InventoryMod.class, "Animation").getValBoolean()){
        	GlStateManager.translate(xmod,ymod, 0);
        	GlStateManager.scale(animation.getValue()/this.width, animation2.getValue() / this.height, 1);
    	}
    }
    
    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void drawScreenPost(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if(clickEffects.size() > 0) {
            Iterator<ClickEffect> clickEffectIterator= clickEffects.iterator();
            while(clickEffectIterator.hasNext()){
                ClickEffect clickEffect = clickEffectIterator.next();
                clickEffect.draw();
                if (clickEffect.canRemove()) clickEffectIterator.remove();
            }
        }
    }
    
    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/inventory/GuiContainer;drawDefaultBackground()V"))
    public void removeDrawDefaultBackground() {}
    
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void mouseClickedHead(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
    	
        ClickEffect clickEffect = new ClickEffect(mouseX, mouseY);
        clickEffects.add(clickEffect);
        
        if (mouseButton - 100 == mc.gameSettings.keyBindInventory.getKeyCode()) {
            mc.thePlayer.closeScreen();
            ci.cancel();
        }
    }

    @Inject(method = "mouseClicked", at = @At("RETURN"))
    private void mouseClickedTail(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        checkHotbarKeys(mouseButton - 100);
    }
    
    @Inject(method = "updateDragSplitting", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"), cancellable = true)
    private void updateDragSplitting(CallbackInfo ci) {
        if (this.dragSplittingButton == 2) {
            this.dragSplittingRemnant = mc.thePlayer.inventory.getItemStack().getMaxStackSize();
            ci.cancel();
        }
    }
    
    @Redirect(method = "mouseClicked", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;touchscreen:Z", ordinal = 0))
    private boolean clickOut(GameSettings instance) {
        return (Soar.instance.modManager.getModByClass(InventoryMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(InventoryMod.class, "Click out of Container").getValBoolean())
        		|| instance.touchscreen;
    }
}