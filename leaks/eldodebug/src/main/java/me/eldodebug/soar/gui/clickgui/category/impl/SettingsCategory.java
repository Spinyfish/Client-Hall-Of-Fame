package me.eldodebug.soar.gui.clickgui.category.impl;

import java.awt.Color;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.gui.clickgui.category.Category;
import me.eldodebug.soar.management.colors.AccentColor;
import me.eldodebug.soar.management.mods.impl.ClientMod;
import me.eldodebug.soar.utils.GlUtils;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils.Scroll;
import me.eldodebug.soar.utils.render.RoundedUtils;

public class SettingsCategory extends Category{

	private double scrollX;
	private SimpleAnimation scrollAnimation = new SimpleAnimation(0.0F);
	private SimpleAnimation themeAnimation1 = new SimpleAnimation(0.0F);
	private SimpleAnimation themeAnimation2 = new SimpleAnimation(0.0F);
	private boolean canToggle;
	
	public SettingsCategory() {
		super("Settings");
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		int offsetX = 14;
		boolean darkMode = Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "DarkMode").getValBoolean();
		int colorIndex = 1;
		int themeOpacity1 = (int) themeAnimation1.getValue();
		int themeOpacity2 = (int) themeAnimation2.getValue();
		
		RoundedUtils.drawRound(this.getX() + 95, this.getY() + 15, 200, 130, 6, ColorUtils.getBackgroundColor(4));
		FontUtils.regular24.drawString("Theme", this.getX() + 105, this.getY() + 22, ColorUtils.getFontColor(1).getRGB());
		
		themeAnimation1.setAnimation(darkMode ? 255 : 0, 12);
		themeAnimation2.setAnimation(darkMode ? 0 : 255, 12);
		
		RoundedUtils.drawGradientRound(this.getX() + 106 - 1, this.getY() + 40 - 1, 80 + 2, 80 + 2, 6, ColorUtils.getClientColor(0, themeOpacity1), ColorUtils.getClientColor(90, themeOpacity1), ColorUtils.getClientColor(180, themeOpacity1), ColorUtils.getClientColor(270, themeOpacity1));
		RoundedUtils.drawGradientRound(this.getX() + 205 - 1, this.getY() + 40 - 1, 80 + 2, 80 + 2, 6, ColorUtils.getClientColor(0, themeOpacity2), ColorUtils.getClientColor(90, themeOpacity2), ColorUtils.getClientColor(180, themeOpacity2), ColorUtils.getClientColor(270, themeOpacity2));
		
		//Dark
		RoundedUtils.drawRound(this.getX() + 106, this.getY() + 40, 80, 80, 6, new Color(26, 33, 42));
		
		//Light
		RoundedUtils.drawRound(this.getX() + 205, this.getY() + 40, 80, 80, 6, new Color(232, 234, 240));
		
		FontUtils.regular20.drawCenteredString("Dark", ((this.getX() + 106) + (this.getX() + 106 + 80)) / 2, this.getY() + 130, ColorUtils.getFontColor(2).getRGB());
		
		FontUtils.regular20.drawCenteredString("Light", ((this.getX() + 205) + (this.getX() + 205 + 80)) / 2, this.getY() + 130, ColorUtils.getFontColor(2).getRGB());
		
		RoundedUtils.drawRound(this.getX() + 95, this.getY() + 155, 200, 52, 6, ColorUtils.getBackgroundColor(4));
		FontUtils.regular24.drawString("Accent Color", this.getX() + 105, this.getY() + 162, ColorUtils.getFontColor(1).getRGB());
        


		for(AccentColor c : Soar.instance.colorManager.getColors()) {
			
			Color color1 = ColorUtils.interpolateColorsBackAndForth(15, 0, c.getColor1(), c.getColor2(), false);
			Color color2 = ColorUtils.interpolateColorsBackAndForth(15, 90, c.getColor1(), c.getColor2(), false);
			Color color3 = ColorUtils.interpolateColorsBackAndForth(15, 180, c.getColor1(), c.getColor2(), false);
			Color color4 = ColorUtils.interpolateColorsBackAndForth(15, 270, c.getColor1(), c.getColor2(), false);
			
			boolean selectedColor = Soar.instance.colorManager.getClientColor().equals(c);
			
			RoundedUtils.drawGradientRound(this.getX() + 95 + offsetX + scrollAnimation.getValue(), this.getY() + 178 , 20, 20, 10, color1, color2, color3, color4);
			
			c.opacityAnimation.setAnimation(selectedColor ? 255 : 0, 14);
			c.zoomAnimation.setAnimation(selectedColor ? 1 : 0, 14);
			
			GlUtils.startScale(this.getX() + 95 + offsetX + scrollAnimation.getValue() + 6, this.getY() + 178 + 6, 20 - 12, 20 - 12, c.zoomAnimation.getValue());
			RoundedUtils.drawRound(this.getX() + 95 + offsetX + scrollAnimation.getValue() + 6, this.getY() + 178 + 6, 20 - 12, 20 - 12, 4, ColorUtils.getBackgroundColor(4, (int) c.opacityAnimation.getValue()));
			GlUtils.stopScale();
			
			offsetX+=28;
			colorIndex++;
		}

		
        //�X�N���[������
        final Scroll scroll = MouseUtils.scroll();

        if(scroll != null) {
        	switch (scroll) {
        	case DOWN:
        		if(scrollX > -((colorIndex - 7.5) * 28)) {
        			scrollX -=20;
        		}
        		
        		if(colorIndex > 5) {
            		if(scrollX < -((colorIndex - 9) * 28)) {
            			scrollX = -((colorIndex - 7.6) * 28);
            		}
        		}
        		break;
            case UP:
        		if(scrollX < -10) {
        			scrollX +=20;
        		}else {
            		if(colorIndex > 5) {
            			scrollX = 0;
            		}
        		}
        		break;
        	}
        }
        
        scrollAnimation.setAnimation((float) scrollX, 16);
        
        if(MouseUtils.isInside(mouseX, mouseY, this.getX() + 95, this.getY() + 155, 200, 52)) {
        	canToggle = true;
        }else {
        	canToggle = false;
        }
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		
		int offsetX = 14;
		
		if(mouseButton == 0) {
			if(MouseUtils.isInside(mouseX, mouseY, this.getX() + 106, this.getY() + 40, 80, 80)) {
				Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "DarkMode").setValBoolean(true);
			}
			if(MouseUtils.isInside(mouseX, mouseY, this.getX() + 205, this.getY() + 40, 80, 80)) {
				Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "DarkMode").setValBoolean(false);
			}
			for(AccentColor c : Soar.instance.colorManager.getColors()) {
				if(MouseUtils.isInside(mouseX, mouseY, this.getX() + 95 + offsetX + scrollAnimation.getValue(), this.getY() + 178 , 20, 20) && canToggle) {
					Soar.instance.colorManager.setClientColor(c);
				}
				
				offsetX+=28;
			}
		}
	}
}
