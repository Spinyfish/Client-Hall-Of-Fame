package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.render.RenderUtils;
import me.eldodebug.soar.utils.render.StencilUtils;
import net.minecraft.util.MathHelper;

public class CompassMod extends Mod{

	public CompassMod() {
		super("Compass", "Display direction", ModCategory.HUD);
	}
	
	@Override
	public void setup() {
		this.addSliderSetting("Width", this, 180, 50, 450, true);
	}
	
	@EventTarget
	public void onRender2D(EventRender2D event) {
		
		int angle = (int) MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) * -1 - 360;
		int angle2 = (int) MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) * -1 - 360;
		int angle3 = (int) MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) * -1 - 360;
		int angle4 = (int) MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) * -1 - 360;
		int angle5 = (int) MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) * -1 - 360;
		int angle6 = (int) MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) * -1 - 360;
		int width = Soar.instance.settingsManager.getSettingByName(this, "Width").getValInt();

		this.drawBackground(this.getX(), this.getY(), width, 28);
		
		RenderUtils.renderMarker((this.getX() + this.getX() + width) / 2, this.getY() + 2.5F, this.getFontColor().getRGB());
		
        StencilUtils.initStencilToWrite();
        RenderUtils.drawRect(this.getX(), this.getY(), width, 28, -1);
        StencilUtils.readStencilBuffer(1);
        
		for(int i = 0; i<=2; i++) {
			for(double d = 0.0D; d<= 1.5D; d+=0.5D) {
				
				String s = "W";
				
				if(d == 0.0D) {
					s = "S";
				}
				
				if(d == 1.0D) {
					s = "N";
				}
				
				if(d == 1.5D) {
					s = "E";
				}
				
				RenderUtils.drawRect(this.getX() + (width / 2) + angle - 2F, this.getY() + 8, 1, 9, this.getFontColor().getRGB());
				
				RenderUtils.drawRect(this.getX() + (width / 2) + angle + 12F, this.getY() + 8, 1, 6, this.getFontColor().getRGB());
				RenderUtils.drawRect(this.getX() + (width / 2) + angle + 26F, this.getY() + 8, 1, 6, this.getFontColor().getRGB());
				
				RenderUtils.drawRect(this.getX() + (width / 2) + angle - 16F, this.getY() + 8, 1, 6, this.getFontColor().getRGB());
				RenderUtils.drawRect(this.getX() + (width / 2) + angle - 30F, this.getY() + 8, 1, 6, this.getFontColor().getRGB());
				
				FontUtils.regular_bold18.drawCenteredString(s, this.getX() + (width / 2) + angle - 1.5F, this.getY() + 19, this.getFontColor().getRGB());
	            
				angle+= 90;
			}
			
			for(double d = 0.0D; d<= 1.5D; d+=0.5D) {
				
				String s = "NW";
				
				if(d == 0.0D) {
					s = "SW";
				}
				
				if(d == 1.0D) {
					s = "NE";
				}
				
				if(d == 1.5D) {
					s = "SE";
				}
				
				
				FontUtils.regular16.drawCenteredString(s, this.getX() + (width / 2) + angle2 + 43F, this.getY() + 9, this.getFontColor().getRGB());
				
				angle2+= 90;
			}
			
			for(double d = 0.0D; d<= 1.5D; d+=0.5D) {
				
				String s = "105";
				
				if(d == 0.0D) {
					s = "15";
				}
				
				if(d == 1.0D) {
					s = "195";
				}
				
				if(d == 1.5D) {
					s = "285";
				}
				
				FontUtils.regular12.drawCenteredString(s, this.getX() + (width / 2) + angle3 + 13F, this.getY() + 17, this.getFontColor().getRGB());
				
				angle3+= 90;
			}
			
			for(double d = 0.0D; d<= 1.5D; d+=0.5D) {
				
				String s = "120";
				
				if(d == 0.0D) {
					s = "30";
				}
				
				if(d == 1.0D) {
					s = "210";
				}
				
				if(d == 1.5D) {
					s = "300";
				}
				
				FontUtils.regular12.drawCenteredString(s, this.getX() + (width / 2) + angle4 + 27F, this.getY() + 17, this.getFontColor().getRGB());
			
				
				angle4+= 90;
			}
			
			for(double d = 0.0D; d<= 1.5D; d+=0.5D) {
				
				String s = "150";
				
				if(d == 0.0D) {
					s = "60";
				}
				
				if(d == 1.0D) {
					s = "240";
				}
				
				if(d == 1.5D) {
					s = "300";
				}
				
				FontUtils.regular12.drawCenteredString(s, this.getX() + (width / 2) + angle5 + 60.5F, this.getY() + 17, this.getFontColor().getRGB());
			
				
				angle5+= 90;
			}
			
			for(double d = 0.0D; d<= 1.5D; d+=0.5D) {
				
				String s = "165";
				
				if(d == 0.0D) {
					s = "70";
				}
				
				if(d == 1.0D) {
					s = "255";
				}
				
				if(d == 1.5D) {
					s = "345";
				}
				
				FontUtils.regular12.drawCenteredString(s, this.getX() + (width / 2) + angle6 + 74.5F, this.getY() + 17, this.getFontColor().getRGB());
				
				angle6+= 90;
			}
		}
		
        StencilUtils.uninitStencilBuffer();
		
		this.setWidth(width);
		this.setHeight(28);
	}
	
	@EventTarget
	public void onRenderShadow(EventRenderShadow event) {
		
		int width = Soar.instance.settingsManager.getSettingByName(this, "Width").getValInt();
		
		this.drawShadow(this.getX(), this.getY(), width, 28);
	}
}
