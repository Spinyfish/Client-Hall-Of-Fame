package me.eldodebug.soar.gui.clickgui.category.setting;

import java.math.BigDecimal;
import java.math.RoundingMode;

import me.eldodebug.soar.management.settings.Setting;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.mouse.MouseUtils;
import me.eldodebug.soar.utils.render.RoundedUtils;

public class SettingSlider {

    private boolean dragging = false;
    private double renderWidth;
    private double renderWidth2;

    private int size;
    private SimpleAnimation animation = new SimpleAnimation(0.0F);
    
    private double x, y;
    private Setting setting;
    
    public void setPosition(double x, double y, int size, Setting setting) {
    	this.x = x;
    	this.y = y;
    	this.setting = setting;
    	this.size = size;
    }

    public void drawScreen(int mouseX, int mouseY) {

        double min = setting.getMin();
        double max = setting.getMax();
        double l = size;

        renderWidth = (l) * (setting.getValDouble() - min) / (max - min);
        renderWidth2 = (l) * (setting.getMax() - min) / (max - min);

        animation.setAnimation((float) renderWidth, 14);
        
        double diff = Math.min(l, Math.max(0, mouseX - (x - 70)));
        
        if (dragging) {
            if (diff == 0) {
                setting.setValDouble(setting.getMin());
            }
            else {
                double newValue = roundToPlace(((diff / l) * (max - min) + min), 2);
                setting.setValDouble(newValue);
            }
        }
        
        RoundedUtils.drawRound((float) (x - 70), (float) (y + 13), (float) (renderWidth2), 6, 3, ColorUtils.getBackgroundColor(2));
        RoundedUtils.drawGradientRoundLR((float) (x - 70), (float) (y + 13), animation.getValue(), 6, 3, ColorUtils.getClientColor(0), ColorUtils.getClientColor(20));
        RoundedUtils.drawRound((float) (x) + animation.getValue() - 72, (float) (y + 10), 5, 12, 2, ColorUtils.getBackgroundColor(4));
    }
    
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtils.isInside(mouseX, mouseY, x - 70, y + 10,renderWidth2 + 3, 10) && mouseButton == 0) {
            dragging = true;
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
    }

    private double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}