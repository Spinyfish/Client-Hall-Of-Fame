package me.eldodebug.soar.management.mods.impl;

import java.awt.Color;

import me.eldodebug.soar.gui.GuiEditHUD;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.GlUtils;
import me.eldodebug.soar.utils.TargetUtils;
import me.eldodebug.soar.utils.animation.normal.Animation;
import me.eldodebug.soar.utils.animation.normal.Direction;
import me.eldodebug.soar.utils.animation.normal.impl.EaseBackIn;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.render.RenderUtils;
import me.eldodebug.soar.utils.render.RoundedUtils;
import me.eldodebug.soar.utils.render.StencilUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class TargetInfoMod extends Mod{

	private String name;
	private float health;
    private Animation introAnimation;
    private ResourceLocation resourceLocation;
    private boolean loaded;
    private SimpleAnimation animation = new SimpleAnimation(0.0F);
    private SimpleAnimation damageAnimation = new SimpleAnimation(0.0F);
    private SimpleAnimation redAnimation = new SimpleAnimation(0.0F);
    
	public TargetInfoMod() {
		super("Target Info", "Render target infomation", ModCategory.HUD);
	}

	@Override
	public void setup() {
		loaded = false;
	}
	
	@EventTarget
	public void onRender2D(EventRender2D event) {

		int scaleOffset;
		float offset;
		
		if(mc.currentScreen instanceof GuiEditHUD) {
			TargetUtils.setTarget(mc.thePlayer);
		}
		
		if(TargetUtils.getTarget() != null) {
			
			if(!loaded) {
		        introAnimation = new EaseBackIn(450, 1, 2);
		        loaded = true;
			}
			
			int healthInit = (int) (((EntityPlayer)TargetUtils.getTarget()).getHealth() > 20 ? 20 : ((EntityPlayer)TargetUtils.getTarget()).getHealth());
			
			introAnimation.setDirection(Direction.FORWARDS);
	        name = TargetUtils.getTarget().getName();
            health = healthInit;
            resourceLocation = ((AbstractClientPlayer)TargetUtils.getTarget()).getLocationSkin();
            scaleOffset = (int) (((EntityPlayer) TargetUtils.getTarget()).hurtTime * 0.35f);
            offset = -(((AbstractClientPlayer) TargetUtils.getTarget()).hurtTime * 23);
		}else {
			if(loaded) {
				introAnimation.setDirection(Direction.BACKWARDS);
			}
			
			scaleOffset = 0;
			offset = 0;
		}
		
		damageAnimation.setAnimation(scaleOffset, 100);
		redAnimation.setAnimation(offset, 100);
		
		if(loaded && name != null && resourceLocation != null) {
			
			GlUtils.startScale(this.getX(), this.getY(), 140, 50, (float) introAnimation.getValue());
			
			this.drawBackground(this.getX(), this.getY(), 140, 50);
			mc.getTextureManager().bindTexture(resourceLocation);
			
	        StencilUtils.initStencilToWrite();
			RenderUtils.drawRound(this.getX() + 5 + damageAnimation.getValue(), this.getY() + 5 + damageAnimation.getValue(), this.getX() + 5 + 30 - damageAnimation.getValue(), this.getY() + 5 + 30 - damageAnimation.getValue(), 4 * 2, this.getFontColor());
	        StencilUtils.readStencilBuffer(1);
	        ColorUtils.setColor(new Color(255, (int) (255 + redAnimation.getValue()), (int) (255 + redAnimation.getValue())).getRGB());
			RenderUtils.drawScaledCustomSizeModalRect(this.getX() + 5 + damageAnimation.getValue(), this.getY() + 5 + damageAnimation.getValue(), 3, 3, 3, 3, 30 - (damageAnimation.getValue() * 2), 30 - (damageAnimation.getValue() * 2), 24, 24.5F);
			StencilUtils.uninitStencilBuffer();
			

			FontUtils.regular20.drawString((int) health + " Health", this.getX() + 40, this.getY() + 18, this.getFontColor().getRGB());
			
	        StencilUtils.initStencilToWrite();
	        RenderUtils.drawRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.getFontColor().getRGB());
	        StencilUtils.readStencilBuffer(1);
			FontUtils.regular_bold22.drawString(name, this.getX() + 40, this.getY() + 5, this.getFontColor().getRGB());
			StencilUtils.uninitStencilBuffer();
			
			animation.setAnimation(health * 6.5F, 12);
			RoundedUtils.drawRound(this.getX() + 5, this.getY() + 40, animation.getValue(), 5, 2, this.getFontColor());
			
			GlUtils.stopScale();
		}
		
		this.setWidth(140);
		this.setHeight(50);
	}
	
	@EventTarget
	public void onRenderShadow(EventRenderShadow event) {
		if(loaded) {
			GlUtils.startScale(this.getX(), this.getY(), 140, 50, (float) introAnimation.getValue());
			this.drawShadow(this.getX(), this.getY(), this.getWidth() - this.getX(), this.getHeight() - this.getY());
			GlUtils.stopScale();
		}
	}
}
