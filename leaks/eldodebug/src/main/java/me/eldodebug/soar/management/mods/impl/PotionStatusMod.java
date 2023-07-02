package me.eldodebug.soar.management.mods.impl;

import java.util.Collection;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.gui.GuiEditHUD;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.GlUtils;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class PotionStatusMod extends Mod{

	protected float zLevelFloat;
	private int index;
	private int maxString = 0;
	private boolean isActive;
	
	public PotionStatusMod() {
		super("Potion Status", "Display the potion that's on you.", ModCategory.HUD);
	}
	
	@Override
	public void setup() {
		this.addBooleanSetting("Compact", this, false);
	}
	
	@EventTarget
	public void onRender2D(EventRender2D event) {
		
		int offsetX = 21;
		int offsetY = 14;
		int i2 = 16;
		int x = this.getX();
		int y = this.getY();
		boolean compact = Soar.instance.settingsManager.getSettingByName(this, "Compact").getValBoolean();
		
		Collection<PotionEffect> collection = this.mc.thePlayer.getActivePotionEffects();
		
		if(mc.currentScreen instanceof GuiEditHUD) {
            
			this.drawBackground(this.getX(), this.getY(), (float) (FontUtils.regular_bold20.getStringWidth("Regeneration") + 29), ((compact ? 16 : 24) * 2) + 2);

            Potion potion = Potion.moveSpeed;
            Potion potion2 = Potion.regeneration;
            
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            
        	mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
            int potionIcon1 = potion.getStatusIconIndex();
            int potionIcon2 = potion2.getStatusIconIndex();
            GlStateManager.enableBlend();
            
            if(compact) {
            	
            	GlUtils.startScale((x + offsetX) - 20, (y + i2) - offsetY - 2, 18, 18, 0.75F);
                RenderUtils.drawTexturedModalRect((x + offsetX) - 20, (y + i2) - offsetY - 2, 0 + potionIcon1 % 8 * 18, 198 + potionIcon1 / 8 * 18, 18, 18);
                RenderUtils.drawTexturedModalRect((x + offsetX) - 20, (y + i2) - (offsetY - 20) - 2, 0 + potionIcon2 % 8 * 18, 198 + potionIcon1 / 8 * 18, 18, 18);
                GlUtils.stopScale();
                
                FontUtils.regular_bold20.drawString("Speed", x + offsetX - 2, (y + i2) - offsetY + 3F, this.getFontColor().getRGB());
                FontUtils.regular_bold20.drawString("Regeneration", x + offsetX - 2, (y + i2) - (offsetY - 20) - 1.5F, this.getFontColor().getRGB());
            }else {
            	RenderUtils.drawTexturedModalRect((x + offsetX) - 17, (y + i2) - offsetY + 2, 0 + potionIcon1 % 8 * 18, 198 + potionIcon1 / 8 * 18, 18, 18);
            	RenderUtils.drawTexturedModalRect((x + offsetX) - 17, (y + i2) - (offsetY - 24) + 2, 0 + potionIcon2 % 8 * 18, 198 + potionIcon1 / 8 * 18, 18, 18);
                
                FontUtils.regular_bold20.drawString("Speed", x + offsetX + 3, (y + i2) - offsetY + 2, this.getFontColor().getRGB());
                FontUtils.regular_bold20.drawString("Regeneration", x + offsetX + 3, (y + i2) - (offsetY - 24) + 2, this.getFontColor().getRGB());
                FontUtils.regular20.drawString("*:**", x + offsetX + 3, (y + i2 + 11) - offsetY + 2, this.getFontColor().getRGB());
                FontUtils.regular20.drawString("*:**", x + offsetX + 3, (y + i2 + 11) - (offsetY - 24) + 2, this.getFontColor().getRGB());
            }
		}else {
			
			if(collection.isEmpty()) {
				maxString = 0;
				isActive = false;
			}
			
			if (!collection.isEmpty())
	        {
	            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	            GlStateManager.disableLighting();
	            int l = compact ? 16 : 24;
	            
	            index = collection.size();
	            isActive = true;
	            
	            this.drawBackground(this.getX(), this.getY(), maxString + 29, (l * index) + 2);
	            
	            for (PotionEffect potioneffect : this.mc.thePlayer.getActivePotionEffects())
	            {
	                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
	                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	                
	                if (potion.hasStatusIcon())
	                {
	                	mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
	                    int i1 = potion.getStatusIconIndex();
	                    GlStateManager.enableBlend();
	                    
	                    if(compact) {
	                    	GlUtils.startScale((x + offsetX) - 20, (y + i2) - offsetY - 2F, 18, 18, 0.75F);
		                    RenderUtils.drawTexturedModalRect((x + offsetX) - 20, (y + i2) - offsetY - 2, 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
	                    	GlUtils.stopScale();
	                    }else {
	                    	RenderUtils.drawTexturedModalRect((x + offsetX) - 17, (y + i2) - offsetY + 2, 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
	                    }
	                }
	                
	                String s1 = I18n.format(potion.getName(), new Object[0]);
	                if (potioneffect.getAmplifier() == 1)
	                {
	                    s1 = s1 + " " + I18n.format("enchantment.level.2", new Object[0]);
	                }
	                else if (potioneffect.getAmplifier() == 2)
	                {
	                    s1 = s1 + " " + I18n.format("enchantment.level.3", new Object[0]);
	                }
	                else if (potioneffect.getAmplifier() == 3)
	                {
	                    s1 = s1 + " " + I18n.format("enchantment.level.4", new Object[0]);
	                }
	                
	                String s = Potion.getDurationString(potioneffect);
	                
	                if(compact) {
		                FontUtils.regular_bold20.drawStringWithUnicode(s1, x + offsetX - 2, (y + i2) - offsetY + 3F, this.getFontColor().getRGB(), false);
	                }else {
		                FontUtils.regular_bold20.drawStringWithUnicode(s1, x + offsetX + 3, (y + i2) - offsetY + 2, this.getFontColor().getRGB(), false);
		                FontUtils.regular20.drawString(s, x + offsetX + 3, (y + i2 + 11) - offsetY + 2, this.getFontColor().getRGB());
	                }

	                i2 += l;
	                
	                if(maxString < fr.getStringWidth(s1)) {
	                	maxString = fr.getStringWidth(s1);
	                }
	            }
	        }
		}
		
		this.setWidth((int) (FontUtils.regular_bold20.getStringWidth("Regeneration") + 29));
		this.setHeight((compact ? 16 : 24 * 2) + 2);
	}
	
	@EventTarget
	public void onRenderShadow(EventRenderShadow event) {
		
		boolean compact = Soar.instance.settingsManager.getSettingByName(this, "Compact").getValBoolean();
		
		if(mc.currentScreen instanceof GuiEditHUD) {
			this.drawShadow(this.getX(), this.getY(), (float) (FontUtils.regular_bold20.getStringWidth("Regeneration") + 29), ((compact ? 16 : 24) * 2) + 2);
		}else if(isActive){
			this.drawShadow(this.getX(), this.getY(), maxString + 29, ((compact ? 16 : 24) * index) + 2);
		}
	}
}