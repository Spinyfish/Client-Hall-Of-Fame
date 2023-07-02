package me.eldodebug.soar.utils.render;

import java.awt.Color;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.ClickEffectMod;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;

public class ClickEffect {
	
	private float x, y;
	
	private SimpleAnimation animation = new SimpleAnimation(0.0F);
	
	public ClickEffect(float x, float y) {
		this.x = x;
		this.y = y;
		animation.setValue(0);
	}
	
	public void draw() {
		animation.setAnimation(100, 12);
        double radius = 8 * animation.getValue() / 100;
        int alpha = (int)(255 - 255 * animation.getValue() / 100);
        boolean accentColor = Soar.instance.settingsManager.getSettingByClass(ClickEffectMod.class, "Accent Color").getValBoolean();
        int color = accentColor ? ColorUtils.getClientColor(0, alpha).getRGB() : new Color(255, 255, 255, alpha).getRGB();
        
        if(Soar.instance.modManager.getModByClass(ClickEffectMod.class).isToggled()) {
            RenderUtils.drawArc(x, y, radius, color, 0, 360, 5);
        }
	}
	
	public boolean canRemove() {
		return animation.getValue() > 99;
	}
}
