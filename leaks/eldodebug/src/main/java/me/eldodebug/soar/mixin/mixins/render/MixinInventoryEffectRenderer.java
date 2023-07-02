package me.eldodebug.soar.mixin.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.InventoryMod;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.Container;

@Mixin(InventoryEffectRenderer.class)
public abstract class MixinInventoryEffectRenderer extends GuiContainer{

	@Shadow
    private boolean hasActivePotionEffects;
	
	public MixinInventoryEffectRenderer(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}
	
	@Overwrite
    protected void updateActivePotionEffects()
    {
        if (!this.mc.thePlayer.getActivePotionEffects().isEmpty()){
        	if(Soar.instance.modManager.getModByClass(InventoryMod.class).isToggled() && Soar.instance.settingsManager.getSettingByClass(InventoryMod.class, "Prevent Potion Shift").getValBoolean()) {
        		this.guiLeft = (this.width - this.xSize) / 2;
        	}else {
        		this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
        	}
        	
            this.hasActivePotionEffects = true;
        }
        else{
            this.guiLeft = (this.width - this.xSize) / 2;
            this.hasActivePotionEffects = false;
        }
    }
}
