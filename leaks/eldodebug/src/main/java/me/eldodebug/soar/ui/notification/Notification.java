package me.eldodebug.soar.ui.notification;

import java.awt.Color;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.utils.TimerUtils;
import me.eldodebug.soar.utils.animation.normal.Animation;
import me.eldodebug.soar.utils.animation.normal.Direction;
import me.eldodebug.soar.utils.animation.normal.impl.DecelerateAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.font.FontUtils;
import me.eldodebug.soar.utils.render.RoundedUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Notification {
	
    private String title;
    private String messsage;

    private Animation animation;
    private TimerUtils timer = new TimerUtils();
    
    private boolean loaded = false;
    
    public void setNotification(String title, String message) {
    	this.title = title;
    	this.messsage = message;
    }

    public void show() {
        animation = new DecelerateAnimation(250, FontUtils.regular_bold20.getStringWidth(messsage) + 12);
    	timer.reset();
    }

    public boolean isShown() {
        return !animation.isDone(Direction.BACKWARDS);
    }

    public void render() {
    	
    	ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    	
    	if(Soar.instance.eventManager != null) {
            if(!loaded) {
            	loaded = true;
            	Soar.instance.eventManager.register(this);
            }
    	}
    	
        double offset = animation.getValue();

        if(timer.delay(3000)) {
        	animation.setDirection(Direction.BACKWARDS);
        }
        
    	RoundedUtils.drawGradientRound((float) (sr.getScaledWidth() - offset), sr.getScaledHeight() - 40, (float) (FontUtils.regular_bold20.getStringWidth(messsage) + 1), 29, 6, ColorUtils.getBackgroundColor(4), ColorUtils.getBackgroundColor(4), ColorUtils.getBackgroundColor(4), ColorUtils.getBackgroundColor(4));
        FontUtils.regular_bold26.drawString(title, sr.getScaledWidth() - offset + 3, sr.getScaledHeight() - 38, ColorUtils.getFontColor(2).getRGB());
        
        FontUtils.regular20.drawString(messsage, sr.getScaledWidth() - offset + 3, sr.getScaledHeight() - 22, ColorUtils.getFontColor(2).getRGB());
    }
    
    @EventTarget
    public void onRenderShadow(EventRenderShadow event) {
    	
    	ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    	
    	if(animation != null) {
            double offset = animation.getValue();
    		RoundedUtils.drawRound((float) (sr.getScaledWidth() - offset), sr.getScaledHeight() - 40, (float) (FontUtils.regular_bold20.getStringWidth(messsage) + 1), 29, 6, new Color(0, 0, 0));
    	}
    }
}
