/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.clickgui.impl.settings;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lodomir.dev.November;
import lodomir.dev.modules.impl.render.Interface;
import lodomir.dev.settings.Setting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.ui.clickgui.impl.Button;
import lodomir.dev.ui.clickgui.impl.settings.SetBase;
import lodomir.dev.ui.font.TTFFontRenderer;
import lodomir.dev.utils.render.RenderUtils;

public class Slider
extends SetBase {
    public Button parent;
    public double y;
    private TTFFontRenderer fr;
    double x;
    double width;
    NumberSetting mode;
    boolean sliding;
    int height;

    public Slider(Setting set, Button parent) {
        this.fr = November.INSTANCE.fm.getFont("SFR 18");
        this.setting = set;
        this.parent = parent;
    }

    private static double roundToPlace(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public double drawScreen(int mouseX, int mouseY, float partialTicks, double settingHeight) {
        this.height = 12;
        this.mode = (NumberSetting)this.setting;
        this.x = this.parent.parent.x;
        this.width = this.parent.parent.width;
        this.y = settingHeight + this.parent.y + (double)this.parent.height;
        if (this.sliding) {
            double diff = Math.min(90.0, Math.max(0.0, (double)mouseX - this.x));
            double min = this.mode.getMin();
            double max = this.mode.getMax();
            if (this.sliding) {
                if (diff == 0.0) {
                    this.mode.setValue(this.mode.getMin());
                } else {
                    double newValue = Slider.roundToPlace(diff / 90.0 * (max - min) + min);
                    this.mode.setValue(newValue);
                }
            }
        }
        RenderUtils.drawRect(this.x, this.y, this.x + this.width, this.y + (double)this.height, -14342875);
        RenderUtils.drawRect(this.x, this.y, this.x + (this.mode.getValue() - this.mode.getMin()) / (this.mode.getMax() - this.mode.getMin()) * this.width, this.y + (double)this.height, new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()).darker().getRGB());
        this.fr.drawStringWithShadow(this.mode.getName(), this.x + 3.0, this.y + (double)(((float)this.height - this.fr.getHeight(this.mode.getName())) / 2.0f) + 0.5, -1);
        this.fr.drawStringWithShadow(Math.floor(this.mode.getValue() * 1000.0) / 1000.0 + "", this.x + this.width - 3.0 - (double)this.fr.getStringWidth(String.valueOf(Math.floor(this.mode.getValue() * 1000.0) / 1000.0)), this.y + (double)(((float)this.height - this.fr.getHeight(Math.floor(this.mode.getValue() * 1000.0) / 1000.0 + "")) / 2.0f) + 0.5, -1);
        return this.height;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtils.isHovered(mouseX, mouseY, this.x, this.y, this.x + this.width, this.y + (double)this.height)) {
            this.sliding = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.sliding = false;
    }

    @Override
    public boolean isHidden() {
        return !this.setting.isVisible();
    }
}

