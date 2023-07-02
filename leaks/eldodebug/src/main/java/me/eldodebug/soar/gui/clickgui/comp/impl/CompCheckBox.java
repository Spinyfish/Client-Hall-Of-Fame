package me.eldodebug.soar.gui.clickgui.comp.impl;

import java.awt.Color;

import me.eldodebug.soar.gui.clickgui.category.impl.FeatureCategory;
import me.eldodebug.soar.gui.clickgui.comp.Comp;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.settings.Setting;
import me.eldodebug.soar.utils.GlUtils;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils;
import me.eldodebug.soar.utils.render.RoundedUtils;

public class CompCheckBox extends Comp {

	private SimpleAnimation animation = new SimpleAnimation(0.0F);
	private SimpleAnimation animation2 = new SimpleAnimation(0.0F);
	
    public CompCheckBox(double x, double y, FeatureCategory parent, Mod mod, Setting setting) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.mod = mod;
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
    	
    	animation.setAnimation(setting.getValBoolean() ? 1 : 0, 10);
    	animation2.setAnimation(setting.getValBoolean() ? 255 : 0, 12);
    	
    	RoundedUtils.drawRound((float) (parent.getX() + x - 70), (float) (parent.getY() + y), 10, 10, 3, ColorUtils.getBackgroundColor(2));
    	
    	GlUtils.startScale((float) (parent.getX() + x - 70 + parent.getX() + x - 70 + 10) / 2, (float) (parent.getY() + y + parent.getY() + y + 10) / 2, animation.getValue());
    	RoundedUtils.drawRound((float) (parent.getX() + x - 70), (float) (parent.getY() + y), 10, 10, 3, ColorUtils.getClientColor(0, (int) animation2.getValue()));
    	FontUtils.icon20.drawString("H", (parent.getX() + x - 70), (parent.getY() + y + 3), new Color(255, 255, 255).getRGB());
    	GlUtils.stopScale();
    	
        FontUtils.regular20.drawString(setting.getName(), (int)(parent.getX() + x - 55), (parent.getY() + y + 2), ColorUtils.getFontColor(2).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (MouseUtils.isInside(mouseX, mouseY, parent.getX() + x - 70, parent.getY() + y, 10, 10) && mouseButton == 0) {
            setting.setValBoolean(!setting.getValBoolean());
        }
    }

}
